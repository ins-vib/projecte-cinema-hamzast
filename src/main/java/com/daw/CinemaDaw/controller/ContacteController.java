package com.daw.CinemaDaw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContacteController {

    @GetMapping("/contacte")
    public String cartelera(Model model) {
        // Sin la barra "/" al principio
        return "contacte"; 
    }

    @PostMapping("/contact/send")
    public String send(@RequestParam(required = false) String nom,
                       @RequestParam(required = false) String cognom,
                       @RequestParam(required = false) String email,
                       @RequestParam(required = false) String assumpte,
                       @RequestParam(required = false) String missatge,
                       Model model) {
        model.addAttribute("nom", nom);
        model.addAttribute("cognom", cognom);
        model.addAttribute("email", email);
        model.addAttribute("assumpte", assumpte);
        model.addAttribute("missatge", missatge);

        if (FormValidation.isBlank(nom)
                || FormValidation.isBlank(cognom)
                || FormValidation.isBlank(email)
                || FormValidation.isBlank(assumpte)
                || FormValidation.isBlank(missatge)) {
            return FormValidation.withRequiredFieldsError(model, "contacte");
        }

        return "redirect:/contacte";
    }

}
