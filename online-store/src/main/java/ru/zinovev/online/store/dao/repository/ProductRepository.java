package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.Product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByPublicProductId(String publicProductId);

    List<Product> findByCategoryPublicCategoryId(String publicCategoryId);

    boolean existsByPublicProductIdIn(List<String> productIds);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.parameters")
    List<Product> findAllWithParameters();
}
