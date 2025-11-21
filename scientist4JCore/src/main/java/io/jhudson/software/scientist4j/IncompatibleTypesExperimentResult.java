package io.jhudson.software.scientist4j;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.jhudson.software.scientist4j.exceptions.MismatchException;

/**
 * @param <T> The return type of the control function
 * @param <U> The return type of the candidate function.
 */
public class IncompatibleTypesExperimentResult<@Nullable T, @Nullable U> {
    private final Observation<T> control;
    private final @Nullable Observation<U> candidate;
    private @Nullable Boolean match;
    private final Map<String, Object> context;

    public IncompatibleTypesExperimentResult(final IncompatibleTypesExperiment<T, U> experiment, final Observation<T> control,
                                             final @Nullable Observation<U> candidate, final Map<String, Object> context) throws MismatchException {
        this.control = control;
        this.candidate = candidate;
        this.context = context;
        this.match = null;

        if (candidate != null) {
            try {
                this.match = experiment.compare(control, candidate);
            } catch (MismatchException e) {
                this.match = false;
                throw e;
            }
        }
    }

    public @Nullable Boolean getMatch() {
        return match;
    }

    public Observation<T> getControl() {
        return control;
    }

    public @Nullable Observation<U> getCandidate() {
        return candidate;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
