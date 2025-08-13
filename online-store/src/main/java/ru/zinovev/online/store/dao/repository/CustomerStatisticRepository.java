package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.CustomerStatistic;

public interface CustomerStatisticRepository extends JpaRepository<CustomerStatistic, Long> {
}
