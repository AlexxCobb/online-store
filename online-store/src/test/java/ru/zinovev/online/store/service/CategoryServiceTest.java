package ru.zinovev.online.store.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.dao.CategoryDaoService;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CategoryDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryDaoService categoryDaoService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldCreateNewCategory() {
        var categoryDto = new CategoryDto("Phone");
        var expectedCategoryDetails = new CategoryDetails("publicId", "Phone");

        when(categoryDaoService.findByNameIgnoreCase(categoryDto)).thenReturn(Optional.empty());
        when(categoryDaoService.createCategory(categoryDto)).thenReturn(expectedCategoryDetails);

        var result = categoryService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals(expectedCategoryDetails, result);
    }

    @Test
    void shouldThrowExceptionWhenCategoryAlreadyExist() {
        var categoryDto = new CategoryDto("Phone");
        var expectedCategoryDetails = new CategoryDetails("publicId", "Phone");

        when(categoryDaoService.findByNameIgnoreCase(categoryDto)).thenReturn(Optional.of(expectedCategoryDetails));

        assertThrows(BadRequestException.class,
                     () -> categoryService.createCategory(categoryDto));
        verify(categoryDaoService, never()).createCategory(categoryDto);
    }

    @Test
    void shouldUpdateCategory() {
        var categoryDto = new CategoryDto("Mobile phone");
        var publicId = "publicId";
        var currentCategoryDetails = new CategoryDetails("publicId", "Phone");
        var expectedCategoryDetails = new CategoryDetails("publicId", "Mobile phone");

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.of(currentCategoryDetails));
        when(categoryDaoService.updateCategory(currentCategoryDetails, categoryDto)).thenReturn(
                expectedCategoryDetails);

        var result = categoryService.updateCategory(publicId, categoryDto);

        assertNotNull(result);
        assertEquals(expectedCategoryDetails, result);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNamesEquals() {
        var categoryDto = new CategoryDto("Mobile phone");
        var publicId = "publicId";
        var currentCategoryDetails = new CategoryDetails("publicId", "MOBILE PHONE");

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.of(currentCategoryDetails));

        assertThrows(BadRequestException.class,
                     () -> categoryService.updateCategory(publicId, categoryDto));
        verify(categoryDaoService, never()).updateCategory(currentCategoryDetails, categoryDto);
    }

    @Test
    void shouldDeleteCategory() {
        var publicId = "publicId";
        var currentCategoryDetails = new CategoryDetails("publicId", "MOBILE PHONE");

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.of(currentCategoryDetails));

        categoryService.deleteCategory(publicId);
        verify(categoryDaoService, times(1)).deleteCategory(currentCategoryDetails);
    }

    @Test
    void shouldReturnCategoryDetailsWhenExist() {
        var publicId = "publicId";
        var expectedCategoryDetails = new CategoryDetails("publicId", "MOBILE PHONE");

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.of(expectedCategoryDetails));

        var result = categoryService.existCategory(publicId);

        assertNotNull(result);
        assertEquals(expectedCategoryDetails, result);
    }

    @Test
    void shouldThrowExceptionWhenCategoryDetailsNotFound() {
        var publicId = "publicId";

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                     () -> categoryService.existCategory(publicId));
    }
}