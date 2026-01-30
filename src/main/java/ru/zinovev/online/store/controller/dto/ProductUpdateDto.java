package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateDto(
        @Size(max = 150, message = "Название должно содержать до {max} символов")
        String name,
        @Positive(message = "Цена товара только положительная")
        BigDecimal price,
        String categoryPublicId,
        @PositiveOrZero(message = "Количество товара положительное число или 0")
        Integer stockQuantity,
        Boolean isDiscount,
        @Positive(message = "Цена товара только положительная")
        BigDecimal discountPrice
) {
}