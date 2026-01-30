package ru.zinovev.online.store.dao.entity;

import java.math.BigDecimal;

public interface CustomerView {
    String getPublicUserId();
    String getEmail();
    String getName();
    String getLastname();
    BigDecimal getTotalSpent();
}
