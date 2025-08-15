package ru.zinovev.online.store.model;

import java.math.BigDecimal;

public record TopCustomerDetails(
        String publicUserId,
        String name,
        String lastname,
        String email,
        BigDecimal totalSpent
) {
}
