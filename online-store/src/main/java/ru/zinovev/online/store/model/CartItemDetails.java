package ru.zinovev.online.store.model;

public record CartItemDetails(
        String publicProductId,
        Integer quantity
) {
}
