package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
