package ru.zinovev.online.store.dao.entity;

import java.math.BigDecimal;

public interface ProductView {
    String getPublicProductId();
    String getName();
    BigDecimal getPrice();
}
