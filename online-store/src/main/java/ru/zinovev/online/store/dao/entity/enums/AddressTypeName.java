package ru.zinovev.online.store.dao.entity.enums;

import lombok.Getter;

@Getter
public enum AddressTypeName {
    USER_ADDRESS("Адрес покупателя"),
    STORE_ADDRESS("Магазин"),
    PARCEL_LOCKER("Пункт выдачи");

    private final String displayValue;

    private AddressTypeName(String displayValue) {
        this.displayValue = displayValue;
    }
}
