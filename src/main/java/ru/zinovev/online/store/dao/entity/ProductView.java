package ru.zinovev.online.store.dao.entity;

import java.math.BigDecimal;

public interface ProductView {
    String getPublicProductId();
    String getName();
    BigDecimal getPrice();
    Integer getPurchaseCount();
    String getBrand();
    String getColor();
    String getImagePath();
    Integer getStockQuantity();
    BigDecimal getDiscountPrice();
}