package com.daw.CinemaDaw.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.CinemaDaw.domain.cinema.Cinema;
import com.daw.CinemaDaw.domain.cinema.Room;
import com.daw.CinemaDaw.repository.CinemaRepository;
import com.daw.CinemaDaw.repository.RoomRepository;

@Controller
public class RoomController {

    private RoomRepository roomRepository;
    private CinemaRepository cinemaRepository;

    public RoomController(RoomRepository roomRepository, CinemaRepository cinemaRepository) {
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
    }

    @GetMapping("/room/create/{cinemaId}")
    public String newRoom(@PathVariable Long cinemaId, Model model) {
        Room room = new Room();
        Cinema cinema = new Cinema();
        cinema.setId(cinemaId);
        room.setCinema(cinema);
        model.addAttribute("room", room);
        return "/rooms/room-crear";
    }

     @PostMapping("/room/new")
    public String altaPelicula(@ModelAttribute Room room) {
        Long cinemaid = room.getCinema().getId();
        Optional<Cinema> cinema = cinemaRepository.findById(cinemaid);
        if (cinema.isPresent()) {
            room.setCinema(cinema.get());
        }
        roomRepository.save(room);
        return "redirect:/cinema/" + cinemaid;
    }

    private static final int CELL_SIZE = 46;

    @GetMapping("/room/edit/{id}")
    public String editRoom(@PathVariable Long id, Model model) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            Room room = optional.get();
            model.addAttribute("room", room);
            return "/rooms/room-edit";
        }
        return "redirect:/cinemes";
    }

    @PostMapping("/room/editar")
    public String editPelicula(@ModelAttribute Room room) {
        Long cinemaid = room.getCinema().getId();
        Long roomId = room.getId();

        Optional<Cinema> cinema = cinemaRepository.findById(cinemaid);
        Optional<Room> existingRoom = roomRepository.findById(roomId);

        if (existingRoom.isPresent() && cinema.isPresent()) {
            Room roomToUpdate = existingRoom.get();
            roomToUpdate.setName(room.getName());
            roomToUpdate.setCapacity(room.getCapacity());
            roomToUpdate.setCinema(cinema.get());
            roomRepository.save(roomToUpdate);
        }

        return "redirect:/cinema/" + cinemaid;
    }

    @GetMapping("/room/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isPresent()) {
            Room room = optional.get();
            Long cinemaid = room.getCinema().getId();
            roomRepository.delete(room);
            return "redirect:/cinema/" + cinemaid;
        }
        return "redirect:/cinema";
    }

   

    @GetMapping("/room/{id}")
    public String detall(@PathVariable Long id, Model model) {
        Optional<Room> optional = roomRepository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/cinemes";
        }
        Room room = optional.get();
        model.addAttribute("room", room);
        return "/rooms/room-detail";
    }

}