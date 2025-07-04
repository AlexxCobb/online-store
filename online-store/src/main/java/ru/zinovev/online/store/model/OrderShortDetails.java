package ru.zinovev.online.store.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.zinovev.online.store.dao.entity.DeliveryMethod;
import ru.zinovev.online.store.dao.entity.OrderStatus;
import ru.zinovev.online.store.dao.entity.PaymentStatus;
import ru.zinovev.online.store.model.constants.Constants;

import java.time.OffsetDateTime;

public record OrderShortDetails(
        String publicOrderId,
        AddressShortDetails addressDetails,
        ProductShortDetails productDetails,
        DeliveryMethod deliveryMethod,
        PaymentStatus paymentStatus,
        OrderStatus orderStatus,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN)
        OffsetDateTime createdAt
) {
}
