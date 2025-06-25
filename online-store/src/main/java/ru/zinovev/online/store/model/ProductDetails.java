package ru.zinovev.online.store.model;

import java.math.BigDecimal;
import java.util.Map;

public record ProductDetails(
        String publicProductId,
        String name,
        BigDecimal price,
        CategoryDetails categoryDetails,
        Map<String, String> parameters,
        BigDecimal weight,
        BigDecimal volume,
        Integer stockQuantity
) {
}