package ru.zinovev.online.store.exception.dto;

public record OutOfStockDto(
        String publicProductId,
        String productName,
        Integer requestedQuantity,
        Integer availableQuantity
) {
}
