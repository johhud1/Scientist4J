package io.github.johhud1.petclinic.repository;

import io.github.johhud1.petclinic.model.Vet;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("memory")
@Primary
public class InMemoryVetRepository implements VetRepository {

    private final List<Vet> vets;
    private final AtomicLong idSequence;

    public InMemoryVetRepository() {
        vets = new ArrayList<>();
        idSequence = new AtomicLong(1);
    }

    public Vet save(Vet vet) {
        if (vet.getId() == null) {
            vet.setId(idSequence.getAndIncrement());
        }
        vets.removeIf(existingVet -> existingVet.getId().equals(vet.getId()));
        vets.add(vet);
        vets.sort(Comparator.comparing(Vet::getLastName).thenComparing(Vet::getFirstName));
        return vet;
    }

    @Override
    public void saveAll(List<Vet> vetsToSave) {
        for (Vet vet : vetsToSave) {
            save(vet);
        }
    }

    @Override
    public long count() {
        return vets.size();
    }

    @Override
    public List<Vet> findAll() {
        return List.copyOf(vets);
    }

    public void deleteAll() {
        vets.clear();
    }
}
