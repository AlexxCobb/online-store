package ru.zinovev.online.store.exception.model;

import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException {
    private final String productName;
    private final int requestedQuantity;
    private final int availableQuantity;

    public OutOfStockException(String message, String productName, int requestedQuantity, int availableQuantity) {
        super(message);
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
}
