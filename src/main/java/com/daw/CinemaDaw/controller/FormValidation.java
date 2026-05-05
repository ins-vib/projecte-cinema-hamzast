package com.daw.CinemaDaw.controller;

import org.springframework.ui.Model;

public final class FormValidation {

    public static final String REQUIRED_FIELDS_MESSAGE = "No puedes dejar ningún campo sin rellenar.";

    private FormValidation() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String withRequiredFieldsError(Model model, String viewName) {
        model.addAttribute("error", REQUIRED_FIELDS_MESSAGE);
        return viewName;
    }
}
