package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.dao.CategoryDaoService;
import ru.zinovev.online.store.model.CategoryDetails;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryDaoService categoryDaoService;

    public CategoryDetails createCategory(CategoryDto categoryDto) {
        categoryDaoService.findByNameIgnoreCase(categoryDto)
                .ifPresent(categoryDetails -> {
                    throw new RuntimeException("Category with name - " + categoryDto.name() + " already exist");
                }); //400
        return categoryDaoService.createCategory(categoryDto);
    }

    public CategoryDetails updateCategory(@NonNull String publicCategoryId, CategoryDto categoryDto) {
        var categoryDetails = existCategoryDetails(publicCategoryId);
        if (!categoryDetails.name().equalsIgnoreCase(categoryDto.name())) {
            return categoryDaoService.updateCategory(categoryDetails, categoryDto);
        } else {
            throw new RuntimeException("Category with name - " + categoryDto.name() + " already exist"); // 400
        }
    }

    public void deleteCategory(@NonNull String publicCategoryId) {
        categoryDaoService.deleteCategory(existCategoryDetails(publicCategoryId));
    }

    private CategoryDetails existCategoryDetails(String publicCategoryId) {
        var currentCategoryDetails = categoryDaoService.findByPublicId(publicCategoryId);
        return currentCategoryDetails.orElseThrow(
                () -> new RuntimeException("Category with id - " + publicCategoryId + " not found")); // 404
    }
}
