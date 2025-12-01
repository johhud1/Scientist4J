package io.jhudson.software.scientist4j;

import io.jhudson.software.scientist4j.metrics.MetricsProvider;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

public class ExperimentBuilder<T> {
    private @Nullable String name;
    private @Nullable MetricsProvider<?> metricsProvider;
    private BiFunction<T, T, Boolean> comparator;
    private Map<String, Object> context;
    private ExecutorService executorService;

    public ExperimentBuilder() {
        context = new HashMap<>();
        comparator = Objects::equals;
        executorService = Executors.newFixedThreadPool(2);
    }

    public ExperimentBuilder<T> withName(final String name) {
        this.name = name;
        return this;
    }

    public ExperimentBuilder<T> withMetricsProvider(final MetricsProvider<?> metricsProvider) {
        this.metricsProvider = metricsProvider;
        return this;
    }

    public ExperimentBuilder<T> withComparator(final BiFunction<T, T, Boolean> comparator) {
        this.comparator = comparator;
        return this;
    }

    public ExperimentBuilder<T> withExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public Experiment<T> build() {
        if (name == null) {
            throw new IllegalStateException("Experiment name must be set");
        }
        if (metricsProvider == null) {
            throw new IllegalStateException("Metrics provider must be set");
        }
        return new Experiment<>(name, context, false, metricsProvider, comparator,
            executorService);
    }
}
