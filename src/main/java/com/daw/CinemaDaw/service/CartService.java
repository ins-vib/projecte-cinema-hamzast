package com.daw.CinemaDaw.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.CinemaDaw.domain.cinema.CartItem;
import com.daw.CinemaDaw.domain.cinema.PromoCode;
import com.daw.CinemaDaw.domain.cinema.Screening;
import com.daw.CinemaDaw.domain.cinema.Seat;
import com.daw.CinemaDaw.domain.cinema.Ticket;
import com.daw.CinemaDaw.domain.cinema.TicketOrder;
import com.daw.CinemaDaw.domain.cinema.user.User;
import com.daw.CinemaDaw.repository.CartItemRepository;
import com.daw.CinemaDaw.repository.TicketOrderRepository;
import com.daw.CinemaDaw.repository.TicketRepository;
import com.daw.CinemaDaw.repository.UserRepository;

@Service("cartService")
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final TicketRepository ticketRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final UserRepository userRepository;
    private final PromoService promoService;

    public CartService(CartItemRepository cartItemRepository,
                       TicketRepository ticketRepository,
                       TicketOrderRepository ticketOrderRepository,
                       UserRepository userRepository,
                       PromoService promoService) {
        this.cartItemRepository = cartItemRepository;
        this.ticketRepository = ticketRepository;
        this.ticketOrderRepository = ticketOrderRepository;
        this.userRepository = userRepository;
        this.promoService = promoService;
    }

    public long getItemCount() {
        String username = getCurrentUsername();
        if (username == null) return 0;
        return cartItemRepository.countByUser_Username(username);
    }

    public List<CartItem> getCurrentUserItems() {
        String username = getCurrentUsername();
        if (username == null) return List.of();
        return cartItemRepository.findByUser_UsernameOrderByCreatedAtAsc(username);
    }

    public double getTotalAmount() {
        return getCurrentUserItems().stream()
                .mapToDouble(item -> item.getScreening().getPrice() + getSeatExtra(item.getSeat()))
                .sum();
    }

    /** Total after applying a promo code (0% discount if code is null/blank). */
    public double getTotalWithPromo(String promoCode) {
        double subtotal = getTotalAmount();
        if (promoCode == null || promoCode.isBlank()) return subtotal;

        String username = getCurrentUsername();
        if (username == null) return subtotal;

        Optional<PromoCode> promo = promoService.findValidForUser(promoCode, username);
        if (promo.isEmpty()) return subtotal;

        return Math.max(0, subtotal - promo.get().applyTo(subtotal));
    }

    public Set<Long> getBookedSeatIdsForScreening(Long screeningId, String username) {
        Set<Long> cartSeatIds = cartItemRepository.findByScreening_Id(screeningId).stream()
                .filter(item -> !item.getUser().getUsername().equals(username))
                .map(item -> item.getSeat().getId())
                .collect(Collectors.toSet());

        Set<Long> soldSeatIds = ticketRepository.findByScreening_Id(screeningId).stream()
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toSet());

        cartSeatIds.addAll(soldSeatIds);
        return cartSeatIds;
    }

    public Set<Long> getCurrentUserSeatIdsForScreening(Long screeningId, String username) {
        return cartItemRepository.findByScreening_IdAndUser_Username(screeningId, username).stream()
                .map(item -> item.getSeat().getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional
    public AddSeatsResult addSeats(User user, Screening screening, List<Seat> seats) {
        int added = 0, alreadyMine = 0, occupied = 0;

        for (Seat seat : seats) {
            boolean alreadySold = ticketRepository.findByScreening_Id(screening.getId()).stream()
                    .anyMatch(ticket -> ticket.getSeat().getId().equals(seat.getId()));
            if (alreadySold) { occupied++; continue; }

            Optional<CartItem> existing = cartItemRepository.findByScreening_IdAndSeat_Id(screening.getId(), seat.getId());
            if (existing.isPresent()) {
                if (existing.get().getUser().getId().equals(user.getId())) alreadyMine++;
                else occupied++;
                continue;
            }

            cartItemRepository.save(new CartItem(user, screening, seat));
            added++;
        }

        return new AddSeatsResult(added, alreadyMine, occupied);
    }

    @Transactional
    public boolean removeItemForCurrentUser(Long itemId) {
        String username = getCurrentUsername();
        if (username == null) return false;

        Optional<CartItem> item = cartItemRepository.findByIdAndUser_Username(itemId, username);
        if (item.isEmpty()) return false;

        cartItemRepository.delete(item.get());
        return true;
    }

    @Transactional
    public void clearCurrentUserCart() {
        String username = getCurrentUsername();
        if (username != null) cartItemRepository.deleteByUser_Username(username);
    }

    /**
     * Checkout without promo code (backwards-compatible).
     */
    @Transactional
    public TicketOrder checkoutCurrentUserCart() {
        return checkoutCurrentUserCart(null);
    }

    /**
     * Checkout with an optional promo code string.
     * If the code is valid, a PromoUsage record is created and the
     * paidPrice on each ticket is proportionally reduced.
     */
    @Transactional
    public TicketOrder checkoutCurrentUserCart(String promoCode) {
        User user = requireCurrentUser();
        List<CartItem> items = cartItemRepository.findByUser_UsernameOrderByCreatedAtAsc(user.getUsername());
        if (items.isEmpty()) return null;

        // Check no seat was already sold
        for (CartItem item : items) {
            boolean alreadySold = ticketRepository.findByScreening_Id(item.getScreening().getId()).stream()
                    .anyMatch(ticket -> ticket.getSeat().getId().equals(item.getSeat().getId()));
            if (alreadySold) {
                throw new IllegalStateException("Uno de los asientos del carrito ya ha sido comprado.");
            }
        }

        // Compute subtotal and discount
        double subtotal = items.stream()
                .mapToDouble(item -> item.getScreening().getPrice() + getSeatExtra(item.getSeat()))
                .sum();

        Optional<PromoCode> validPromo = Optional.empty();
        double discountMultiplier = 1.0;

        if (promoCode != null && !promoCode.isBlank()) {
            validPromo = promoService.findValidForUser(promoCode, user.getUsername());
            if (validPromo.isPresent()) {
                discountMultiplier = 1.0 - (validPromo.get().getDiscountPercent() / 100.0);
            }
        }

        // Create order
        TicketOrder order = ticketOrderRepository.save(new TicketOrder(user));

        // Save tickets with discounted price
        for (CartItem item : items) {
            double basePrice = item.getScreening().getPrice() + getSeatExtra(item.getSeat());
            double paidPrice = Math.max(0, basePrice * discountMultiplier);
            ticketRepository.save(new Ticket(order, item.getScreening(), item.getSeat(), paidPrice));
        }

        // Record promo usage
        if (validPromo.isPresent()) {
            promoService.applyCode(validPromo.get(), user, order, subtotal);
        }

        cartItemRepository.deleteByUser_Username(user.getUsername());
        return order;
    }

    public List<TicketOrder> getCurrentUserOrders() {
        String username = getCurrentUsername();
        if (username == null) return List.of();
        return ticketOrderRepository.findByUser_UsernameOrderByPurchasedAtDesc(username);
    }

    public User requireCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) throw new IllegalStateException("No hay usuario autenticado");
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return null;
        String name = authentication.getName();
        if (name == null || "anonymousUser".equals(name)) return null;
        return name;
    }

    public double getSeatExtra(Seat seat) {
        return switch (seat.getType()) {
            case PREMIUM -> 2.0;
            default -> 0.0;
        };
    }

    public static final class AddSeatsResult {
        private final int added, alreadyMine, occupied;

        public AddSeatsResult(int added, int alreadyMine, int occupied) {
            this.added = added;
            this.alreadyMine = alreadyMine;
            this.occupied = occupied;
        }

        public int getAdded() { return added; }
        public int getAlreadyMine() { return alreadyMine; }
        public int getOccupied() { return occupied; }
    }
}
