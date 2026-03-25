package com.daw.CinemaDaw.controller;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.CinemaDaw.domain.cinema.Cinema;
import com.daw.CinemaDaw.domain.cinema.Room;
import com.daw.CinemaDaw.domain.cinema.Seat;
import com.daw.CinemaDaw.domain.cinema.SeatType;
import com.daw.CinemaDaw.repository.CinemaRepository;
import com.daw.CinemaDaw.repository.RoomRepository;
import com.daw.CinemaDaw.repository.SeatRepository;

import jakarta.transaction.Transactional;

@Controller
@PreAuthorize("hasRole('ADMIN')")  // protege TODOS los métodos de la clase

public class RoomController {

    private RoomRepository roomRepository;
    private CinemaRepository cinemaRepository;
    private SeatRepository seatRepository;

    public RoomController(RoomRepository roomRepository, CinemaRepository cinemaRepository, SeatRepository seatRepository) {
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
        this.seatRepository = seatRepository;
    }

    private void generateSeats(Room room) {
        int capacity = room.getCapacity();
        int cols = (int) Math.ceil(Math.sqrt(capacity));
        int rows = (int) Math.ceil((double) capacity / cols);

        int seatsCreated = 0;
        for (int i = 0; i < rows && seatsCreated < capacity; i++) {
            String rowLetter = String.valueOf((char) ('A' + i));
            for (int j = 1; j <= cols && seatsCreated < capacity; j++) {
                Seat seat = new Seat(rowLetter, j, j, i, SeatType.STANDARD, true);
                seat.setRoom(room);
                seatRepository.save(seat);
                seatsCreated++;
            }
        }
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
    public String altaRoom(@ModelAttribute Room room) {
        Long cinemaid = room.getCinema().getId();
        Optional<Cinema> cinema = cinemaRepository.findById(cinemaid);
        if (cinema.isPresent()) {
            room.setCinema(cinema.get());
        }
        roomRepository.save(room);
        generateSeats(room);
        return "redirect:/cinema/" + cinemaid;
    }

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

  @Transactional
@PostMapping("/room/editar")
public String editRoom(@ModelAttribute Room room) {
        Long cinemaid = room.getCinema().getId();
        Long roomId = room.getId();

        Optional<Cinema> cinema = cinemaRepository.findById(cinemaid);
        Optional<Room> existingRoom = roomRepository.findById(roomId);

        if (existingRoom.isPresent() && cinema.isPresent()) {
            Room roomToUpdate = existingRoom.get();
            boolean capacityChanged = roomToUpdate.getCapacity() != room.getCapacity();

            roomToUpdate.setName(room.getName());
            roomToUpdate.setCapacity(room.getCapacity());
            roomToUpdate.setCinema(cinema.get());
            roomRepository.save(roomToUpdate);

            // Si cambia la capacidad, borrar seats antiguos y generar nuevos
            if (capacityChanged) {
    seatRepository.deleteAllByRoomId(roomToUpdate.getId());
    roomToUpdate.setCapacity(room.getCapacity());
    generateSeats(roomToUpdate);
}
            
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