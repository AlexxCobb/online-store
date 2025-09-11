package ru.zinovev.online.store.dao.entity.enums;

import lombok.Getter;

@Getter
public enum PaymentStatusName {
    PENDING("Ожидает оплаты"),
    PAID("Оплачен"),
    REJECTED("Оплата отклонена"),
    REFUND("Возврат денежных средств");

    private final String displayValue;

    private PaymentStatusName(String displayValue) {
        this.displayValue = displayValue;
    }
}
