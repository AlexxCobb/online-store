package ru.zinovev.online.store.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.dao.CategoryDaoService;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CategoryDetails;
import ru.zinovev.online.store.model.UserDetails;

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
    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldCreateNewCategory() {
        var userDetails = new UserDetails("userId", "ya@ta.ru", "name", "lastname");
        var categoryDetails = new CategoryDetails(null, "Phone");
        var expectedCategoryDetails = new CategoryDetails("publicId", "Phone");

        when(userService.findUserDetails(userDetails.publicUserId())).thenReturn(userDetails);
        when(categoryDaoService.findByNameIgnoreCase(categoryDetails)).thenReturn(Optional.empty());
        when(categoryDaoService.createCategory(categoryDetails)).thenReturn(expectedCategoryDetails);

        var result = categoryService.createCategory(userDetails.publicUserId(), categoryDetails);

        assertNotNull(result);
        assertEquals(expectedCategoryDetails, result);
    }

    @Test
    void shouldThrowExceptionWhenCategoryAlreadyExist() {
        var userDetails = new UserDetails("userId", "ya@ta.ru", "name", "lastname");
        var categoryDetails = new CategoryDetails(null, "Phone");
        var expectedCategoryDetails = new CategoryDetails("publicId", "Phone");

        when(userService.findUserDetails(userDetails.publicUserId())).thenReturn(userDetails);
        when(categoryDaoService.findByNameIgnoreCase(categoryDetails)).thenReturn(Optional.of(expectedCategoryDetails));

        assertThrows(BadRequestException.class,
                     () -> categoryService.createCategory(userDetails.publicUserId(), categoryDetails));
        verify(categoryDaoService, never()).createCategory(categoryDetails);
    }

    @Test
    void shouldUpdateCategory() {
        var categoryDetails = new CategoryDetails(null, "Mobile phone");
        var publicId = "publicId";
        var currentCategoryDetails = new CategoryDetails("publicId", "Phone");
        var expectedCategoryDetails = new CategoryDetails("publicId", "Mobile phone");

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.of(currentCategoryDetails));
        when(categoryDaoService.updateCategory(currentCategoryDetails, categoryDetails)).thenReturn(
                expectedCategoryDetails);

        var result =
                categoryService.updateCategory(publicId, currentCategoryDetails.publicCategoryId(), categoryDetails);

        assertNotNull(result);
        assertEquals(expectedCategoryDetails, result);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNamesEquals() {
        var categoryDetails = new CategoryDetails(null, "Mobile phone");
        var publicId = "publicId";
        var currentCategoryDetails = new CategoryDetails("publicId", "MOBILE PHONE");

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.of(currentCategoryDetails));

        assertThrows(BadRequestException.class,
                     () -> categoryService.updateCategory(publicId, currentCategoryDetails.publicCategoryId(),
                                                          categoryDetails));
        verify(categoryDaoService, never()).updateCategory(currentCategoryDetails, categoryDetails);
    }

    @Test
    void shouldDeleteCategory() {
        var userDetails = new UserDetails("userId", "ya@ta.ru", "name", "lastname");
        var currentCategoryDetails = new CategoryDetails("publicId", "MOBILE PHONE");

        when(userService.findUserDetails(userDetails.publicUserId())).thenReturn(userDetails);

        categoryService.deleteCategory(userDetails.publicUserId(), currentCategoryDetails.publicCategoryId());
        verify(categoryDaoService, times(1)).deleteCategory(currentCategoryDetails.publicCategoryId());
    }

    @Test
    void shouldReturnCategoryDetailsWhenExist() {
        var publicId = "publicId";
        var expectedCategoryDetails = new CategoryDetails("publicId", "MOBILE PHONE");

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.of(expectedCategoryDetails));

        var result = categoryService.getCategoryByPublicId(publicId);

        assertNotNull(result);
        assertEquals(expectedCategoryDetails, result);
    }

    @Test
    void shouldThrowExceptionWhenCategoryDetailsNotFound() {
        var publicId = "publicId";

        when(categoryDaoService.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                     () -> categoryService.getCategoryByPublicId(publicId));
    }
}