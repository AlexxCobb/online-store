package ru.zinovev.online.store.dao.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.CustomerStatistic;
import ru.zinovev.online.store.dao.entity.CustomerView;
import ru.zinovev.online.store.dao.entity.Order;
import ru.zinovev.online.store.dao.entity.RevenueView;

import java.time.OffsetDateTime;
import java.util.List;

public interface CustomerStatisticRepository extends JpaRepository<CustomerStatistic, Long> {

    @Query("SELECT u.publicUserId as publicUserId, u.email as email, u.name as name, u.lastname as lastname, SUM(cs.totalSpent) as totalSpent "
            + "FROM CustomerStatistic cs "
            + "JOIN cs.user u "
            + "WHERE cs.createdAt "
            + "BETWEEN :dateFrom AND :dateTo "
            + "GROUP BY u.publicUserId, u.email, u.name, u.lastname "
            + "ORDER BY SUM(cs.totalSpent) DESC")
    List<CustomerView> findTopCustomers(Pageable page, OffsetDateTime dateFrom, OffsetDateTime dateTo);

    @Query("SELECT o.publicOrderId as publicOrderId, os.name as name, o.createdAt as createdAt, cs.totalSpent as totalOrderSpent, SUM(cs.totalSpent) OVER () as totalRevenue "
            + "FROM CustomerStatistic cs "
            + "JOIN cs.order o "
            + "JOIN o.orderStatus os "
            + "WHERE o.createdAt "
            + "BETWEEN :dateFrom AND :dateTo "
            + "AND os.name = DELIVERED "
            + "GROUP BY o.publicOrderId, os.name, o.createdAt, cs.totalSpent  "
            + "ORDER BY o.createdAt DESC")
    List<RevenueView> findRevenueByPeriod(Pageable page, OffsetDateTime dateFrom, OffsetDateTime dateTo);

    void deleteByOrder(Order order);
}