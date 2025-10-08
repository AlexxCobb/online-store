package ru.zinovev.online.store.controller.dto;

import ru.zinovev.online.store.controller.dto.enums.ProductType;

import java.math.BigDecimal;

public record ProductForStandDto(
        String publicProductId,
        String name,
        BigDecimal price,
        Integer purchaseCount,
        String brand,
        String color,
        String imagePath,
        Integer stockQuantity,
        ProductType type
) {
}
