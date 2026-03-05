package com.daw.CinemaDaw.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.CinemaDaw.domain.cinema.pelicula;
import com.daw.CinemaDaw.repository.PeliculaRepository;

@Controller
public class peliculaController {

    private PeliculaRepository peliculaRepository;

    public peliculaController(PeliculaRepository peliculaRepository){
        this.peliculaRepository=peliculaRepository;
    }

   

     @GetMapping("/pelicula")
    public String pelicules(Model model){

        List<pelicula> pelicula = peliculaRepository.findAll();
        model.addAttribute("llista", pelicula);
        return "pelicula";
    }
    
    //detall pelicula
    @GetMapping("/pelicula/{id}")
    public String detall(@PathVariable Long id, Model model){
        Optional<pelicula> optional = peliculaRepository.findById(id);
        if(optional.isPresent()){
            pelicula pelicula = optional.get();
            model.addAttribute("pelicula", pelicula);
            return "CarpetaPelicula/pelicula-detall";
        }
        return "redirect:/";
    }


    //crear pelicula
    @GetMapping("/pelicula/create")
    public String create(Model model){
        pelicula pelicula = new pelicula();
        model.addAttribute("pelicula", pelicula);
        return "CarpetaPelicula/crear-pelicula";
    }
    @PostMapping("/pelicula/create")
    public String create(pelicula pelicula){
        peliculaRepository.save(pelicula);
        return "redirect:/pelicula";
    }

//borrar pelicula
    @GetMapping("/pelicula/delete/{id}")
    public String delete(@PathVariable Long id, Model model){
        Optional<pelicula> optional = peliculaRepository.findById(id);
        if(optional.isPresent()){
            pelicula pelicula = optional.get();
            peliculaRepository.delete(pelicula);
        }
        return "redirect:/pelicula";
    }

//editar pelicula
    @GetMapping("/pelicula/edit/{id}")
    public String edit(@PathVariable Long id, Model model){
        Optional<pelicula> optional = peliculaRepository.findById(id);
        if(optional.isPresent()){
            pelicula pelicula = optional.get();
            model.addAttribute("pelicula", pelicula);
            return "CarpetaPelicula/editar-pelicula";
        }
        return "redirect:/";
    }

    @PostMapping("/pelicula/edit")
    public String edit(pelicula pelicula){
        peliculaRepository.save(pelicula);
        return "redirect:/pelicula";
    }




}
