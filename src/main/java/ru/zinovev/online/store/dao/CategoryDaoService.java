package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.config.cache.Caches;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.mapper.CategoryMapper;
import ru.zinovev.online.store.dao.repository.CategoryRepository;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CategoryDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryDaoService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @CacheEvict(value = Caches.Category.ALL, key = "'all'")
    public CategoryDetails createCategory(CategoryDetails categoryDetails) {
        var newCategory = Category.builder()
                .publicCategoryId(UUID.randomUUID().toString())
                .name(categoryDetails.name().trim())
                .build();
        return categoryMapper.toCategoryDetails(categoryRepository.save(newCategory));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = Caches.Category.ALL, key = "'all'"),
            @CacheEvict(value = Caches.Category.BY_ID, key = "#categoryDetailsExist.publicCategoryId()")
    })
    public CategoryDetails updateCategory(CategoryDetails categoryDetailsExist, CategoryDetails categoryDetails) {
        var category = categoryRepository.findByPublicCategoryId(categoryDetailsExist.publicCategoryId())
                .orElseThrow(() -> new NotFoundException(
                        "Category with id - " + categoryDetails.publicCategoryId() + " not found"));
        var updateCategory = category.toBuilder()
                .name(categoryDetails.name())
                .build();
        return categoryMapper.toCategoryDetails(categoryRepository.save(updateCategory));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = Caches.Category.ALL, key = "'all'"),
            @CacheEvict(value = Caches.Category.BY_ID, key = "#publicCategoryId")
    })
    public void deleteCategory(String publicCategoryId) {
        var category = categoryRepository.findByPublicCategoryId(publicCategoryId)
                .orElseThrow(() -> new NotFoundException("Category with id - " + publicCategoryId + " not found"));
        categoryRepository.delete(category);
    }

    @Cacheable(value = Caches.Category.ALL, key = "'all'")
    public List<CategoryDetails> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryDetails)
                .collect(Collectors.toList());
    }

    public Optional<CategoryDetails> findByNameIgnoreCase(CategoryDetails categoryDetails) {
        return categoryRepository.findByNameIgnoreCase(categoryDetails.name().trim())
                .map(categoryMapper::toCategoryDetails);
    }

    @Cacheable(value = Caches.Category.BY_ID, key = "#publicCategoryId")
    public Optional<CategoryDetails> findByPublicId(String publicCategoryId) {
        return categoryRepository.findByPublicCategoryId(publicCategoryId).map(categoryMapper::toCategoryDetails);
    }

    public Boolean existCategories(List<String> publicIds) {
        return categoryRepository.existsByPublicCategoryIdIn(publicIds);
    }
}