package io.github.johhud1.petclinic.repository;

import io.github.johhud1.petclinic.model.Vet;

import java.util.List;

public interface VetRepository {

    List<Vet> findAll();

    void saveAll(List<Vet> vets);

    long count();
}
