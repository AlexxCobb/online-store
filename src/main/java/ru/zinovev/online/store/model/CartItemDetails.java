package ru.zinovev.online.store.model;

import java.math.BigDecimal;

public record CartItemDetails(
        String publicProductId,
        String name,
        String brand,
        String color,
        Integer quantity,
        BigDecimal price,
        String imagePath
) {
}
