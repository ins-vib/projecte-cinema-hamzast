package com.daw.CinemaDaw.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daw.CinemaDaw.domain.cinema.PromoCode;
import com.daw.CinemaDaw.service.CartService;
import com.daw.CinemaDaw.service.PromoService;
import com.daw.CinemaDaw.service.PromoService.ValidationResult;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/client/cart/promo")
public class PromoCartController {

    /** Session key used to store the validated promo code string. */
    public static final String SESSION_PROMO_KEY = "appliedPromoCode";

    private final PromoService promoService;
    private final CartService cartService;

    public PromoCartController(PromoService promoService, CartService cartService) {
        this.promoService = promoService;
        this.cartService = cartService;
    }

    /**
     * POST /client/cart/promo/apply
     * Validates the code and, if OK, stores it in the session so the cart
     * can display the discounted total.
     */
    @PostMapping("/apply")
    public String apply(@RequestParam String promoCode,
                        HttpSession session,
                        RedirectAttributes ra) {

        String username = cartService.getCurrentUsername();
        if (username == null) {
            return "redirect:/login";
        }

        String code = promoCode.toUpperCase().trim();
        ValidationResult result = promoService.validate(code, username);

        if (result != ValidationResult.OK) {
            ra.addFlashAttribute("promoError", promoService.errorMessage(result));
            return "redirect:/client/cart";
        }

        // Store validated code in session — it will be consumed at checkout
        session.setAttribute(SESSION_PROMO_KEY, code);
        ra.addFlashAttribute("promoSuccess", "Código '" + code + "' aplicado correctamente.");
        return "redirect:/client/cart";
    }

    /**
     * POST /client/cart/promo/remove
     * Removes the promo code from the session.
     */
    @PostMapping("/remove")
    public String remove(HttpSession session, RedirectAttributes ra) {
        session.removeAttribute(SESSION_PROMO_KEY);
        ra.addFlashAttribute("promoSuccess", "Código promocional eliminado.");
        return "redirect:/client/cart";
    }
}
