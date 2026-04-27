package com.daw.CinemaDaw.controller;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daw.CinemaDaw.domain.cinema.CartItem;
import com.daw.CinemaDaw.domain.cinema.Screening;
import com.daw.CinemaDaw.domain.cinema.Seat;
import com.daw.CinemaDaw.domain.cinema.TicketOrder;
import com.daw.CinemaDaw.domain.cinema.user.User;
import com.daw.CinemaDaw.domain.cinema.pelicula;
import com.daw.CinemaDaw.repository.PeliculaRepository;
import com.daw.CinemaDaw.repository.ScreeningRepository;
import com.daw.CinemaDaw.service.CartService;

@Controller
public class ClientController {

    private final PeliculaRepository peliculaRepository;
    private final ScreeningRepository screeningRepository;
    private final CartService cartService;

    public ClientController(PeliculaRepository peliculaRepository,
                            ScreeningRepository screeningRepository,
                            CartService cartService) {
        this.peliculaRepository = peliculaRepository;
        this.screeningRepository = screeningRepository;
        this.cartService = cartService;
    }

    @GetMapping("/client/cartelera")
    public String cartelera(Model model) {
        List<pelicula> peliculas = peliculaRepository.findAll();
        model.addAttribute("llista", peliculas);
        return "client/cartelera";
    }

    @GetMapping("/cartelera/{id}")
    public String detall(@PathVariable Long id, Model model) {
        Optional<pelicula> optional = peliculaRepository.findById(id);
        if (optional.isPresent()) {
            pelicula pelicula = optional.get();
            List<Screening> screenings = screeningRepository.findByMovie(pelicula);
            screenings.sort(Comparator.comparing(Screening::getScreeningDateTime));
            model.addAttribute("pelicula", pelicula);
            model.addAttribute("screenings", screenings);
            return "client/cartelera-especifica";
        }
        return "redirect:/";
    }

    @GetMapping("/client/screening/{id}/seats")
    public String selectSeats(@PathVariable Long id, Model model) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/client/cartelera";
        }

        Screening screening = optional.get();
        String username = cartService.getCurrentUsername();
        Set<Long> preselected = username == null
                ? Set.of()
                : cartService.getCurrentUserSeatIdsForScreening(screening.getId(), username);

        populateSeatSelectionModel(model, screening, preselected, null, null);
        return "client/seleccionar-asientos";
    }

    @PostMapping("/client/screening/{id}/book")
    public String bookSeats(@PathVariable Long id,
                            @RequestParam(name = "seatIds", required = false) List<Long> seatIds,
                            Model model) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/client/cartelera";
        }

        Screening screening = optional.get();
        User user = cartService.requireCurrentUser();
        Set<Long> validSeatIds = screening.getRoom().getSeats().stream()
                .filter(Seat::isActive)
                .map(Seat::getId)
                .collect(Collectors.toSet());

        Set<Long> selectedIds = seatIds == null
                ? Set.of()
                : seatIds.stream()
                        .filter(validSeatIds::contains)
                        .collect(Collectors.toCollection(TreeSet::new));

        if (selectedIds.isEmpty()) {
            populateSeatSelectionModel(model, screening, Set.of(), "Selecciona al menos un asiento.", null);
            return "client/seleccionar-asientos";
        }

        List<Seat> selectedSeats = screening.getRoom().getSeats().stream()
                .filter(seat -> selectedIds.contains(seat.getId()))
                .toList();

        CartService.AddSeatsResult result = cartService.addSeats(user, screening, selectedSeats);
        if (result.getAdded() > 0) {
            return "redirect:/client/cart?added=" + result.getAdded();
        }

        String error = result.getOccupied() > 0
                ? "Algunos asientos ya no estan disponibles."
                : "Esos asientos ya estaban en tu carrito.";
        Set<Long> preselected = cartService.getCurrentUserSeatIdsForScreening(screening.getId(), user.getUsername());
        populateSeatSelectionModel(model, screening, preselected, error, null);
        return "client/seleccionar-asientos";
    }

    @GetMapping("/client/cart")
    public String cart(@RequestParam(name = "added", required = false) Integer added,
                       @RequestParam(name = "removed", required = false) Integer removed,
                       @RequestParam(name = "cleared", required = false) Boolean cleared,
                       @RequestParam(name = "error", required = false) Boolean error,
                       Model model) {
        List<CartItem> items = cartService.getCurrentUserItems();
        model.addAttribute("cartItems", items);
        model.addAttribute("cartTotal", cartService.getTotalAmount());

        if (added != null && added > 0) {
            model.addAttribute("success", "Se han anadido " + added + " entrada(s) al carrito.");
        } else if (removed != null && removed > 0) {
            model.addAttribute("success", "La entrada se ha quitado del carrito.");
        } else if (Boolean.TRUE.equals(cleared)) {
            model.addAttribute("success", "El carrito se ha vaciado.");
        } else if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", "No se pudo completar la compra porque algun asiento ya no estaba disponible.");
        }

        return "client/carrito";
    }

    @PostMapping("/client/cart/remove/{itemId}")
    public String removeCartItem(@PathVariable Long itemId) {
        cartService.removeItemForCurrentUser(itemId);
        return "redirect:/client/cart?removed=1";
    }

    @PostMapping("/client/cart/clear")
    public String clearCart() {
        cartService.clearCurrentUserCart();
        return "redirect:/client/cart?cleared=1";
    }

    @PostMapping("/client/cart/checkout")
    public String checkoutCart() {
        try {
            TicketOrder order = cartService.checkoutCurrentUserCart();
            if (order == null) {
                return "redirect:/client/cart";
            }
            return "redirect:/client/my-orders?bought=1";
        } catch (IllegalStateException ex) {
            return "redirect:/client/cart?error=1";
        }
    }

    @GetMapping("/client/my-orders")
    public String myOrders(@RequestParam(name = "bought", required = false) Boolean bought,
                           Model model) {
        model.addAttribute("orders", cartService.getCurrentUserOrders());
        if (Boolean.TRUE.equals(bought)) {
            model.addAttribute("success", "Compra realizada correctamente. Tus tickets ya estan en pedidos.");
        }
        return "client/pedidos";
    }

    private void populateSeatSelectionModel(Model model,
                                            Screening screening,
                                            Set<Long> preselected,
                                            String error,
                                            String success) {
        String username = cartService.getCurrentUsername();
        LinkedHashMap<String, List<Seat>> seatsByRow = screening.getRoom().getSeats().stream()
                .filter(Seat::isActive)
                .sorted(Comparator.comparing(Seat::getSeatRow).thenComparingInt(Seat::getSeatNumber))
                .collect(Collectors.groupingBy(
                        Seat::getSeatRow,
                        LinkedHashMap::new,
                        Collectors.toList()));

        model.addAttribute("screening", screening);
        model.addAttribute("seatsByRow", seatsByRow);
        model.addAttribute("bookedIds", username == null
                ? Set.of()
                : cartService.getBookedSeatIdsForScreening(screening.getId(), username));
        model.addAttribute("preselected", preselected);
        model.addAttribute("error", error);
        model.addAttribute("success", success);
    }
}
