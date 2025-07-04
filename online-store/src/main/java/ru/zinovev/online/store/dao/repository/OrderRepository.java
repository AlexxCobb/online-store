package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserPublicUserIdOrderByCreatedAtAsc(String publicUserId);

    Optional<Order> findByPublicOrderId(String publicOrderId);
}
