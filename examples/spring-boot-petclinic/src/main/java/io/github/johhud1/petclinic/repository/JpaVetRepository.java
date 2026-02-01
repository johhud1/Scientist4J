package io.github.johhud1.petclinic.repository;

import io.github.johhud1.petclinic.model.Vet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Profile("jpa")
@Primary
@Transactional
public class JpaVetRepository implements VetRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Vet> findAll() {
        return entityManager.createQuery(
                "select distinct v from Vet v left join fetch v.specialties order by v.lastName, v.firstName",
                Vet.class)
            .getResultList();
    }

    @Override
    public void saveAll(List<Vet> vets) {
        for (Vet vet : vets) {
            if (vet.getId() == null) {
                entityManager.persist(vet);
            } else {
                entityManager.merge(vet);
            }
        }
    }

    @Override
    public long count() {
        return entityManager.createQuery("select count(v) from Vet v", Long.class)
            .getSingleResult();
    }
}
