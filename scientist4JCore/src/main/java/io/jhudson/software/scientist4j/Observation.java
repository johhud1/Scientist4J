package io.jhudson.software.scientist4j;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.jhudson.software.scientist4j.metrics.MetricsProvider.Timer;

public class Observation<T> {

    private final String name;
    private final Timer timer;
    private @Nullable Exception exception;
    private @Nullable T value;

    public Observation(String name, Timer timer) {
        this.name = name;
        this.timer = timer;
        this.exception = null;
    }

    public String getName() {
        return name;
    }

    public void setValue(@Nullable T o) {
        this.value = o;
    }

    public @Nullable T getValue() {
        return value;
    }

    public String getValueString() {
        return value == null ? "[null]" : value.toString();
    }

    public void setException(Exception e) {
        this.exception = e;
    }

    public @Nullable Exception getException() {
        return exception;
    }

    public long getDuration() {
        return timer.getDuration();
    }

    public void time(Runnable runnable) {
        timer.record(runnable);
    }
}
