package com.daw.CinemaDaw.controller;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.CinemaDaw.domain.cinema.Room;
import com.daw.CinemaDaw.domain.cinema.Screening;
import com.daw.CinemaDaw.domain.cinema.pelicula;
import com.daw.CinemaDaw.repository.PeliculaRepository;
import com.daw.CinemaDaw.repository.RoomRepository;
import com.daw.CinemaDaw.repository.ScreeningRepository;

@Controller
public class ScreeningController {

    private ScreeningRepository screeningRepository;
    private PeliculaRepository peliculaRepository;
    private RoomRepository roomRepository;

    public ScreeningController(ScreeningRepository screeningRepository,
                               PeliculaRepository peliculaRepository,
                               RoomRepository roomRepository) {
        this.screeningRepository = screeningRepository;
        this.peliculaRepository = peliculaRepository;
        this.roomRepository = roomRepository;
    }

    // Totes les sessions
   @GetMapping("/screenings/all")
    public String todasScreenings(Model model) {
    model.addAttribute("screenings", screeningRepository.findAll());
    model.addAttribute("pelicules", peliculaRepository.findAll());
    return "/screenings/screenings-all";
}

    // Sessions d'una pel·licula
    @GetMapping("/screenings/movie/{movieId}")
    public String screeningsByMovie(@PathVariable Long movieId, Model model) {
        Optional<pelicula> optionalMovie = peliculaRepository.findById(movieId);
        if (optionalMovie.isEmpty()) {
            return "redirect:/screenings/all";
        }

        List<Screening> screenings = screeningRepository.findByMovie(optionalMovie.get());
        screenings.sort(
            Comparator.comparing(Screening::getScreeningDate)
                .thenComparing(Screening::getScreeningTime)
        );

        model.addAttribute("movie", optionalMovie.get());
        model.addAttribute("screenings", screenings);
        return "/screenings/screenings-bymovies";
    }

    // Llista de projeccions d'una sala
    @GetMapping("/screening/llista/{roomId}")
    public String llista(@PathVariable Long roomId, Model model) {
        Optional<Room> optional = roomRepository.findById(roomId);
        if (optional.isEmpty()) return "redirect:/cinemes";
        List<Screening> screenings = screeningRepository.findByRoom_Id(roomId);
        model.addAttribute("room", optional.get());
        model.addAttribute("screenings", screenings);
        return "/screenings/screening-llista";
    }

    // Detall d'una projecció
    @GetMapping("/screening/{id}")
    public String detall(@PathVariable Long id, Model model) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isEmpty()) return "redirect:/cinemes";
        model.addAttribute("screening", optional.get());
        return "/screenings/screening-detail";
    }

    // Formulari crear projecció
    @GetMapping("/screening/create/{roomId}")
    public String newScreening(@PathVariable Long roomId, Model model) {
        Screening screening = new Screening();
        Room room = new Room();
        room.setId(roomId);
        screening.setRoom(room);
        model.addAttribute("screening", screening);
        model.addAttribute("pelicules", peliculaRepository.findAll());
        return "/screenings/screening-crear";
    }

    // Guardar nova projecció
    @PostMapping("/screening/new")
    public String createScreening(@ModelAttribute Screening screening) {
        Long roomId = screening.getRoom().getId();
        Optional<Room> room = roomRepository.findById(roomId);
        Optional<pelicula> movie = peliculaRepository.findById(screening.getMovie().getId());
        if (room.isPresent() && movie.isPresent()) {
            screening.setRoom(room.get());
            screening.setMovie(movie.get());
            screeningRepository.save(screening);
        }
        return "redirect:/screening/llista/" + roomId;
    }

    // Formulari editar projecció
    @GetMapping("/screening/edit/{id}")
    public String editScreening(@PathVariable Long id, Model model) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("screening", optional.get());
            model.addAttribute("pelicules", peliculaRepository.findAll());
            return "/screenings/screening-edit";
        }
        return "redirect:/cinemes";
    }

    // Guardar canvis projecció
    @PostMapping("/screening/editar")
    public String updateScreening(@ModelAttribute Screening screening) {
        Long roomId = screening.getRoom().getId();
        Long screeningId = screening.getId();

        Optional<Screening> existing = screeningRepository.findById(screeningId);
        Optional<Room> room = roomRepository.findById(roomId);
        Optional<pelicula> movie = peliculaRepository.findById(screening.getMovie().getId());

        if (existing.isPresent() && room.isPresent() && movie.isPresent()) {
            Screening s = existing.get();
            s.setScreeningDate(screening.getScreeningDate());
            s.setScreeningTime(screening.getScreeningTime());
            s.setPrice(screening.getPrice());
            s.setRoom(room.get());
            s.setMovie(movie.get());
            screeningRepository.save(s);
        }
        return "redirect:/screening/llista/" + roomId;
    }

    // Esborrar projecció
    @GetMapping("/screening/delete/{id}")
    public String deleteScreening(@PathVariable Long id) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isPresent()) {
            Long roomId = optional.get().getRoom().getId();
            screeningRepository.delete(optional.get());
            return "redirect:/screening/llista/" + roomId;
        }
        return "redirect:/cinemes";
    }
}
