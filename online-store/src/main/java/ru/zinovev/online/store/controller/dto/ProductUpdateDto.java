package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateDto(
        @Size(min = 3, max = 150)
        String name,
        @Positive
        BigDecimal price,
        String categoryPublicId,
        @PositiveOrZero
        Integer stockQuantity
) {
}