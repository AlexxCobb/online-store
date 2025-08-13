package ru.zinovev.online.store.dao.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.ProductStatistic;
import ru.zinovev.online.store.dao.entity.ProductView;

import java.util.List;

public interface ProductStatisticRepository extends JpaRepository<ProductStatistic, Long> {

    @Query("SELECT p.publicProductId as publicProductId, p.name as name, p.price as price " +
            "FROM ProductStatistic ps " +
            "JOIN ps.product p " +
            "GROUP BY p.id, p.publicProductId, p.name, p.price " +
            "ORDER BY SUM(ps.purchaseCount) DESC")
    List<ProductView> findTopProductViews(Pageable pageable);
}