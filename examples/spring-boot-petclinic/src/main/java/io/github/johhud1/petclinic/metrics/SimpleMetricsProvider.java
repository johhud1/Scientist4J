package io.github.johhud1.petclinic.metrics;

import io.jhudson.software.scientist4j.metrics.MetricsProvider;

public final class SimpleMetricsProvider implements MetricsProvider<Void> {

    @Override
    public Timer timer(String... nameComponents) {
        return new Timer() {

            private long duration;

            @Override
            public void record(Runnable runnable) {
                long start = System.nanoTime();
                runnable.run();
                duration = System.nanoTime() - start;
            }

            @Override
            public long getDuration() {
                return duration;
            }
        };
    }

    @Override
    public Counter counter(String... nameComponents) {
        return () -> {
        };
    }

    @Override
    public Void getRegistry() {
        return null;
    }

    @Override
    public void setRegistry(Void registry) {
    }
}
