package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record ProductDto(
        @NotBlank
        @Size(min = 3, max = 150)
        String name,
        @NotBlank
        @Positive
        BigDecimal price,
        @NotBlank
        String categoryPublicId,
        Set<ParametersDto> parameters,
        @NotBlank
        @Positive
        BigDecimal weight,
        @NotBlank
        @Positive
        BigDecimal volume,
        @NotBlank
        @PositiveOrZero
        Integer stockQuantity
) {
}