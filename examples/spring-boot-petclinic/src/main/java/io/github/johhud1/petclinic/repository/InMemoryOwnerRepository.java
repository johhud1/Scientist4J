package io.github.johhud1.petclinic.repository;

import io.github.johhud1.petclinic.model.Owner;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryOwnerRepository implements OwnerRepository {

    private final ConcurrentHashMap<Long, Owner> owners;
    private final AtomicLong idSequence;

    public InMemoryOwnerRepository() {
        owners = new ConcurrentHashMap<>();
        idSequence = new AtomicLong(1);
    }

    public Owner save(Owner owner) {
        if (owner.getId() == null) {
            owner.setId(idSequence.getAndIncrement());
        }
        owners.put(owner.getId(), owner);
        return owner;
    }

    @Override
    public List<Owner> findAll() {
        return owners.values().stream()
            .sorted(Comparator.comparing(Owner::getLastName).thenComparing(Owner::getFirstName))
            .toList();
    }

    @Override
    public @Nullable Owner findById(Long id) {
        return owners.get(id);
    }

    @Override
    public @Nullable Owner findByLastName(String lastName) {
        return owners.values().stream()
            .filter(owner -> owner.getLastName().equalsIgnoreCase(lastName))
            .findFirst()
            .orElse(null);
    }

    public void deleteAll() {
        owners.clear();
    }

    public void saveAll(List<Owner> ownersToSave) {
        ownersToSave.forEach(this::save);
    }
}
