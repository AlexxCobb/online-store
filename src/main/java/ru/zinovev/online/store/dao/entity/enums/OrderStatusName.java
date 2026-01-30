package ru.zinovev.online.store.dao.entity.enums;

import lombok.Getter;

@Getter
public enum OrderStatusName {
    PENDING_PAYMENT("Ожидает оплаты"),
    PENDING_DELIVERY("Ожидает отгрузки"),
    SHIPPED("Отгружен"),
    DELIVERED("Доставлен"),
    CANCELLED("Отменен");

    private final String displayValue;

    private OrderStatusName(String displayValue) {
        this.displayValue = displayValue;
    }
}
