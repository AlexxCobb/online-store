package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.CategoryDaoService;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CategoryDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryDaoService categoryDaoService;
    private final UserService userService;

    public CategoryDetails createCategory(@NonNull String publicUserId, @NonNull CategoryDetails categoryDetails) {
        userService.findUserDetails(publicUserId);
        categoryDaoService.findByNameIgnoreCase(categoryDetails)
                .ifPresent(categoryDetailsExist -> {
                    throw new BadRequestException("Category with name - " + categoryDetails.name() + " already exist");
                });
        return categoryDaoService.createCategory(categoryDetails);
    }

    public CategoryDetails updateCategory(@NonNull String publicUserId, @NonNull String publicCategoryId,
                                          @NonNull CategoryDetails categoryDetails) {
        userService.findUserDetails(publicUserId);
        var categoryDetailsExist = existCategory(publicCategoryId);
        if (!categoryDetailsExist.name().equalsIgnoreCase(categoryDetails.name())) {
            return categoryDaoService.updateCategory(categoryDetailsExist, categoryDetails);
        } else {
            throw new BadRequestException("Category with name - " + categoryDetails.name() + " already exist");
        }
    }

    public void deleteCategory(@NonNull String publicUserId, @NonNull String publicCategoryId) {
        userService.findUserDetails(publicUserId);
        categoryDaoService.deleteCategory(publicCategoryId);
    }

    public List<CategoryDetails> getCategories() {
        return categoryDaoService.getCategories();
    }

    public CategoryDetails existCategory(String publicCategoryId) {
        return categoryDaoService.findByPublicId(publicCategoryId).orElseThrow(
                () -> new NotFoundException("Category with id - " + publicCategoryId + " not found"));
    }

    public Boolean existCategories(List<String> publicIds) {
        return categoryDaoService.existCategories(publicIds);
    }
}
