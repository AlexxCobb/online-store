package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;
import ru.zinovev.online.store.dao.entity.enums.PaymentMethodName;

public record OrderDto(
        @NotBlank
        String publicAddressId,
        @NotBlank
        PaymentMethodName paymentMethodName,
        @NotBlank
        DeliveryMethodName deliveryMethodName
) {
}
