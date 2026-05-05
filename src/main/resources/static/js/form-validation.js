document.addEventListener("DOMContentLoaded", () => {
  const requiredMessage = "No puedes dejar ningún campo sin rellenar.";

  document.querySelectorAll("input[required], select[required], textarea[required]").forEach((field) => {
    field.addEventListener("invalid", () => {
      if (field.validity.valueMissing) {
        field.setCustomValidity(requiredMessage);
      }
    });

    field.addEventListener("input", () => field.setCustomValidity(""));
    field.addEventListener("change", () => field.setCustomValidity(""));
  });
});
