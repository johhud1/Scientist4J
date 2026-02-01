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

    public PetClinicService(OwnerRepository ownerRepository, VetRepository vetRepository) {
        this.ownerRepository = ownerRepository;
        this.vetRepository = vetRepository;  
    }

    public List<Owner> findOwners() {
        return ownerRepository.findAll();
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
