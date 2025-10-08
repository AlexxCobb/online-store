package ru.zinovev.online.store.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.entity.ProductView;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByPublicProductId(String publicProductId);

    List<Product> findByCategoryPublicCategoryId(String publicCategoryId);

    @Query("SELECT p.id FROM Product p")
    Page<Long> findProductIds(Pageable pageable);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.parameters WHERE p.id IN :ids ORDER BY p.stockQuantity DESC")
    List<Product> findProductsWithParametersInIds(List<Long> ids);

    @Query("SELECT DISTINCT pp.value FROM ProductParameter pp WHERE pp.key = :key ORDER BY pp.value")
    Set<String> findUniqueParametersByKey(String key);

    @Query("SELECT MIN(p.price) FROM Product p")
    BigDecimal getMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    BigDecimal getMaxPrice();

    @Query("SELECT p.publicProductId as publicProductId, p.name as name, " +
            "p.price as price, " +
            "pp_brand.value as brand, " +
            "pp_color.value as color, " +
            "p.imagePath as imagePath, p.stockQuantity as stockQuantity " +
            "FROM Product p " +
            "LEFT JOIN p.parameters pp_brand ON pp_brand.key = 'brand' " +
            "LEFT JOIN p.parameters pp_color ON pp_color.key = 'color' " +
            "WHERE p.id IN " +
            "   (SELECT MIN(p2.id) " +
            "   FROM Product p2 WHERE p2.category.id IN :categoryIds AND p2.stockQuantity > 0 " +
            "   GROUP BY p2.category.id)")
    List<ProductView> getOneProductFromEachCategory(Collection<Long> categoryIds);

    Optional<Product> findByFingerprint(String fingerprint);

    @Query("SELECT p.publicProductId as publicProductId, p.name as name, " +
            "p.price as price, p.discountPrice as discountPrice, " +
            "pp_brand.value as brand, " +
            "pp_color.value as color, " +
            "p.imagePath as imagePath, p.stockQuantity as stockQuantity " +
            "FROM Product p " +
            "LEFT JOIN p.parameters pp_brand ON pp_brand.key = 'brand' " +
            "LEFT JOIN p.parameters pp_color ON pp_color.key = 'color' " +
            "WHERE p.isDiscount = TRUE " +
            "ORDER BY p.discountPrice ASC")
    List<ProductView> findDiscountProducts(Pageable pageable);

    @Query("SELECT p.publicProductId as publicProductId, p.name as name, " +
            "p.price as price, " +
            "pp_brand.value as brand, " +
            "pp_color.value as color, " +
            "p.imagePath as imagePath, p.stockQuantity as stockQuantity " +
            "FROM Product p " +
            "LEFT JOIN p.parameters pp_brand ON pp_brand.key = 'brand' " +
            "LEFT JOIN p.parameters pp_color ON pp_color.key = 'color' " +
            "WHERE p.createdAt >= :cutoffDate " +
            "ORDER BY p.createdAt DESC")
    List<ProductView> findNewProducts(OffsetDateTime cutoffDate, Pageable pageable);
}