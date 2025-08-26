package ru.zinovev.online.store.model;

import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;

import java.time.OffsetDateTime;

public record OrderShortDetails(
        String publicOrderId,
        UserDetails userDetails,
        DeliveryMethodName deliveryMethodName,
        PaymentStatusName paymentStatusName,
        OrderStatusName orderStatusName,
        OffsetDateTime createdAt
) {
}
