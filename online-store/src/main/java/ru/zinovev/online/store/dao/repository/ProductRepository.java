package ru.zinovev.online.store.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByPublicProductId(String publicProductId);

    List<Product> findByCategoryPublicCategoryId(String publicCategoryId);

    boolean existsByPublicProductIdIn(List<String> productIds);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.parameters",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p")
    Page<Product> findAllWithParameters(Pageable pageable);

    @Query("SELECT DISTINCT pp.value FROM ProductParameter pp WHERE pp.key = :key ORDER BY pp.value")
    Set<String> findUniqueParametersByKey(String key);

    @Query("SELECT MIN(p.price) FROM Product p")
    BigDecimal getMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    BigDecimal getMaxPrice();
}
