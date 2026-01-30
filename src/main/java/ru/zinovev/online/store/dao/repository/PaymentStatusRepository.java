package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.PaymentStatus;
import ru.zinovev.online.store.dao.entity.enums.PaymentStatusName;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {

    PaymentStatus getByName(PaymentStatusName name);
}
