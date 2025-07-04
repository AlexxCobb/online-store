package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.PaymentMethod;
import ru.zinovev.online.store.dao.entity.enums.PaymentMethodName;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

    PaymentMethod getByName(PaymentMethodName name);
}
