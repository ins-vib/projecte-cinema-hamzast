package com.daw.CinemaDaw.domain.cinema;

import java.time.LocalDateTime;

import com.daw.CinemaDaw.domain.cinema.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Records each time a user successfully applies a promo code to an order.
 * The unique constraint prevents the same user from using the same code twice.
 */
@Entity
@Table(name = "promo_usages",
       uniqueConstraints = @UniqueConstraint(columnNames = {"promo_code_id", "user_id"}))
public class PromoUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private TicketOrder order;

    /** The discount amount that was actually saved (in euros). */
    @Column(nullable = false)
    private double savedAmount;

    @Column(nullable = false)
    private LocalDateTime usedAt = LocalDateTime.now();

    public PromoUsage() {}

    public PromoUsage(PromoCode promoCode, User user, TicketOrder order, double savedAmount) {
        this.promoCode = promoCode;
        this.user = user;
        this.order = order;
        this.savedAmount = savedAmount;
        this.usedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PromoCode getPromoCode() { return promoCode; }
    public void setPromoCode(PromoCode promoCode) { this.promoCode = promoCode; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TicketOrder getOrder() { return order; }
    public void setOrder(TicketOrder order) { this.order = order; }

    public double getSavedAmount() { return savedAmount; }
    public void setSavedAmount(double savedAmount) { this.savedAmount = savedAmount; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
}
