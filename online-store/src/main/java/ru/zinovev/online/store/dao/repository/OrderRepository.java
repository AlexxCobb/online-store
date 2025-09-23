package ru.zinovev.online.store.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserPublicUserIdOrderByCreatedAtDesc(String publicUserId, Pageable pageable);

    Optional<Order> findByPublicOrderId(String publicOrderId);
}

