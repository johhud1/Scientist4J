package io.jhudson.software.scientist4j;

import static org.assertj.core.api.Assertions.assertThat;

import io.jhudson.software.scientist4j.metrics.MetricsProvider;

public class TestPublishExperiment extends Experiment<Integer> {

    TestPublishExperiment(String name, MetricsProvider<?> metricsProvider) {
        super(name, metricsProvider);
    }

    @Override
    protected void publish(Result<Integer> result) {
        Observation<Integer> candidate = result.getCandidate();
        assertThat(candidate).isNotNull();
        if (candidate != null) {
            assertThat(candidate.getDuration()).isGreaterThan(0L);
        }
        assertThat(result.getControl().getDuration()).isGreaterThan(0L);
    }
}
