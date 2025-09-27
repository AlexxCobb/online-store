package ru.zinovev.online.store.controller.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {BirthDateValidator.class})
public @interface ValidBirthDate {
    String message() default "Некорректная дата рождения";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
