package io.github.johhud1.petclinic.repository;

import io.github.johhud1.petclinic.model.Owner;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public interface OwnerRepository {

    List<Owner> findAll();

    @Nullable Owner findById(Long id);

    @Nullable Owner findByLastName(String lastName);

    void saveAll(List<Owner> owners);

    long count();
}
