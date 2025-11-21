package io.jhudson.software.scientist4j;

import io.jhudson.software.scientist4j.metrics.MetricsProvider.Timer;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Observation<T> {

    private String name;
    private @Nullable Exception exception;
    private T value;
    private Timer timer;

    public Observation(String name, Timer timer) {
        this.name = name;
        this.timer = timer;
        this.exception = null;
    }

    public String getName() {
        return name;
    }

    public void setValue(T o) {
        this.value = o;
    }

    public T getValue() {
        return value;
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
