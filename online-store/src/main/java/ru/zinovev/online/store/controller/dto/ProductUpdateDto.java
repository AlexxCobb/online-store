package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public record ProductUpdateDto(
        @Size(min = 3, max = 150)
        String name,
        @Positive
        BigDecimal price,
        String categoryPublicId,
        Set<ParametersDto> parameters,
        @PositiveOrZero
        Integer stockQuantity
) {
    public Optional<String> getOptionalName() {
        return Optional.ofNullable(name);
    }

    public Optional<BigDecimal> getOptionalPrice() {
        return Optional.ofNullable(price);
    }

    public Optional<String> getOptionalCategoryPublicId() {
        return Optional.ofNullable(categoryPublicId);
    }

    public Optional<Integer> getOptionalStockQuantity() {
        return Optional.ofNullable(stockQuantity);
    }
}
