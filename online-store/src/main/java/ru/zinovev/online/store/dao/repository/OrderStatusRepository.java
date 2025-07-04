package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.OrderStatus;
import ru.zinovev.online.store.dao.entity.enums.OrderStatusName;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {

    OrderStatus getByName(OrderStatusName name);
}
