package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    Optional<Category> findByPublicCategoryId(String publicCategoryId);

    boolean existsByPublicCategoryIdIn(List<String> publicCategoryIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Category c set c.name = :name where c.publicCategoryId = :publicCategoryId")
    int updateNameByPublicCategoryId(String name, String publicCategoryId);
}
