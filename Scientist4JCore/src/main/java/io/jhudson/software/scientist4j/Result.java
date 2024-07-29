package io.jhudson.software.scientist4j;

import java.util.Map;
import java.util.Optional;

import io.jhudson.software.scientist4j.exceptions.MismatchException;

public class Result<T> {
    private Experiment experiment;
    private Observation control;
    private Optional<Observation<T>> candidate;
    private Optional<Boolean> match;
    private Map<String, Object> context;

    public Result(Experiment experiment, Observation<T> control, Optional<Observation<T>> candidate, Map<String, Object> context) throws MismatchException {
      this.experiment = experiment;
      this.control = control;
      this.candidate = candidate;
      this.context = context;
      this.match = Optional.empty();

      if (candidate.isPresent()) {
        Optional<MismatchException> ex = Optional.empty();
        try {
          this.match = Optional.of(experiment.compare(control, candidate.get()));
        } catch (MismatchException e) {
          ex = Optional.of(e);
          this.match = Optional.of(false);
        } finally {
          if (experiment.getRaiseOnMismatch() && ex.isPresent()) {
            throw ex.get();
          }
        }
      }
    }

    public Optional<Boolean> getMatch() {
      return match;
    }

    public Observation<T> getControl() {
      return control;
    }

    public Optional<Observation<T>> getCandidate() {
      return candidate;
    }

    public Map<String, Object> getContext() {
      return context;
    }
}
