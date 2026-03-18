package com.daw.CinemaDaw.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.daw.CinemaDaw.domain.cinema.Room;
import com.daw.CinemaDaw.domain.cinema.Seat;
import com.daw.CinemaDaw.domain.cinema.SeatType;
import com.daw.CinemaDaw.repository.RoomRepository;
import com.daw.CinemaDaw.repository.SeatRepository;

@Controller
public class SeatController {

    private SeatRepository seatRepository;
    private RoomRepository roomRepository;

    public SeatController(SeatRepository seatRepository, RoomRepository roomRepository) {
        this.seatRepository = seatRepository;
        this.roomRepository = roomRepository;
    }

    // Mostrar detall d'una silla
    @GetMapping("/seat/{id}")
    public String detall(@PathVariable Long id, Model model) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/cinemes";
        }
        Seat seat = optional.get();
        model.addAttribute("seat", seat);
        model.addAttribute("seatTypes", SeatType.values());
        return "/seats/seat-detail";
    }

    // Formulari crear silla
    @GetMapping("/seat/create/{roomId}")
    public String newSeat(@PathVariable Long roomId, Model model) {
        Seat seat = new Seat();
        Room room = new Room();
        room.setId(roomId);
        seat.setRoom(room);
        model.addAttribute("seat", seat);
        model.addAttribute("seatTypes", SeatType.values());
        return "/seats/seat-crear";
    }

    // Guardar nova silla
    @PostMapping("/seat/new")
    public String createSeat(@ModelAttribute Seat seat) {
        Long roomId = seat.getRoom().getId();
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isPresent()) {
            seat.setRoom(room.get());
        }
        seatRepository.save(seat);
        return "redirect:/room/" + roomId;
    }

    // Formulari editar silla
    @GetMapping("/seat/edit/{id}")
    public String editSeat(@PathVariable Long id, Model model) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            Seat seat = optional.get();
            model.addAttribute("seat", seat);
            model.addAttribute("seatTypes", SeatType.values());
            return "/seats/seat-edit";
        }
        return "redirect:/cinemes";
    }

    // Guardar canvis silla
    @PostMapping("/seat/editar")
    public String updateSeat(@ModelAttribute Seat seat) {
        Long roomId = seat.getRoom().getId();
        Long seatId = seat.getId();

        Optional<Room> room = roomRepository.findById(roomId);
        Optional<Seat> existingSeat = seatRepository.findById(seatId);

        if (existingSeat.isPresent() && room.isPresent()) {
            Seat seatToUpdate = existingSeat.get();
            seatToUpdate.setSeatRow(seat.getSeatRow());
            seatToUpdate.setSeatNumber(seat.getSeatNumber());
            seatToUpdate.setPosX(seat.getPosX());
            seatToUpdate.setPosY(seat.getPosY());
            seatToUpdate.setType(seat.getType());
            seatToUpdate.setActive(seat.isActive());
            seatToUpdate.setRoom(room.get());
            seatRepository.save(seatToUpdate);
        }

        return "redirect:/room/" + roomId;
    }

    // Esborrar silla
    @GetMapping("/seat/delete/{id}")
    public String deleteSeat(@PathVariable Long id) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (optional.isPresent()) {
            Seat seat = optional.get();
            Long roomId = seat.getRoom().getId();
            seatRepository.delete(seat);
            return "redirect:/room/" + roomId;
        }
        return "redirect:/cinemes";
    }



@GetMapping("/seat/llista/{roomId}")
public String llista(@PathVariable Long roomId, Model model) {
    Optional<Room> optional = roomRepository.findById(roomId);
    if (optional.isEmpty()) {
        return "redirect:/cinemes";
    }
    model.addAttribute("room", optional.get());
    return "seat-llista";
}






}