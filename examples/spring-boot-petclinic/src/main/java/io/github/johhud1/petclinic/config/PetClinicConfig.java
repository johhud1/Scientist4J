package io.github.johhud1.petclinic.config;

import io.github.johhud1.petclinic.model.Owner;
import io.github.johhud1.petclinic.model.Pet;
import io.github.johhud1.petclinic.model.PetType;
import io.github.johhud1.petclinic.model.Vet;
import io.github.johhud1.petclinic.repository.InMemoryOwnerRepository;
import io.github.johhud1.petclinic.repository.InMemoryVetRepository;
import io.github.johhud1.petclinic.repository.OwnerRepository;
import io.github.johhud1.petclinic.repository.VetRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class PetClinicConfig {

    @Bean
    public OwnerRepository ownerRepository() {
        InMemoryOwnerRepository repository = new InMemoryOwnerRepository();
        repository.saveAll(sampleOwners());
        return repository;
    }

    @Bean
    public VetRepository vetRepository() {
        InMemoryVetRepository repository = new InMemoryVetRepository();
        repository.saveAll(sampleVets());
        return repository;
    }

    private List<Owner> sampleOwners() {
        Owner george = createOwner("George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
        addPet(george, "Rosy", PetType.CAT, LocalDate.of(2016, 4, 17));
        addPet(george, "Jewel", PetType.DOG, LocalDate.of(2019, 8, 21));

        Owner betty = createOwner("Betty", "Davis", "638 Cardinal Ave.", "Sun Prairie", "6085551749");
        addPet(betty, "Lucky", PetType.DOG, LocalDate.of(2015, 6, 24));

        Owner eduardo = createOwner("Eduardo", "Rodriquez", "2693 Commerce St.", "McFarland", "6085558763");
        addPet(eduardo, "Samantha", PetType.HAMSTER, LocalDate.of(2020, 1, 5));

        return List.of(george, betty, eduardo);
    }

    private Owner createOwner(String firstName, String lastName, String address, String city, String telephone) {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress(address);
        owner.setCity(city);
        owner.setTelephone(telephone);
        return owner;
    }

    private void addPet(Owner owner, String name, PetType type, LocalDate birthDate) {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setType(type);
        pet.setBirthDate(birthDate);
        owner.addPet(pet);
    }

    private List<Vet> sampleVets() {
        Vet james = createVet("James", "Carter", "surgery");
        Vet helen = createVet("Helen", "Leary", "radiology");
        Vet linda = createVet("Linda", "Douglas", "dentistry");
        return List.of(james, helen, linda);
    }

    private Vet createVet(String firstName, String lastName, String specialty) {
        Vet vet = new Vet();
        vet.setFirstName(firstName);
        vet.setLastName(lastName);
        vet.addSpecialty(specialty);
        return vet;
    }
}

