package ru.zinovev.online.store.controller.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Month;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, LocalDate> {

    private final LocalDate startBirthDate = LocalDate.of(1930, Month.JANUARY, 1);
    private final LocalDate endBirthDate = LocalDate.now().minusYears(14);


    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.isBefore(startBirthDate)) {
            buildValidationError(context, "Дата рождения не может быть раньше 1930 года.");
            return false;
        }
        if (value.isAfter(endBirthDate)) {
            buildValidationError(context, "Пользователь должен быть не моложе 14 лет.");
            return false;
        }
        return true;
    }

    private void buildValidationError(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
