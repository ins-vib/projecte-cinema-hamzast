package com.daw.CinemaDaw.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.CinemaDaw.domain.cinema.Cinema;
import com.daw.CinemaDaw.repository.CinemaRepository;

@Controller
@PreAuthorize("hasRole('ADMIN')")  // protege TODOS los métodos de la clase
public class cinemaController {

    private CinemaRepository cinemaRepository;

    public cinemaController(CinemaRepository cinemaRepository){
        this.cinemaRepository=cinemaRepository;
    }

   

     @GetMapping("/cinemes")
    public String cinemes(Model model){

        List<Cinema> cinemes = cinemaRepository.findAll();
        model.addAttribute("llista", cinemes);
        return "cinema/cinemes";
    }
    
    //detall cinema
    @GetMapping("/cinema/{id}")
    public String detall(@PathVariable Long id, Model model){
        Optional<Cinema> optional = cinemaRepository.findById(id);
        if(optional.isPresent()){
            Cinema cinema = optional.get();
            model.addAttribute("cinema", cinema);
            return "cinema/cinemes-detall";
        }
        return "redirect:/";
       

    }


    //crear cinema
    @GetMapping("/cinema/create")
    public String create(Model model){
        Cinema cinema = new Cinema();
        model.addAttribute("cinema", cinema);
        return "cinema/create-cinema";
    }
    @PostMapping("/cinema/create")
    public String create(Cinema cinema){
        cinemaRepository.save(cinema);
        return "redirect:/cinemes";
    }

//borrar cinema
    @GetMapping("/cinema/delete/{id}")
    public String delete(@PathVariable Long id, Model model){
        Optional<Cinema> optional = cinemaRepository.findById(id);
        if(optional.isPresent()){
            Cinema cinema = optional.get();
            cinemaRepository.delete(cinema);
        }
        return "redirect:/cinemes";
    }

//editar cinema
    @GetMapping("/cinema/edit/{id}")
    public String edit(@PathVariable Long id, Model model){
        Optional<Cinema> optional = cinemaRepository.findById(id);
        if(optional.isPresent()){
            Cinema cinema = optional.get();
            model.addAttribute("cinema", cinema);
            return "cinema/edit-cinema";
        }
        return "redirect:/";
    }

    @PostMapping("/cinema/edit")
    public String edit(Cinema cinema){
        cinemaRepository.save(cinema);
        return "redirect:/cinemes";
    }




    
}


