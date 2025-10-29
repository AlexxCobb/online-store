package ru.zinovev.online.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.dao.ProductDaoService;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;
import ru.zinovev.online.store.model.UserDetails;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
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
    @Mock
    private UserService userService;
    @InjectMocks
    private ProductService productService;

    private UserDetails mockUserDetails;
    private Category mockCategory;
    private Product mockProduct;
    private ProductDetails mockProductDetails;
    private Product mockUpdatedProduct;
    private ProductDetails mockUpdatedProductDetails;
    private ProductDetails mockSavedProductDetails;
    private ProductUpdateDetails mockProductUpdateDetailsRequest;

    @BeforeEach
    void setUp() {
        mockUserDetails = new UserDetails("PublicUserId", "ya@ya.ru", "name",
                                          "lastname");
        mockCategory = Category.builder()
                .id(1L)
                .name("Phones")
                .publicCategoryId("phonesId")
                .build();
        mockProduct = Product.builder()
                .publicProductId(UUID.randomUUID().toString())
                .name("name")
                .category(mockCategory)
                .price(BigDecimal.valueOf(100))
                .weight(BigDecimal.valueOf(200))
                .volume(BigDecimal.valueOf(0.2))
                .stockQuantity(10)
                .createdAt(OffsetDateTime.now())
                .build();
        mockProductDetails =
                new ProductDetails(mockProduct.getPublicProductId(), mockProduct.getName(), mockProduct.getPrice(),
                                   mockCategory.getPublicCategoryId(),
                                   new HashSet<>(), mockProduct.getWeight(), mockProduct.getVolume(),
                                   mockProduct.getStockQuantity(), mockProduct.getImagePath(),
                                   mockProduct.getIsDiscount(), mockProduct.getDiscountPrice(),
                                   mockProduct.getCreatedAt());
        mockSavedProductDetails =
                new ProductDetails(mockProduct.getPublicProductId(), mockProduct.getName(), mockProduct.getPrice(),
                                   mockCategory.getPublicCategoryId(),
                                   new HashSet<>(), mockProduct.getWeight(), mockProduct.getVolume(),
                                   mockProduct.getStockQuantity(), mockProduct.getImagePath(),
                                   mockProduct.getIsDiscount(), mockProduct.getDiscountPrice(),
                                   mockProduct.getCreatedAt());
        mockProductUpdateDetailsRequest =
                new ProductUpdateDetails(null, BigDecimal.valueOf(1000.00), null,
                                         null, null, null);
        mockUpdatedProduct = Product.builder()
                .publicProductId(mockProduct.getPublicProductId())
                .name(mockProductDetails.name())
                .category(mockCategory)
                .price(mockProductDetails.price())
                .weight(mockProductDetails.weight())
                .volume(mockProductDetails.volume())
                .parameters(new HashSet<>())
                .stockQuantity(mockProductDetails.stockQuantity())
                .createdAt(mockProduct.getCreatedAt())
                .build();
        mockUpdatedProductDetails =
                new ProductDetails(mockUpdatedProduct.getPublicProductId(), mockUpdatedProduct.getName(),
                                   mockProductUpdateDetailsRequest.price(),
                                   mockCategory.getPublicCategoryId(),
                                   new HashSet<>(), mockUpdatedProduct.getWeight(),
                                   mockUpdatedProduct.getVolume(),
                                   mockUpdatedProduct.getStockQuantity(), mockUpdatedProduct.getImagePath(),
                                   mockUpdatedProduct.getIsDiscount(), mockUpdatedProduct.getDiscountPrice(),
                                   mockUpdatedProduct.getCreatedAt());
    }

    @Test
    void shouldCreateNewProduct() {
        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);
        when(productDaoService.createProduct(mockProductDetails)).thenReturn(mockSavedProductDetails);

        var result = productService.createProduct(mockUserDetails.publicUserId(), mockProductDetails);
        assertNotNull(result);
        assertEquals(mockSavedProductDetails, result);
        assertEquals(mockSavedProductDetails.name(), result.name());
    }

    @Test
    void shouldUpdateExistedProduct() {
        var publicId = mockProduct.getPublicProductId();
        var categoryId = mockCategory.getPublicCategoryId();

        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);
        when(productDaoService.findByPublicId(publicId)).thenReturn(Optional.of(mockProductDetails));
        when(productDaoService.updateProduct(mockProductUpdateDetailsRequest, publicId)).thenReturn(
                mockUpdatedProductDetails);

        var result =
                productService.updateProduct(mockUserDetails.publicUserId(), mockProductUpdateDetailsRequest, publicId);
        assertNotNull(result);
        assertEquals(mockUpdatedProductDetails, result);
        assertEquals(mockUpdatedProductDetails.price(), result.price());
    }

    @Test
    void shouldUpdateExistedProductWhenCategoryIdEmpty() {
        var publicId = mockProduct.getPublicProductId();
        var categoryId = mockCategory.getPublicCategoryId();
        var updateProductDtoWithoutCategory =
                new ProductUpdateDetails("name", null, null, null, null, null);

        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);
        when(productDaoService.findByPublicId(publicId)).thenReturn(Optional.of(mockProductDetails));
        when(productDaoService.updateProduct(updateProductDtoWithoutCategory, publicId)).thenReturn(
                mockUpdatedProductDetails);

        var result =
                productService.updateProduct(mockUserDetails.publicUserId(), updateProductDtoWithoutCategory, publicId);
        assertNotNull(result);
        assertEquals(mockUpdatedProductDetails, result);
        assertEquals(mockUpdatedProductDetails.price(), result.price());
        verify(categoryService, times(0)).getCategoryByPublicId(categoryId);
    }

    @Test
    void shouldDeleteProduct() {
        var publicId = mockProduct.getPublicProductId();

        when(userService.findUserDetails(mockUserDetails.publicUserId())).thenReturn(mockUserDetails);

        productService.deleteProduct(mockUserDetails.publicUserId(), publicId);
        verify(productDaoService, times(1)).deleteProduct(publicId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        var publicId = mockProduct.getPublicProductId();

        when(productDaoService.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getByPublicId(publicId));
    }
}