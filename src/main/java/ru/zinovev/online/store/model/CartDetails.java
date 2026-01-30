package ru.zinovev.online.store.model;

import java.math.BigDecimal;
import java.util.List;

public record CartDetails(
        String publicCartId,
        String publicUserId,
        List<CartItemDetails> cartItems,
        BigDecimal totalPrice,
        Integer totalQuantity
) {
}
