package io.jhudson.software.scientist4j;

import static org.assertj.core.api.Assertions.assertThat;

import io.jhudson.software.scientist4j.metrics.MetricsProvider;

public class TestPublishExperiment<Integer> extends Experiment<Integer> {
  TestPublishExperiment(String name, MetricsProvider<?> metricsProvider) {
    super(name, metricsProvider);
  }

  @Override
  protected void publish(Result<Integer> r) {
    assertThat(r.getCandidate().get().getDuration()).isGreaterThan(0L);
    assertThat(r.getControl().getDuration()).isGreaterThan(0L);
  }

}
