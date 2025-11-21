package io.jhudson.software.scientist4j.metrics;

import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer.Context;

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
        // if(nameComponents == null || nameComponents.length == 0)
        //     throw new IllegalArgumentException("nameComponents must have at least one element");

        // final com.codahale.metrics.Timer timer = registry.timer(MetricRegistry.name(nameComponents[0], Arrays.copyOfRange(nameComponents, 1, nameComponents.length)));
        //TODO: why isn't checkerframework using flow analysis to resolve this to NonNull...
        final com.codahale.metrics.Timer timer = registry.timer(MetricRegistry.name("foobar"));

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
        if(nameComponents == null || nameComponents.length == 0)
            throw new IllegalArgumentException("nameComponents must have at least one element");

        String nameOne = nameComponents[0] == null ? "null" : nameComponents[0];
        @NonNull String[] nameComponentsNonNull = validateVargs(Arrays.copyOfRange(nameComponents, 1, nameComponents.length));
        final com.codahale.metrics.Counter counter = registry
            .counter(MetricRegistry.name(nameOne, nameComponentsNonNull));

        return new Counter() {

            @Override
            public void increment() {
                counter.inc();
            }
        };
    }

    //TODO: this was a nightmare to get working. There's got to be a better way to do this...
    // converting between string.. varargs and string[] seems like a challenge.. need to find nicer pattern
    private @NonNull String @NonNull[] validateVargs(@Nullable String[] nameComponents) {
        if(nameComponents == null)
            throw new IllegalArgumentException("nameComponents must have at least one element");

        @NonNull String @NonNull [] nameComponentsNonNull = new String[nameComponents.length];
        for(int i = 0; i < nameComponents.length; i++) {
            nameComponentsNonNull[i] = nameComponents[i] == null ? "null" : nameComponents[i];
        }
        return nameComponentsNonNull;
    }

    @Override
    public MetricRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public void setRegistry(MetricRegistry registry) {
        this.registry = registry;
    }
}
