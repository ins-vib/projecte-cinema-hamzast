package com.daw.CinemaDaw.controller;

import org.springframework.ui.Model;

public final class FormValidation {

    public static final String REQUIRED_FIELDS_MESSAGE = "No pots deixar cap camp sense emplenar.";

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
