package com.daw.CinemaDaw.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.daw.CinemaDaw.domain.cinema.pelicula;
import com.daw.CinemaDaw.repository.PeliculaRepository;

@Controller
public class ClientController {

    private final PeliculaRepository peliculaRepository;

    public ClientController(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    @GetMapping("/client/cartelera")
    public String cartelera(Model model) {
        List<pelicula> peliculas = peliculaRepository.findAll();
        model.addAttribute("llista", peliculas);
        return "client/cartelera";
    }


@GetMapping("/cartelera/{id}")
    public String detall(@PathVariable Long id, Model model){
        Optional<pelicula> optional = peliculaRepository.findById(id);
        if(optional.isPresent()){
            pelicula pelicula = optional.get();
            model.addAttribute("pelicula", pelicula);
            return "client/cartelera-especifica";
        }
        return "redirect:/";
    }










}


