package ru.zinovev.online.store.dao.entity;

import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface RevenueView {
    String getPublicOrderId();
    OrderStatusName getName();
    OffsetDateTime getCreatedAt();
    BigDecimal getTotalOrderSpent();
}
