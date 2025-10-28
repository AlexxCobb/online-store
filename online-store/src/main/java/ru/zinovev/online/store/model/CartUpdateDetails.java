package ru.zinovev.online.store.model;

public record CartUpdateDetails(
        String publicProductId,
        Integer availableQuantity
) {
}
