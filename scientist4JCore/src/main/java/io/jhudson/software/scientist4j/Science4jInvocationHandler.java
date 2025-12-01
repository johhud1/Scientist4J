package io.jhudson.software.scientist4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.codahale.metrics.MetricRegistry;

import io.jhudson.software.scientist4j.metrics.DropwizardMetricsProvider;

public class Science4jInvocationHandler<T> implements InvocationHandler {

    private final @NonNull T candidate;
    private final @NonNull T control;
    private final DropwizardMetricsProvider metricsProvider;

    // think I have to add @NonNull to these generic T type args because they are generic? not assumed to be non-null?
    public Science4jInvocationHandler(@NonNull T candidate, @NonNull T control, MetricRegistry metricRegistry) {
        this.candidate = candidate;
        this.control = control;
        this.metricsProvider = new DropwizardMetricsProvider(metricRegistry);
    }

    // @SuppressWarnings("nullness")
    @Override
    public @Nullable Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object[] safeArgs = args == null ? new Object[0] : args;
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(control, safeArgs);
        }

        Experiment<@Nullable Object> experiment = new ExperimentBuilder<@Nullable Object>()
                .withName(method.getName())
                .withMetricsProvider(metricsProvider)
                .build();

        Callable<@Nullable Object> controlCallable = callableFor(control, method, safeArgs);
        Callable<@Nullable Object> candidateCallable = callableFor(candidate, method, safeArgs);

        return experiment.run(controlCallable, candidateCallable);
    }

    // TODO: Look into why i have to specify the @NonNull on T target...
    private Callable<@Nullable Object> callableFor(@NonNull T target, Method method, Object[] args) {
        return () -> {
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException invocationTargetException) {
                Throwable cause = invocationTargetException.getCause();
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                }
                if (cause instanceof Error) {
                    throw (Error) cause;
                }
                throw invocationTargetException;
            }
        };
    }
}
