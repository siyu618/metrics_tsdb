package com.study.metrics.daemon;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.providers.jdk.JDKAsyncHttpProvider;
import com.study.metrics.MetricsFactory;
import com.study.metrics.MetricsFactoryUtil;
import com.study.metrics.OnDemandMetricsFactory;
import com.study.metrics.reporter.opentsdb.HttpOpenTsdbClient;
import com.study.metrics.reporter.opentsdb.OpenTsdbClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianyuzhi on 16/1/1.
 */
public class Daemon {

    private static final String opentsdbAddress = "http://10.111.0.108:4242/api/put";
    private static final Map<String, String> tags = new HashMap<>();
    private static final String QPS = "metrics.test.qps";
    private static final String LATENCY = "metrics.test.latency";
    private static final String ERR = "metrics.test.error";
    static {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        System.setProperty("log4j.configuration", "src/main/resources/config/log4j_debug.properties");
        try {
            tags.put("host", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public static void init() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(new JDKAsyncHttpProvider(new AsyncHttpClientConfig.Builder().build()));
        OpenTsdbClient openTsdbClient = new HttpOpenTsdbClient(asyncHttpClient, opentsdbAddress);
        MetricsFactory metricsFactory = new OnDemandMetricsFactory(tags, openTsdbClient);
        MetricsFactoryUtil.register(metricsFactory);
    }
    public static void main(String[] args) throws InterruptedException {
        init();
        int n = 10000;
        for (int i = 0; i < n; i ++) {
            MetricsFactoryUtil.getRegisteredFactory().getMeter(QPS).mark();
            long start = System.currentTimeMillis();
            boolean runSuccessfully = true;//doSomething();
            long end = System.currentTimeMillis();
            MetricsFactoryUtil.getRegisteredFactory().getHistogram(LATENCY).update(end - start);
            if (runSuccessfully) {
                MetricsFactoryUtil.getRegisteredFactory().getHistogram(ERR).update(0);
            }
            else {
                MetricsFactoryUtil.getRegisteredFactory().getHistogram(ERR).update(100);
            }
        }
        Thread.sleep(3000 * 1000);

    }
}
