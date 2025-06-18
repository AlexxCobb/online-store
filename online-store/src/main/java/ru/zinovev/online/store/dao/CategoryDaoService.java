package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.mapper.CategoryMapper;
import ru.zinovev.online.store.dao.repository.CategoryRepository;
import ru.zinovev.online.store.model.CategoryDetails;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryDaoService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDetails createCategory(CategoryDto categoryDto) {
        var newCategory = Category.builder()
                .publicCategoryId(UUID.randomUUID().toString())
                .name(categoryDto.name())
                .build();
        return categoryMapper.toCategoryDetails(categoryRepository.save(newCategory));
    }

    @Transactional
    public CategoryDetails updateCategory(CategoryDetails categoryDetails, CategoryDto categoryDto) {
        var category = categoryRepository.findByPublicCategoryId(categoryDetails.publicCategoryId()).get();
        var updateCategory = category.toBuilder()
                .name(categoryDto.name())
                .build();
        return categoryMapper.toCategoryDetails(categoryRepository.save(updateCategory));
    }

    @Transactional
    public void deleteCategory(CategoryDetails categoryDetails) {
        categoryRepository.delete(categoryMapper.toCategory(categoryDetails));
    }


    public Optional<CategoryDetails> findByNameIgnoreCase(CategoryDto categoryDto) {
        return categoryRepository.findByNameIgnoreCase(categoryDto.name()).map(categoryMapper::toCategoryDetails);
    }

    public Optional<CategoryDetails> findByPublicId(String publicCategoryId) {
        return categoryRepository.findByPublicCategoryId(publicCategoryId).map(categoryMapper::toCategoryDetails);
    }
}
