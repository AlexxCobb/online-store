package ru.zinovev.online.store.model;

import java.math.BigDecimal;

public record TopProductDetails(
        String publicProductId,
        String name,
        BigDecimal price,
        Integer purchaseCount,
        String brand,
        String color,
        String imagePath,
        Integer stockQuantity
) {
}