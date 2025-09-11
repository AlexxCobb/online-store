package ru.zinovev.online.store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.zinovev.online.store.dao.entity.enums.DeliveryMethodName;
import ru.zinovev.online.store.dao.entity.enums.PaymentMethodName;

public record OrderDto(
        @NotBlank
        String publicAddressId,
        @NotNull
        PaymentMethodName paymentMethodName,
        @NotNull
        DeliveryMethodName deliveryMethodName
) {
}