package io.jhudson.software.scientist4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.jhudson.software.scientist4j.exceptions.MismatchException;
import io.jhudson.software.scientist4j.metrics.MetricsProvider;

public class Experiment<@Nullable T> {

    private final ExecutorService executor;
    private static final String NAMESPACE_PREFIX = "scientist";
    private final MetricsProvider<?> metricsProvider;
    private final String name;
    private final boolean raiseOnMismatch;
    private Map<String, Object> context;
    private final MetricsProvider.Timer controlTimer;
    private final MetricsProvider.Timer candidateTimer;
    private final MetricsProvider.Counter mismatchCount;
    private final MetricsProvider.Counter candidateExceptionCount;
    private final MetricsProvider.Counter totalCount;
    private final BiFunction<T, T, Boolean> comparator;

    public Experiment(MetricsProvider<?> metricsProvider) {
        this("Experiment", metricsProvider);
    }

    public Experiment(String name, MetricsProvider<?> metricsProvider) {
        this(name, false, metricsProvider);
    }

    public Experiment(String name, Map<String, Object> context, MetricsProvider<?> metricsProvider) {
        this(name, context, false, metricsProvider);
    }

    public Experiment(String name, boolean raiseOnMismatch, MetricsProvider<?> metricsProvider) {
        this(name, new HashMap<>(), raiseOnMismatch, metricsProvider);
    }

    public Experiment(String name, Map<String, Object> context, boolean raiseOnMismatch, MetricsProvider<?> metricsProvider) {
        this(name, context, raiseOnMismatch, metricsProvider, Objects::equals);
    }

    public Experiment(String name, Map<String, Object> context, boolean raiseOnMismatch,
                      MetricsProvider<?> metricsProvider, BiFunction<T, T, Boolean> comparator) {
        this(name, context, raiseOnMismatch, metricsProvider, comparator, Executors.newFixedThreadPool(2));
    }

    public Experiment(String name, Map<String, Object> context, boolean raiseOnMismatch,
                      MetricsProvider<?> metricsProvider, BiFunction<T, T, Boolean> comparator,
                      ExecutorService executorService) {
        this.name = name;
        this.context = context;
        this.raiseOnMismatch = raiseOnMismatch;
        this.comparator = comparator;
        this.metricsProvider = metricsProvider;
        controlTimer = metricsProvider.timer(NAMESPACE_PREFIX, this.name, "control");
        candidateTimer = metricsProvider.timer(NAMESPACE_PREFIX, this.name, "candidate");
        mismatchCount = metricsProvider.counter(NAMESPACE_PREFIX, this.name, "mismatch");
        candidateExceptionCount = metricsProvider.counter(NAMESPACE_PREFIX, this.name, "candidate.exception");
        totalCount = metricsProvider.counter(NAMESPACE_PREFIX, this.name, "total");
        executor = executorService;
    }

    /**
     * Allow override here if extending the class
     */
    public MetricsProvider<?> getMetricsProvider() {
        return this.metricsProvider;
    }

    /**
     * Note that if {@code raiseOnMismatch} is true, {@link #runAsync(Callable, Callable)} will block waiting for
     * the candidate function to complete before it can raise any resulting errors. In situations where the candidate
     * function may be significantly slower than the control, it is <em>not</em> recommended to raise on mismatch.
     */
    public boolean getRaiseOnMismatch() {
        return raiseOnMismatch;
    }

    public String getName() {
        return name;
    }

    public T run(Callable<T> control, Callable<T> candidate) throws Exception {
        if (isAsyncCandidateOnly()) {
            return runAsyncCandidateOnly(control, candidate);
        } else if (isAsync()) {
            return runAsync(control, candidate);
        } else {
            return runSync(control, candidate);
        }
    }

    private T runSync(Callable<T> control, Callable<T> candidate) throws Exception {
        Observation<T> controlObservation;
        @Nullable Observation<T> candidateObservation = null;
        if (Math.random() < 0.5) {
            controlObservation = executeResult("control", controlTimer, control, true);
            if (runIf() && enabled()) {
                candidateObservation = executeResult("candidate", candidateTimer, candidate, false);
            }
        } else {
            if (runIf() && enabled()) {
                candidateObservation = executeResult("candidate", candidateTimer, candidate, false);
            }
            controlObservation = executeResult("control", controlTimer, control, true);
        }

        countExceptions(candidateObservation, candidateExceptionCount);
        Result<T> result = new Result<>(this, controlObservation, candidateObservation, context);
        publish(result);
        return controlObservation.getValue();
    }

    public T runAsync(Callable<T> control, Callable<T> candidate) throws Exception {
        Future<Observation<T>> observationFutureCandidate;
        Future<Observation<T>> observationFutureControl;

        if (runIf() && enabled()) {
            if (Math.random() < 0.5) {
                observationFutureControl = executor.submit(() -> executeResult("control", controlTimer, control, true));
                observationFutureCandidate = executor.submit(() -> executeResult("candidate", candidateTimer, candidate, false));
            } else {
                observationFutureCandidate = executor.submit(() -> executeResult("candidate", candidateTimer, candidate, false));
                observationFutureControl = executor.submit(() -> executeResult("control", controlTimer, control, true));
            }
        } else {
            observationFutureControl = executor.submit(() -> executeResult("control", controlTimer, control, true));
            observationFutureCandidate = null;
        }

        Observation<T> controlObservation;
        try {
            controlObservation = observationFutureControl.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        Future<Void> publishedResult = executor.submit(() -> publishAsync(controlObservation, observationFutureCandidate));

        if (raiseOnMismatch) {
            try {
                publishedResult.get();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                }
                throw new RuntimeException(cause);
            }
        }

        return controlObservation.getValue();
    }

    public T runAsyncCandidateOnly(Callable<T> control, Callable<T> candidate) throws Exception {
        Future<Observation<T>> observationFutureCandidate;
        Observation<T> controlObservation;

        if (runIf() && enabled()) {
            if (Math.random() < 0.5) {
                observationFutureCandidate = executor.submit(() -> executeResult("candidate", candidateTimer, candidate, false));
                controlObservation = executeResult("control", controlTimer, control, true);
            } else {
                controlObservation = executeResult("control", controlTimer, control, true);
                observationFutureCandidate = executor.submit(() -> executeResult("candidate", candidateTimer, candidate, false));
            }
        } else {
            controlObservation = executeResult("control", controlTimer, control, true);
            observationFutureCandidate = null;
        }

        Future<Void> publishedResult = executor.submit(() -> publishAsync(controlObservation, observationFutureCandidate));

        if (raiseOnMismatch) {
            try {
                publishedResult.get();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                }
                throw new RuntimeException(cause);
            }
        }

        return controlObservation.getValue();
    }

    private Void publishAsync(Observation<T> controlObservation, @Nullable Future<Observation<T>> observationFutureCandidate) throws Exception {
        @Nullable Observation<T> candidateObservation = null;
        if (observationFutureCandidate != null) {
            candidateObservation = observationFutureCandidate.get();
        }

        countExceptions(candidateObservation, candidateExceptionCount);
        Result<T> result = new Result<>(this, controlObservation, candidateObservation, context);
        publish(result);
        return null;
    }

    private void countExceptions(@Nullable Observation<T> observation, MetricsProvider.Counter exceptions) {
        if (observation != null && observation.getException() != null) {
            exceptions.increment();
        }
    }

    public Observation<T> executeResult(String name, MetricsProvider.Timer timer, Callable<T> control, boolean shouldThrow) throws Exception {
        Observation<T> observation = new Observation<>(name, timer);

        observation.time(() -> {
            try {
                observation.setValue(control.call());
            } catch (Exception e) {
                observation.setException(e);
            }
        });

        @Nullable Exception exception = observation.getException();
        if (shouldThrow && exception != null) {
            throw exception;
        }

        return observation;
    }

    protected boolean compareResults(T controlVal, T candidateVal) {
        return comparator.apply(controlVal, candidateVal);
    }

    public boolean compare(Observation<T> controlVal, Observation<T> candidateVal) throws MismatchException {
        boolean resultsMatch = candidateVal.getException() == null && compareResults(controlVal.getValue(), candidateVal.getValue());
        totalCount.increment();
        if (!resultsMatch) {
            mismatchCount.increment();
            handleComparisonMismatch(controlVal, candidateVal);
        }
        return true;
    }

    protected void publish(Result<T> r) {
    }

    protected boolean runIf() {
        return true;
    }

    protected boolean enabled() {
        return true;
    }

    protected boolean isAsync() {
        return false;
    }

    protected boolean isAsyncCandidateOnly() {
        return false;
    }

    private void handleComparisonMismatch(Observation<T> controlVal, Observation<T> candidateVal) throws MismatchException {
        String msg;
        Exception exception = candidateVal.getException();
        if (exception != null) {
            String stackTrace = exception.getStackTrace().toString();
            String exceptionName = exception.getClass().getName();
            msg = new StringBuilder().append(candidateVal.getName()).append(" raised an exception: ")
                .append(exceptionName).append(" ").append(stackTrace).toString();
        } else {
            msg = new StringBuilder().append(candidateVal.getName()).append(" does not match control value (")
                .append(controlVal.getValueString()).append(" != ").append(candidateVal.getValueString()).append(")").toString();
        }
        throw new MismatchException(msg);
    }
}
