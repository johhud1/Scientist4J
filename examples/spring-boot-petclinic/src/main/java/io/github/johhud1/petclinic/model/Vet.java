package io.github.johhud1.petclinic.model;

import java.util.HashSet;
import java.util.Set;

public class Vet {

    private Long id;
    private String firstName;
    private String lastName;
    private final Set<String> specialties;

    public Vet() {
        specialties = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<String> getSpecialties() {
        return specialties;
    }

    public void addSpecialty(String specialty) {
        specialties.add(specialty);
    }
}

