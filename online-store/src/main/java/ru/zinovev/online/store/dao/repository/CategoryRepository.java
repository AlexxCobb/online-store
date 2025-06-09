package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
