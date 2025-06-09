package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
