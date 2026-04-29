package com.daw.CinemaDaw.domain.cinema;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "promo_codes")
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    /** Percentage discount: 0-100 */
    @Column(nullable = false)
    private int discountPercent;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validUntil;

    /** Maximum number of total uses (across all users). */
    @Column(nullable = false)
    private int maxUses;

    /** How many times this code has already been used. */
    @Column(nullable = false)
    private int timesUsed = 0;

    @Column(nullable = false)
    private boolean active = true;

    public PromoCode() {}

    public PromoCode(String code, int discountPercent, LocalDate validFrom,
                     LocalDate validUntil, int maxUses) {
        this.code = code.toUpperCase().trim();
        this.discountPercent = discountPercent;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.maxUses = maxUses;
    }

    // ── business helpers ──────────────────────────────────────────────────────

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return active
                && !today.isBefore(validFrom)
                && !today.isAfter(validUntil)
                && timesUsed < maxUses;
    }

    /** Returns the discount amount for a given subtotal. */
    public double applyTo(double subtotal) {
        return subtotal * discountPercent / 100.0;
    }

    public void incrementUsage() {
        this.timesUsed++;
    }

    // ── getters / setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code.toUpperCase().trim(); }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }

    public int getTimesUsed() { return timesUsed; }
    public void setTimesUsed(int timesUsed) { this.timesUsed = timesUsed; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
