package com.daw.CinemaDaw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.CinemaDaw.domain.cinema.PromoCode;
import com.daw.CinemaDaw.domain.cinema.PromoUsage;
import com.daw.CinemaDaw.domain.cinema.TicketOrder;
import com.daw.CinemaDaw.domain.cinema.user.User;
import com.daw.CinemaDaw.repository.PromoCodeRepository;
import com.daw.CinemaDaw.repository.PromoUsageRepository;

@Service
public class PromoService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoUsageRepository promoUsageRepository;

    public PromoService(PromoCodeRepository promoCodeRepository,
                        PromoUsageRepository promoUsageRepository) {
        this.promoCodeRepository = promoCodeRepository;
        this.promoUsageRepository = promoUsageRepository;
    }

    // ── Admin: CRUD ───────────────────────────────────────────────────────────

    public List<PromoCode> findAll() {
        return promoCodeRepository.findAll();
    }

    public Optional<PromoCode> findById(Long id) {
        return promoCodeRepository.findById(id);
    }

    @Transactional
    public PromoCode save(PromoCode promoCode) {
        return promoCodeRepository.save(promoCode);
    }

    @Transactional
    public void deleteById(Long id) {
        promoCodeRepository.deleteById(id);
    }

    public boolean codeExists(String code) {
        return promoCodeRepository.existsByCode(code.toUpperCase().trim());
    }

    public List<PromoUsage> getUsagesForCode(Long promoCodeId) {
        return promoUsageRepository.findByPromoCode_IdOrderByUsedAtDesc(promoCodeId);
    }

    // ── Client: validate & apply ──────────────────────────────────────────────

    public enum ValidationResult {
        OK,
        NOT_FOUND,
        EXPIRED,
        MAX_USES_REACHED,
        ALREADY_USED_BY_USER,
        INACTIVE
    }

    /**
     * Validates a promo code for a given user WITHOUT applying it.
     * Used to preview the discount in the cart.
     */
    public ValidationResult validate(String code, String username) {
        Optional<PromoCode> opt = promoCodeRepository.findByCode(code.toUpperCase().trim());
        if (opt.isEmpty()) return ValidationResult.NOT_FOUND;

        PromoCode promo = opt.get();

        if (!promo.isActive()) return ValidationResult.INACTIVE;

        LocalDate today = LocalDate.now();
        if (today.isBefore(promo.getValidFrom()) || today.isAfter(promo.getValidUntil())) {
            return ValidationResult.EXPIRED;
        }

        if (promo.getTimesUsed() >= promo.getMaxUses()) {
            return ValidationResult.MAX_USES_REACHED;
        }

        if (promoUsageRepository.existsByPromoCode_IdAndUser_Username(promo.getId(), username)) {
            return ValidationResult.ALREADY_USED_BY_USER;
        }

        return ValidationResult.OK;
    }

    /**
     * Returns the PromoCode if valid for this user, empty otherwise.
     */
    public Optional<PromoCode> findValidForUser(String code, String username) {
        if (validate(code, username) != ValidationResult.OK) {
            return Optional.empty();
        }
        return promoCodeRepository.findByCode(code.toUpperCase().trim());
    }

    /**
     * Applies a promo code to an order: records the usage and increments the counter.
     * Must be called inside a transaction (e.g. from CartService.checkoutCurrentUserCart).
     */
    @Transactional
    public PromoUsage applyCode(PromoCode promo, User user, TicketOrder order, double subtotal) {
        double saved = promo.applyTo(subtotal);
        promo.incrementUsage();
        promoCodeRepository.save(promo);

        PromoUsage usage = new PromoUsage(promo, user, order, saved);
        return promoUsageRepository.save(usage);
    }

    /** Converts a ValidationResult to a human-readable Spanish error message. */
    public String errorMessage(ValidationResult result) {
        return switch (result) {
            case NOT_FOUND          -> "El código promocional no existe.";
            case EXPIRED            -> "Este código ha caducado o aún no está vigente.";
            case MAX_USES_REACHED   -> "Este código ha alcanzado el límite de usos.";
            case ALREADY_USED_BY_USER -> "Ya has utilizado este código anteriormente.";
            case INACTIVE           -> "Este código está desactivado.";
            default                 -> "Código inválido.";
        };
    }
}
