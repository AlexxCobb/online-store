package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public record ProductUpdateDto(
        @Size(min = 3, max = 150)
        Optional<String> name,
        @Positive
        Optional<BigDecimal> price,
        Optional<String> categoryPublicId,
        Map<String, String> parameters,
        @PositiveOrZero
        Optional<Integer> stockQuantity
) {
}
