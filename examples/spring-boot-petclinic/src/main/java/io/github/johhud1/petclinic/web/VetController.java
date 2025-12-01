package io.github.johhud1.petclinic.web;

import io.github.johhud1.petclinic.service.PetClinicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vets")
public class VetController {

    private final PetClinicService petClinicService;

    public VetController(PetClinicService petClinicService) {
        this.petClinicService = petClinicService;
    }

    @GetMapping
    public String listVets(Model model) {
        model.addAttribute("vets", petClinicService.findVets());
        return "vets/list";
    }
}

