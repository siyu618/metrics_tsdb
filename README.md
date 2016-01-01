# metrics_tsdb

1. install 
http://opentsdb.net/docs/build/html/installation.html#id1
http://docs.grafana.org/installation/

2. when run , got error: 
2016-01-01 11:50:04,178 INFO  [OpenTSDB I/O Worker #15-EventThread] ClientCnxn: EventThread shut down
2016-01-01 11:50:04,180 INFO  [AsyncHBase I/O Worker #11] HBaseClient: Added client for region RegionInfo(table="tsdb-uid", region_name="tsdb-uid,,1451603848967.067c64dc8ec5ac1d19e58d44e2db5e15.", stop_key=""), which was added to the regions cache.  Now we know that RegionClient@1859601858(chan=[id: 0x24596eb6, /10.111.0.108:39001 => /10.111.0.108:21952], #pending_rpcs=0, #batched=0, #rpcs_inflight=0) is hosting 1 region.
2016-01-01 11:50:04,182 WARN  [OpenTSDB I/O Worker #15] PutDataPointRpc: Unknown metric: metric=push_notification.online_generator_call.qps ts=13521389587 value=1 host=10.101.2.2 component=push_notification
2016-01-01 11:50:04,184 INFO  [OpenTSDB I/O Worker #15] HttpQuery: [id: 0xe4a76841, /10.101.2.2:14241 => /10.111.0.108:4242] HTTP /api/put done in 16ms