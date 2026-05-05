document.addEventListener("DOMContentLoaded", () => {
  const requiredMessage = "No pots deixar cap camp sense emplenar.";

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
