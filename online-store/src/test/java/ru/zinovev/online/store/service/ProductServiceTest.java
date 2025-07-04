package ru.zinovev.online.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.ProductDaoService;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CategoryDetails;
import ru.zinovev.online.store.model.ProductDetails;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductDaoService productDaoService;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private ProductService productService;

    private Category mockCategory;
    private Product mockProduct;
    private ProductDto mockProductDto;
    private ProductDetails mockProductDetails;
    private ProductUpdateDto mockUpdateDto;
    private Product mockUpdatedProduct;
    private ProductDetails mockUpdatedProductDetails;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id(1L)
                .name("Phones")
                .publicCategoryId("phonesId")
                .build();
        mockProductDto =
                new ProductDto("Mobile phone", BigDecimal.valueOf(1234.00), "phonesId", null, BigDecimal.valueOf(200),
                               BigDecimal.valueOf(0.0036), 10);
        mockProduct = Product.builder()
                .publicProductId(UUID.randomUUID().toString())
                .name(mockProductDto.name())
                .category(mockCategory)
                .price(mockProductDto.price())
                .weight(mockProductDto.weight())
                .volume(mockProductDto.volume())
                .stockQuantity(mockProductDto.stockQuantity())
                .build();
        mockProductDetails =
                new ProductDetails(mockProduct.getPublicProductId(), mockProduct.getName(), mockProduct.getPrice(),
                                   new CategoryDetails(mockCategory.getPublicCategoryId(), mockCategory.getName()),
                                   new HashMap<>(), mockProduct.getWeight(), mockProduct.getVolume(),
                                   mockProductDto.stockQuantity());
        mockUpdateDto =
                new ProductUpdateDto(Optional.empty(), Optional.of(BigDecimal.valueOf(1000.00)),
                                     Optional.of(mockCategory.getPublicCategoryId()),
                                     Map.of("color", "black"), Optional.empty());
        mockUpdatedProduct = Product.builder()
                .publicProductId(mockProduct.getPublicProductId())
                .name(mockProductDto.name())
                .category(mockCategory)
                .price(mockUpdateDto.price().get())
                .weight(mockProductDto.weight())
                .volume(mockProductDto.volume())
                .parameters(mockUpdateDto.parameters())
                .stockQuantity(mockProductDto.stockQuantity())
                .build();
        mockUpdatedProductDetails =
                new ProductDetails(mockUpdatedProduct.getPublicProductId(), mockUpdatedProduct.getName(),
                                   mockUpdatedProduct.getPrice(),
                                   new CategoryDetails(mockCategory.getPublicCategoryId(),
                                                       mockUpdatedProduct.getName()),
                                   mockUpdatedProduct.getParameters(), mockUpdatedProduct.getWeight(),
                                   mockUpdatedProduct.getVolume(),
                                   mockUpdatedProduct.getStockQuantity());
    }

    @Test
    void shouldCreateNewProduct() {
        when(productDaoService.createProduct(mockProductDto)).thenReturn(mockProductDetails);

        var result = productService.createProduct(mockProductDto);
        assertNotNull(result);
        assertEquals(mockProductDetails, result);
        assertEquals(mockProductDto.name(), result.name());
    }

    @Test
    void shouldUpdateExistedProduct() {
        var publicId = mockProduct.getPublicProductId();
        var categoryId = mockCategory.getPublicCategoryId();

        when(productDaoService.findByPublicId(publicId)).thenReturn(Optional.of(mockProductDetails));
        when(productDaoService.updateProduct(mockUpdateDto, publicId)).thenReturn(
                mockUpdatedProductDetails);

        var result = productService.updateProduct(mockUpdateDto, publicId);
        assertNotNull(result);
        assertEquals(mockUpdatedProductDetails, result);
        assertEquals(mockUpdatedProductDetails.price(), result.price());
        verify(categoryService, times(1)).existCategory(categoryId);
    }

    @Test
    void shouldUpdateExistedProductWhenCategoryIdEmpty() {
        var publicId = mockProduct.getPublicProductId();
        var categoryId = mockCategory.getPublicCategoryId();
        var updateProductDtoWithoutCategory =
                new ProductUpdateDto(Optional.of("name"), Optional.empty(), Optional.empty(), null, Optional.empty());

        when(productDaoService.findByPublicId(publicId)).thenReturn(Optional.of(mockProductDetails));
        when(productDaoService.updateProduct(updateProductDtoWithoutCategory, publicId)).thenReturn(
                mockUpdatedProductDetails);

        var result = productService.updateProduct(updateProductDtoWithoutCategory, publicId);
        assertNotNull(result);
        assertEquals(mockUpdatedProductDetails, result);
        assertEquals(mockUpdatedProductDetails.price(), result.price());
        verify(categoryService, times(0)).existCategory(categoryId);
    }

    @Test
    void shouldDeleteProduct() {
        var publicId = mockProduct.getPublicProductId();

        when(productDaoService.findByPublicId(publicId)).thenReturn(Optional.of(mockProductDetails));

        productService.deleteProduct(publicId);
        verify(productDaoService, times(1)).deleteProduct(publicId);
    }

    @Test
    void shouldFoundProductByPublicId() {
        var publicId = mockProduct.getPublicProductId();

        when(productDaoService.findByPublicId(publicId)).thenReturn(Optional.of(mockProductDetails));

        productService.deleteProduct(publicId);
        verify(productDaoService, times(1)).deleteProduct(publicId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        var publicId = mockProduct.getPublicProductId();

        when(productDaoService.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getByPublicId(publicId));
    }

    @Test
    void shouldReturnListOfProductsByCategoryId() {
        var categoryId = mockCategory.getPublicCategoryId();
        when(categoryService.existCategory(categoryId)).thenReturn(new CategoryDetails(categoryId, "phones"));
        when(productDaoService.findProductsByCategoryId(categoryId)).thenReturn(
                List.of(mockProductDetails, mockUpdatedProductDetails));

        var result = productService.getProductsByCategoryId(categoryId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockProductDetails.name(), result.get(0).name());
    }

    @Test
    void shouldThrowExceptionWhenReturnEmptyList() {
        var categoryId = mockCategory.getPublicCategoryId();
        when(categoryService.existCategory(categoryId)).thenReturn(new CategoryDetails(categoryId, "phones"));
        when(productDaoService.findProductsByCategoryId(categoryId)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> productService.getProductsByCategoryId(categoryId));
    }
}