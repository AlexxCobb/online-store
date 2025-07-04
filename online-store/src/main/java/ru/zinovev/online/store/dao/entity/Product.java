package ru.zinovev.online.store.dao.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_public_id")
    private String publicProductId;

    @Column(name = "product_name")
    private String name;

    @Column(name = "product_price")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_parameters", joinColumns = {@JoinColumn(name = "product_id")})
    @MapKeyColumn(name = "param_key")
    @Column(name = "param_value")
    @Builder.Default
    private Map<String, String> parameters = new HashMap<>();

    @Column(name = "product_weight")
    private BigDecimal weight;

    @Column(name = "product_volume")
    private BigDecimal volume;

    @Column(name = "product_stock_quantity")
    private Integer stockQuantity;

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Product product))
            return false;
        return id != null && id.equals(product.id);
    }
}