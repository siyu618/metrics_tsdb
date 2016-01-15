# metrics_tsdb

1. install 
   http://opentsdb.net/docs/build/html/installation.html#id1
   http://docs.grafana.org/installation/

2. when run , got error: 
   2016-01-01 11:50:04,178 INFO  [OpenTSDB I/O Worker #15-EventThread] ClientCnxn: EventThread shut down
   2016-01-01 11:50:04,180 INFO  [AsyncHBase I/O Worker #11] HBaseClient: Added client for region RegionInfo(table="tsdb-uid", region_name="tsdb-uid,,1451603848967.067c64dc8ec5ac1d19e58d44e2db5e15.", stop_key=""), which was added to the regions cache.  Now we know that RegionClient@1859601858(chan=[id: 0x24596eb6, /10.111.0.108:39001 => /10.111.0.108:21952], #pending_rpcs=0, #batched=0, #rpcs_inflight=0) is hosting 1 region.
   2016-01-01 11:50:04,182 WARN  [OpenTSDB I/O Worker #15] PutDataPointRpc: Unknown metric: metric=push_notification.online_generator_call.qps ts=13521389587 value=1 host=10.101.2.2 component=push_notification
   2016-01-01 11:50:04,184 INFO  [OpenTSDB I/O Worker #15] HttpQuery: [id: 0xe4a76841, /10.101.2.2:14241 => /10.111.0.108:4242] HTTP /api/put done in 16ms

3. usage : 
   * Meter: 用来实现qps等计数类的metrics，最终数据的单位是event/sec.
     // 每个StaticChannel的query执行一次下面的语句
     MetricsFactoryUtil.getRegisteredFactory().getMeter("YourService.qps").mark();
   * Histogram: 用来统计latency等分布数据，也可以用来实现error_rate一类的数据.
     // latency的例子
     long tsBegin = System.currentTimeMillis();
     ...
     long tsEnd = System.currentTimeMillis();
     MetricsFactoryUtil.getRegisteredFactory().getHistogram("YourService.latency").update(tsEnd-tsBegin);
     // error_rate的例子
     try {
       ..
       MetricsFactoryUtil.getRegisteredFactory().getHistogram("YourService.error").update(0);
     } catch(Exception) {
       MetricsFactoryUtil.getRegisteredFactory().getHistogram("YourService.error").update(100);
     }
   * Gauge: 用来实现某些状态的监控，比如cache size.
     cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(10000).build();
     MetricsFactoryUtil.getRegisteredFactory().register(new Gauge("torsonews.UserNewsCandidatesCache.size") {
         @Override
         public Number getValue() {
             return cache.size();
         }
     });     
4. 配置
   * 每个server配置文件的格式可能是不一样的,配置文件中需要server的tag信息以及opentsdb的地址.
     // config file example
     ...
     serving-metrics {
       opentsdb.address = "http://test1-2.kafka.yidian.com:14242/api/put"
       tags = {
         host = "serving1-9.yidian.com"
         colo = "lugu"
         component = "channel-serving"
       }
     }
   * 读取config并注册MetricsFactory的代码只需要在server启动的时候运行一次,注册需要在使用metrics之前进行.
     // read config
     Config config = ConfigFactory.load();
     Config servingMetricsConfig;
     try {
         servingMetricsConfig = config.getConfig("serving-metrics");
     } catch (Exception e) {
         logger.info("serving-metrics config is not found, will not send serving metrics");
         return;
     }
     String openTsdbAddr = servingMetricsConfig.getString("opentsdb.address");
     Config tagsConfig = servingMetricsConfig.getConfig("tags");
     Map tags = Maps.newHashMap();
     for (Map.Entry entry : tagsConfig.entrySet()) {
         tags.put(entry.getKey(), entry.getValue().unwrapped().toString());
     }

     AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
     OpenTsdbClient openTsdbClient = new HttpOpenTsdbClient(asyncHttpClient, openTsdbAddr);
     MetricsFactory metricsFactory = new OnDemandMetricsFactory(tags, openTsdbClient);
     MetricsFactoryUtil.register(metricsFactory);