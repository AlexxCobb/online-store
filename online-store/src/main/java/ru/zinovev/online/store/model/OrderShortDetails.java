package ru.zinovev.online.store.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;
import ru.zinovev.online.store.model.constants.Constants;

import java.time.OffsetDateTime;

public record OrderShortDetails(
        String publicOrderId,
        UserDetails userDetails,
        DeliveryMethodName deliveryMethodName,
        PaymentStatusName paymentStatusName,
        OrderStatusName orderStatusName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN)
        OffsetDateTime createdAt
) {
}
