package ru.zinovev.online.store.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.mapper.CategoryMapper;
import ru.zinovev.online.store.dao.repository.CategoryRepository;
import ru.zinovev.online.store.model.CategoryDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryDaoServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @InjectMocks
    private CategoryDaoService categoryDaoService;

    @Test
    void shouldSaveNewCategory() {
        var categoryDto = new CategoryDto("Phone");
        var newCategory = Category.builder()
                .id(1L)
                .publicCategoryId("publicId")
                .name("Phone")
                .build();
        var expectedCategoryDetails = new CategoryDetails("publicId", "Phone");

        when(categoryMapper.toCategoryDetails(newCategory)).thenReturn(expectedCategoryDetails);
        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(newCategory);

        var result = categoryDaoService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals(expectedCategoryDetails, result);
        assertEquals(newCategory.getName(), categoryCaptor.getValue().getName());
    }

    @Test
    void shouldUpdateCategory() {
        var categoryDto = new CategoryDto("Laptop");
        var currentCategory = Category.builder()
                .id(1L)
                .publicCategoryId("publicId")
                .name("Phone")
                .build();
        var updateCategory = currentCategory.toBuilder()
                .name(categoryDto.name())
                .build();
        var currentCategoryDetails = new CategoryDetails("publicId", "Phone");
        var expectedCategoryDetails = new CategoryDetails("publicId", "Laptop");

        when(categoryRepository.findByPublicCategoryId(currentCategoryDetails.publicCategoryId())).thenReturn(
                Optional.of(currentCategory));
        when(categoryMapper.toCategoryDetails(updateCategory)).thenReturn(expectedCategoryDetails);
        when(categoryRepository.save(categoryCaptor.capture())).thenReturn(updateCategory);

        var result = categoryDaoService.updateCategory(currentCategoryDetails, categoryDto);

        assertNotNull(result);
        assertEquals(expectedCategoryDetails, result);
        assertEquals(updateCategory.getName(), result.name());
    }

    @Test
    void shouldDeleteCategory() {
        var currentCategoryDetails = new CategoryDetails("publicId", "Phone");
        var expectedCategory = Category.builder()
                .id(1L)
                .publicCategoryId("publicId")
                .name("Phone")
                .build();
        when(categoryMapper.toCategory(currentCategoryDetails)).thenReturn(expectedCategory);

        categoryRepository.delete(categoryMapper.toCategory(currentCategoryDetails));
        verify(categoryRepository, times(1)).delete(eq(expectedCategory));
    }

    @Test
    void findByNameIgnoreCase() {
        var categoryDto = new CategoryDto("Phone");
        var currentCategory = Category.builder()
                .id(1L)
                .publicCategoryId("publicId")
                .name("Phone")
                .build();
        var currentCategoryDetails = new CategoryDetails("publicId", "Phone");
        var expectedCategoryDetails = new CategoryDetails("publicId", "Phone");

        when(categoryRepository.findByNameIgnoreCase(categoryDto.name())).thenReturn(
                Optional.of(currentCategory));
        when(categoryMapper.toCategoryDetails(currentCategory)).thenReturn(currentCategoryDetails);

        var result = categoryDaoService.findByNameIgnoreCase(categoryDto);

        assertTrue(result.isPresent());
        assertEquals(expectedCategoryDetails, result.get());
    }

    @Test
    void findByPublicId() {
        var publicCategoryId = "publicId";
        var expectedCategory = Category.builder()
                .id(1L)
                .publicCategoryId(publicCategoryId)
                .name("Phone")
                .build();
        var expectedCategoryDetails = new CategoryDetails(publicCategoryId, "Phone");

        when(categoryRepository.findByPublicCategoryId(eq(publicCategoryId))).thenReturn(
                Optional.of(expectedCategory));
        when(categoryMapper.toCategoryDetails(expectedCategory)).thenReturn(expectedCategoryDetails);

        var result = categoryDaoService.findByPublicId(publicCategoryId);

        assertTrue(result.isPresent());
        assertEquals(expectedCategoryDetails, result.get());
    }
}