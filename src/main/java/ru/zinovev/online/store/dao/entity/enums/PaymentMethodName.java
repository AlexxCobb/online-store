package ru.zinovev.online.store.dao.entity.enums;

import lombok.Getter;

@Getter
public enum PaymentMethodName {
    CASH("Оплата наличными"),
    CARD("Оплата картой");
    private final String displayValue;

    private PaymentMethodName(String displayValue) {
        this.displayValue = displayValue;
    }
}
