package io.github.johhud1.petclinic.web;

import io.github.johhud1.petclinic.model.Owner;
import io.github.johhud1.petclinic.service.PetClinicService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/owners")
public class OwnerController {

    private final PetClinicService petClinicService;

    public OwnerController(PetClinicService petClinicService) {
        this.petClinicService = petClinicService;
    }

    @GetMapping
    public String listOwners(Model model) {
        model.addAttribute("owners", petClinicService.findOwners());
        return "owners/list";
    }

    @GetMapping("/{id}")
    public String ownerDetails(@PathVariable Long id, Model model) {
        Owner owner = petClinicService.findOwnerById(id);
        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found.");
        }
        model.addAttribute("owner", owner);
        return "owners/details";
    }
}
