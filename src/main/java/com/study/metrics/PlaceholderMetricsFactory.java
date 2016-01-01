package com.study.metrics;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.study.metrics.core.Gauge;
import com.study.metrics.core.Histogram;
import com.study.metrics.core.Meter;


/**
 * A trivial MetricsFactory as Placeholder.
 * @author jixu
 */
public class PlaceholderMetricsFactory implements MetricsFactory {
    private static final Histogram placeholderHistogram =
            new Histogram("placeholderHisto", new com.codahale.metrics.Histogram(new ExponentiallyDecayingReservoir()));
    private static final Meter placeholderMeter =
            new Meter("placeholderMeter", new com.codahale.metrics.Meter());

    public Histogram getHistogram(String name) {
        return placeholderHistogram;
    }

    public Meter getMeter(String name) {
        return placeholderMeter;
    }

    public <T extends Number> boolean register(Gauge<T> gauge) { return true; }
}
