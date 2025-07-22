package ru.zinovev.online.store.model;

import java.math.BigDecimal;

public record ProductUpdateDetails(
        String name,
        BigDecimal price,
        String categoryPublicId,
        Integer stockQuantity
) {
}
