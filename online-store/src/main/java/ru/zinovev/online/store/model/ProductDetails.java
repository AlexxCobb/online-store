package ru.zinovev.online.store.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record ProductDetails(
        String publicProductId,
        String name,
        BigDecimal price,
        String categoryPublicId,
        Set<ParametersDetails> parameters,
        BigDecimal weight,
        BigDecimal volume,
        Integer stockQuantity,
        String imagePath,
        Boolean isDiscount,
        BigDecimal discountPrice,
        OffsetDateTime createdAt
) {
}