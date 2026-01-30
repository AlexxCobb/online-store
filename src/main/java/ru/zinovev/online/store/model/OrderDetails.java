package ru.zinovev.online.store.model;

import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentMethodName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderDetails(
        String publicOrderId,
        UserDetails userDetails,
        AddressShortDetails addressShortDetails,
        PaymentMethodName paymentMethodName,
        DeliveryMethodName deliveryMethodName,
        List<ProductShortDetails> productShortDetails,
        PaymentStatusName paymentStatusName,
        OrderStatusName orderStatusName,
        OffsetDateTime createdAt
) {
}