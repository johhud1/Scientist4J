package io.github.johhud1.petclinic.repository;

import io.github.johhud1.petclinic.model.Owner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Profile("jpa")
@Primary
@Transactional
public class JpaOwnerRepository implements OwnerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Owner> findAll() {
        return entityManager.createQuery(
                "select o from Owner o order by o.lastName, o.firstName",
                Owner.class)
            .getResultList();
    }

    @Override
    public @Nullable Owner findById(Long id) {
        return entityManager.find(Owner.class, id);
    }

    @Override
    public @Nullable Owner findByLastName(String lastName) {
        List<Owner> owners = entityManager.createQuery(
                "select o from Owner o where lower(o.lastName) = lower(:lastName)",
                Owner.class)
            .setParameter("lastName", lastName)
            .setMaxResults(1)
            .getResultList();
        return owners.isEmpty() ? null : owners.getFirst();
    }

    @Override
    public void saveAll(List<Owner> owners) {
        for (Owner owner : owners) {
            if (owner.getId() == null) {
                entityManager.persist(owner);
            } else {
                entityManager.merge(owner);
            }
        }
    }

    @Override
    public long count() {
        return entityManager.createQuery("select count(o) from Owner o", Long.class)
            .getSingleResult();
    }
}
