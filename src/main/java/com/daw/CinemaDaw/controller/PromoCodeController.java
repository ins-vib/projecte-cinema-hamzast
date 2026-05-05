package com.daw.CinemaDaw.controller;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daw.CinemaDaw.domain.cinema.PromoCode;
import com.daw.CinemaDaw.service.PromoService;

@Controller
@RequestMapping("/admin/promos")
@PreAuthorize("hasRole('ADMIN')")
public class PromoCodeController {

    private final PromoService promoService;

    public PromoCodeController(PromoService promoService) {
        this.promoService = promoService;
    }

    /** List all promo codes */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("promos", promoService.findAll());
        return "admin/promos/promo-list";
    }

    /** Show create form */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("promo", new PromoCode());
        return "admin/promos/promo-form";
    }

    /** Save new promo code */
    @PostMapping("/new")
    public String create(PromoCode promo, Model model) {
        if (hasEmptyFields(promo)) {
            model.addAttribute("promo", promo);
            return FormValidation.withRequiredFieldsError(model, "admin/promos/promo-form");
        }

        // Validate discount range
        if (promo.getDiscountPercent() < 1 || promo.getDiscountPercent() > 100) {
            model.addAttribute("promo", promo);
            model.addAttribute("error", "El descuento debe estar entre 1% y 100%.");
            return "admin/promos/promo-form";
        }
        // Validate dates
        if (promo.getValidUntil().isBefore(promo.getValidFrom())) {
            model.addAttribute("promo", promo);
            model.addAttribute("error", "La fecha de fin debe ser posterior a la de inicio.");
            return "admin/promos/promo-form";
        }
        // Check duplicate code
        if (promoService.codeExists(promo.getCode())) {
            model.addAttribute("promo", promo);
            model.addAttribute("error", "Ya existe un código con ese nombre.");
            return "admin/promos/promo-form";
        }
        // maxUses >= 1
        if (promo.getMaxUses() < 1) {
            model.addAttribute("promo", promo);
            model.addAttribute("error", "El número máximo de usos debe ser al menos 1.");
            return "admin/promos/promo-form";
        }

        promoService.save(promo);
        return "redirect:/admin/promos?created=1";
    }

    /** Show edit form */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<PromoCode> opt = promoService.findById(id);
        if (opt.isEmpty()) return "redirect:/admin/promos";
        model.addAttribute("promo", opt.get());
        model.addAttribute("editing", true);
        return "admin/promos/promo-form";
    }

    /** Save edited promo code */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, PromoCode form, Model model) {
        Optional<PromoCode> opt = promoService.findById(id);
        if (opt.isEmpty()) return "redirect:/admin/promos";

        PromoCode existing = opt.get();

        if (hasEmptyFields(form)) {
            model.addAttribute("promo", existing);
            model.addAttribute("editing", true);
            return FormValidation.withRequiredFieldsError(model, "admin/promos/promo-form");
        }

        // Validate discount range
        if (form.getDiscountPercent() < 1 || form.getDiscountPercent() > 100) {
            model.addAttribute("promo", existing);
            model.addAttribute("editing", true);
            model.addAttribute("error", "El descuento debe estar entre 1% y 100%.");
            return "admin/promos/promo-form";
        }
        // Validate dates
        if (form.getValidUntil().isBefore(form.getValidFrom())) {
            model.addAttribute("promo", existing);
            model.addAttribute("editing", true);
            model.addAttribute("error", "La fecha de fin debe ser posterior a la de inicio.");
            return "admin/promos/promo-form";
        }

        // Update mutable fields (code cannot change once created)
        existing.setDiscountPercent(form.getDiscountPercent());
        existing.setValidFrom(form.getValidFrom());
        existing.setValidUntil(form.getValidUntil());
        existing.setMaxUses(form.getMaxUses());
        existing.setActive(form.isActive());

        promoService.save(existing);
        return "redirect:/admin/promos?updated=1";
    }

    /** Toggle active/inactive */
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        promoService.findById(id).ifPresent(promo -> {
            promo.setActive(!promo.isActive());
            promoService.save(promo);
        });
        return "redirect:/admin/promos";
    }

    /** Delete a promo code */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        promoService.deleteById(id);
        return "redirect:/admin/promos?deleted=1";
    }

    /** View all usages of a specific promo code */
    @GetMapping("/{id}/usages")
    public String usages(@PathVariable Long id, Model model) {
        Optional<PromoCode> opt = promoService.findById(id);
        if (opt.isEmpty()) return "redirect:/admin/promos";
        model.addAttribute("promo", opt.get());
        model.addAttribute("usages", promoService.getUsagesForCode(id));
        return "admin/promos/promo-usages";
    }

    private boolean hasEmptyFields(PromoCode promo) {
        return FormValidation.isBlank(promo.getCode())
                || promo.getDiscountPercent() <= 0
                || promo.getValidFrom() == null
                || promo.getValidUntil() == null
                || promo.getMaxUses() <= 0;
    }
}
