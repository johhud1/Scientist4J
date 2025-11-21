package io.jhudson.software.scientist4j.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer.Context;

import java.util.Arrays;

public class DropwizardMetricsProvider implements MetricsProvider<MetricRegistry> {

    private MetricRegistry registry;

    public DropwizardMetricsProvider() {
        this(new MetricRegistry());
    }

    public DropwizardMetricsProvider(MetricRegistry metricRegistry) {
        this.registry = metricRegistry;
    }

    @Override
    public Timer timer(String... nameComponents) {
        final com.codahale.metrics.Timer timer = registry.timer(MetricRegistry.name(primary(nameComponents), remainder(nameComponents)));

        return new Timer() {

            long duration;

            @Override
            public void record(Runnable runnable) {

                final Context context = timer.time();

                try {
                    runnable.run();
                } finally {
                    duration = context.stop();
                }
            }

            @Override
            public long getDuration() {
                return duration;
            }
        };
    }

    @Override
    public Counter counter(String... nameComponents) {

        final com.codahale.metrics.Counter counter = registry.counter(MetricRegistry.name(primary(nameComponents), remainder(nameComponents)));

        return new Counter() {

            @Override
            public void increment() {
                counter.inc();
            }
        };
    }

    @Override
    public MetricRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public void setRegistry(MetricRegistry registry) {
        this.registry = registry;
    }

    private String primary(String... nameComponents) {
        if (nameComponents.length == 0) {
            throw new IllegalArgumentException("Metric name must have at least one component");
        }
        String primary = nameComponents[0];
        if (primary == null) {
            throw new IllegalArgumentException("Metric name cannot be null");
        }
        return primary;
    }

    private String[] remainder(String... nameComponents) {
        if (nameComponents.length <= 1) {
            return new String[]{};
        }
        if (nameComponents.length <= 1) {
            return new String[0];
        }
        String[] tail = new String[nameComponents.length - 1];
        for (int i = 0; i < tail.length; i++) {
            String value = nameComponents[i + 1];
            tail[i] = value == null ? "" : value;
        }
        return tail;
    }
}
