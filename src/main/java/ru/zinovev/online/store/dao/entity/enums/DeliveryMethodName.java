package ru.zinovev.online.store.dao.entity.enums;

import lombok.Getter;

@Getter
public enum DeliveryMethodName {
    BY_SELF("Самовывоз из магазина"),
    COURIER("Доставка курьером"),
    PARCEL_LOCKER("Пункт выдачи");

    private final String displayValue;

    private DeliveryMethodName(String displayValue) {
        this.displayValue = displayValue;
    }
}
