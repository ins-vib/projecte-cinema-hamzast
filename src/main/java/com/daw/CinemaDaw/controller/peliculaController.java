package com.daw.CinemaDaw.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daw.CinemaDaw.domain.cinema.Genere;
import com.daw.CinemaDaw.domain.cinema.Screening;
import com.daw.CinemaDaw.domain.cinema.pelicula;
import com.daw.CinemaDaw.repository.GenereRepository;
import com.daw.CinemaDaw.repository.PeliculaRepository;
import com.daw.CinemaDaw.repository.ScreeningRepository;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class peliculaController {

    private final PeliculaRepository peliculaRepository;
    private final GenereRepository genereRepository;
    private final ScreeningRepository screeningRepository;

    public peliculaController(PeliculaRepository peliculaRepository,
                               GenereRepository genereRepository,
                               ScreeningRepository screeningRepository) {
        this.peliculaRepository = peliculaRepository;
        this.genereRepository = genereRepository;
        this.screeningRepository = screeningRepository;
    }

    @GetMapping("/pelicula")
    public String pelicules(Model model) {
        List<pelicula> pelicula = peliculaRepository.findAll();
        model.addAttribute("llista", pelicula);
        return "CarpetaPelicula/pelicula";
    }

    @GetMapping("/pelicula/{id}")
    public String detall(@PathVariable Long id, Model model) {
        Optional<pelicula> optional = peliculaRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("pelicula", optional.get());
            return "CarpetaPelicula/pelicula-detall";
        }
        return "redirect:/";
    }

    @GetMapping("/pelicula/create")
    public String create(Model model) {
        model.addAttribute("pelicula", new pelicula());
        model.addAttribute("totsElsGeneres", genereRepository.findAll());
        return "CarpetaPelicula/crear-pelicula";
    }

    @PostMapping("/pelicula/create")
    public String create(pelicula pelicula,
                         @RequestParam(value = "genereIds", required = false) List<Long> genereIds,
                         Model model) {
        if (hasEmptyFields(pelicula)) {
            model.addAttribute("pelicula", pelicula);
            model.addAttribute("totsElsGeneres", genereRepository.findAll());
            return FormValidation.withRequiredFieldsError(model, "CarpetaPelicula/crear-pelicula");
        }
        assignarGeneres(pelicula, genereIds);
        peliculaRepository.save(pelicula);
        return "redirect:/pelicula";
    }

    @GetMapping("/pelicula/delete/{id}")
    public String delete(@PathVariable Long id) {
        Optional<pelicula> optional = peliculaRepository.findById(id);
        if (optional.isPresent()) {
            pelicula p = optional.get();
            List<Screening> sessions = screeningRepository.findByMovie(p);
            screeningRepository.deleteAll(sessions);
            peliculaRepository.delete(p);
        }
        return "redirect:/pelicula";
    }

    @GetMapping("/pelicula/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Optional<pelicula> optional = peliculaRepository.findById(id);
        if (optional.isPresent()) {
            pelicula p = optional.get();
            model.addAttribute("pelicula", p);
            model.addAttribute("totsElsGeneres", genereRepository.findAll());
            Set<Long> seleccionats = new HashSet<>();
            for (Genere g : p.getGeneres()) seleccionats.add(g.getId());
            model.addAttribute("generesSeleccionats", seleccionats);
            return "CarpetaPelicula/editar-pelicula";
        }
        return "redirect:/";
    }

    @PostMapping("/pelicula/edit")
    public String edit(pelicula pelicula,
                       @RequestParam(value = "genereIds", required = false) List<Long> genereIds,
                       Model model) {
        if (hasEmptyFields(pelicula)) {
            model.addAttribute("pelicula", pelicula);
            model.addAttribute("totsElsGeneres", genereRepository.findAll());
            model.addAttribute("generesSeleccionats", new HashSet<>());
            return FormValidation.withRequiredFieldsError(model, "CarpetaPelicula/editar-pelicula");
        }
        assignarGeneres(pelicula, genereIds);
        peliculaRepository.save(pelicula);
        return "redirect:/pelicula";
    }

    private void assignarGeneres(pelicula pelicula, List<Long> genereIds) {
        Set<Genere> generes = new HashSet<>();
        if (genereIds != null) {
            for (Long gid : genereIds) {
                genereRepository.findById(gid).ifPresent(generes::add);
            }
        }
        pelicula.setGeneres(generes);
    }

    private boolean hasEmptyFields(pelicula pelicula) {
        return FormValidation.isBlank(pelicula.getTitol())
                || pelicula.getDurada() <= 0
                || FormValidation.isBlank(pelicula.getSinopsi())
                || FormValidation.isBlank(pelicula.getEstrena());
    }
}