package io.jhudson.software.scientist4j;

import io.jhudson.software.scientist4j.exceptions.MismatchException;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

public class Result<T> {
    private Experiment experiment;
    private Observation control;
    private @Nullable Observation<T> candidate;
    private @Nullable Boolean match;
    private Map<String, Object> context;

    public Result(Experiment experiment, Observation<T> control, @Nullable Observation<T> candidate, Map<String, Object> context) throws MismatchException {
        this.experiment = experiment;
        this.control = control;
        this.candidate = candidate;
        this.context = context;
        this.match = null;

        if (candidate != null) {
            MismatchException mismatchException = null;
            try {
                this.match = experiment.compare(control, candidate);
            } catch (MismatchException e) {
                mismatchException = e;
                this.match = false;
            }
            if (experiment.getRaiseOnMismatch() && mismatchException != null) {
                throw mismatchException;
            }
        }
    }

    public @Nullable Boolean getMatch() {
        return match;
    }

    public Observation<T> getControl() {
        return control;
    }

    public @Nullable Observation<T> getCandidate() {
        return candidate;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
