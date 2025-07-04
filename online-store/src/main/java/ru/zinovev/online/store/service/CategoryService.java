package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.dao.CategoryDaoService;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CategoryDetails;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryDaoService categoryDaoService;

    public CategoryDetails createCategory(@NonNull CategoryDto categoryDto) {
        categoryDaoService.findByNameIgnoreCase(categoryDto)
                .ifPresent(categoryDetails -> {
                    throw new BadRequestException("Category with name - " + categoryDto.name() + " already exist");
                });
        return categoryDaoService.createCategory(categoryDto);
    }

    public CategoryDetails updateCategory(@NonNull String publicCategoryId, @NonNull CategoryDto categoryDto) {
        var categoryDetails = existCategory(publicCategoryId);
        if (!categoryDetails.name().equalsIgnoreCase(categoryDto.name())) {
            return categoryDaoService.updateCategory(categoryDetails, categoryDto);
        } else {
            throw new BadRequestException("Category with name - " + categoryDto.name() + " already exist");
        }
    }

    public void deleteCategory(@NonNull String publicCategoryId) {
        categoryDaoService.deleteCategory(existCategory(publicCategoryId));
    }

    public CategoryDetails existCategory(String publicCategoryId) {
        return categoryDaoService.findByPublicId(publicCategoryId).orElseThrow(
                () -> new NotFoundException("Category with id - " + publicCategoryId + " not found"));
    }
}
