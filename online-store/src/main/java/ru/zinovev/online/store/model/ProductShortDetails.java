package ru.zinovev.online.store.model;

import java.math.BigDecimal;

public record ProductShortDetails(
        String publicProductId,
        String name,
        BigDecimal price
) {
}
