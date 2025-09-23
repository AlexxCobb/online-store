package ru.zinovev.online.store.model;

import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RevenueDetails(
        String publicOrderId,
        OrderStatusName name,
        OffsetDateTime createdAt,
        BigDecimal totalOrderSpent
) {
}
