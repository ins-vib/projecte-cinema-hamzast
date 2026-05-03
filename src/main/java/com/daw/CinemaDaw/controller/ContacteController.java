package com.daw.CinemaDaw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContacteController {

    @GetMapping("/contacte")
    public String cartelera(Model model) {
        // Sin la barra "/" al principio
        return "contacte"; 
    }

}