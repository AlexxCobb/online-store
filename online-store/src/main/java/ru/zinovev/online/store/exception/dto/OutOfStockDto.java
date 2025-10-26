package ru.zinovev.online.store.exception.dto;

public record OutOfStockDto(
        String productName,
        Integer requestedQuantity,
        Integer availableQuantity
) {
}
