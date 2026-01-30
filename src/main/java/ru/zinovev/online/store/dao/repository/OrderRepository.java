package ru.zinovev.online.store.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o.id FROM Order o WHERE o.user.publicUserId = :publicUserId ORDER BY o.createdAt DESC")
    Page<Long> findOrderIdsByPublicUserId(String publicUserId, Pageable pageable);

    @Query("SELECT o.id FROM Order o ORDER BY o.createdAt DESC")
    Page<Long> findAllOrderIds(Pageable pageable);

    @EntityGraph(attributePaths = {
            "address",
            "paymentMethod",
            "deliveryMethod",
            "paymentStatus",
            "orderStatus",
            "items",
            "items.product"
    })
    @Query("SELECT o FROM Order o WHERE o.id IN :orderIds ORDER BY o.createdAt DESC")
    List<Order> findUserOrdersWithDetails(List<Long> orderIds);

    @EntityGraph(attributePaths = {
            "address",
            "paymentMethod",
            "deliveryMethod",
            "paymentStatus",
            "orderStatus",
            "items",
            "items.product"
    })
    Optional<Order> findByPublicOrderId(String publicOrderId);

    @EntityGraph(attributePaths = {
            "address",
            "paymentMethod",
            "deliveryMethod",
            "paymentStatus",
            "orderStatus",
            "items",
            "items.product",
            "user"
    })
    @Query("SELECT o FROM Order o WHERE o.id IN :orderIds ORDER BY o.createdAt DESC")
    List<Order> findAll(List<Long> orderIds);
}

