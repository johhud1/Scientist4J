package io.jhudson.software.scientist4j;

import java.util.function.BiPredicate;

import io.jhudson.software.scientist4j.metrics.MetricsProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class TestPublishIncompatibleTypesExperiment extends IncompatibleTypesExperiment<Integer, String> {
    TestPublishIncompatibleTypesExperiment(String name, MetricsProvider<?> metricsProvider, BiPredicate<Integer, String> comparator) {
        super(name, metricsProvider, comparator);
    }

    @Override
    protected void publish(IncompatibleTypesExperimentResult<Integer, String> result) {
        assertThat(result.getCandidate().getDuration()).isGreaterThan(0L);
        assertThat(result.getControl().getDuration()).isGreaterThan(0L);
    }
}
