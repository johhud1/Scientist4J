package io.github.johhud1.petclinic.service;

import io.github.johhud1.petclinic.model.Owner;
import io.github.johhud1.petclinic.model.Vet;
import io.github.johhud1.petclinic.repository.OwnerRepository;
import io.github.johhud1.petclinic.repository.VetRepository;
import io.jhudson.software.scientist4j.Experiment;
import io.jhudson.software.scientist4j.ExperimentBuilder;
import io.github.johhud1.petclinic.metrics.SimpleMetricsProvider;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetClinicService {

    private final OwnerRepository ownerRepository;
    private final VetRepository vetRepository;
    private final Experiment<List<Owner>> ownersExperiment;

    public PetClinicService(OwnerRepository ownerRepository, VetRepository vetRepository) {
        this.ownerRepository = ownerRepository;
        this.vetRepository = vetRepository;
        ownersExperiment = new ExperimentBuilder<List<Owner>>()
            .withName("findOwners")
            .withMetricsProvider(new SimpleMetricsProvider())
            .build();
    }

    public List<Owner> findOwners() {
        try {
            return ownersExperiment.run(ownerRepository::findAll, ownerRepository::findAll);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to run experiment", e);
        }
    }

    public @Nullable Owner findOwnerById(Long id) {
        return ownerRepository.findById(id);
    }

    public @Nullable Owner findOwnerByLastName(String lastName) {
        return ownerRepository.findByLastName(lastName);
    }

    public List<Vet> findVets() {
        return vetRepository.findAll();
    }
}
