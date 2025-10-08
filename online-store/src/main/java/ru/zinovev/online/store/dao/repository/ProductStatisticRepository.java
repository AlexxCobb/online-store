package ru.zinovev.online.store.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.Order;
import ru.zinovev.online.store.dao.entity.ProductStatistic;
import ru.zinovev.online.store.dao.entity.ProductView;

public interface ProductStatisticRepository extends JpaRepository<ProductStatistic, Long> {

    @Query("SELECT p.publicProductId as publicProductId, p.name as name, " +
            "p.price as price, SUM(ps.purchaseCount) as purchaseCount, " +
            "pp_brand.value as brand, " +
            "pp_color.value as color, " +
            "p.imagePath as imagePath, p.stockQuantity as stockQuantity " +
            "FROM ProductStatistic ps " +
            "JOIN ps.product p " +
            "LEFT JOIN p.parameters pp_brand ON pp_brand.key = 'brand' " +
            "LEFT JOIN p.parameters pp_color ON pp_color.key = 'color' " +
            "GROUP BY p.publicProductId, p.name, p.price, " +
            "pp_brand.value, pp_color.value, p.imagePath, p.stockQuantity " +
            "ORDER BY SUM(ps.purchaseCount) DESC")
    Page<ProductView> findTopProductViews(Pageable pageable);

    void deleteByOrder(Order order);
}