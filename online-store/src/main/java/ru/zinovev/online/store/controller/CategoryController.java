package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.model.CategoryDetails;
import ru.zinovev.online.store.service.CategoryService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDetails addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Received POST request to add category");
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDetails updateCategory(@PathVariable String publicCategoryId, @Valid @RequestBody
    CategoryDto categoryDto) {
        log.info("Received PATCH request to update category with id = {}", publicCategoryId);
        return categoryService.updateCategory(publicCategoryId, categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable String publicCategoryId) {
        log.info("Received DELETE request to delete category with id = {}", publicCategoryId);
        categoryService.deleteCategory(publicCategoryId);
    }
}
