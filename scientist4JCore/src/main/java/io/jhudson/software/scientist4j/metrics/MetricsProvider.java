package io.jhudson.software.scientist4j.metrics;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface MetricsProvider<T> {

    Timer timer(@NonNull String... nameComponents);

    Counter counter(@NonNull String... nameComponents);

    interface Timer {

        void record(Runnable runnable);

        /**
         * The duration recorded by this timer
         *
         * @return timer duration in nanoseconds
         */
        long getDuration();
    }

    interface Counter {

        void increment();
    }

    T getRegistry();

    void setRegistry(T registry);
}
