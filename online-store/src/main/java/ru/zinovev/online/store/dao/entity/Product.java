package ru.zinovev.online.store.dao.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Set;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    @Builder.Default
    private Set<ProductParameter> parameters = new HashSet<>();

    @Column(name = "product_weight")
    private BigDecimal weight;

    @Column(name = "product_volume")
    private BigDecimal volume;

    @Column(name = "product_stock_quantity")
    private Integer stockQuantity;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "product_fingerprint")
    private String fingerprint;

    @Column(name = "is_discount")
    private Boolean isDiscount;

    @Column(name = "discount_price")
    private BigDecimal discountPrice;

    @Column(name = "created_at")
    @CreationTimestamp
    private OffsetDateTime createdAt;

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

    @PrePersist
    @PreUpdate
    public void calculateProductFingerprint() {

        var parametersMap =
                parameters.stream().collect(Collectors.toMap(ProductParameter::getKey, ProductParameter::getValue));

        final var NULL_PLACEHOLDER = "NULL";

        var brand = parametersMap.getOrDefault("brand", NULL_PLACEHOLDER);
        var color = parametersMap.getOrDefault("color", NULL_PLACEHOLDER);
        var ram = parametersMap.getOrDefault("ram", NULL_PLACEHOLDER);
        var memory = parametersMap.getOrDefault("memory", NULL_PLACEHOLDER);

        var canonicalString =
                String.join("|", name.toUpperCase(), brand.toUpperCase(), color.toUpperCase(), ram.toUpperCase(),
                            memory.toUpperCase());

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            var encodedHash = digest.digest(
                    canonicalString.getBytes(StandardCharsets.UTF_8));
            this.fingerprint = HexFormat.of().formatHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}