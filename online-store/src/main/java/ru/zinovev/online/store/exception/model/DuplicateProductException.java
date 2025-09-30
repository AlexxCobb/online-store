package ru.zinovev.online.store.exception.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DuplicateProductException extends RuntimeException {
    private final String publicProductId;
    private final String name;
    private final Integer stockQuantity;
    private final BigDecimal price;

    public DuplicateProductException(String message, String publicProductId, String name, Integer stockQuantity,
                                     BigDecimal price) {
        super(message);
        this.publicProductId = publicProductId;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
    }
}
