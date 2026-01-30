package ru.zinovev.online.store.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.controller.dto.ProductForStandDto;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.entity.ProductParameter;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.repository.CategoryRepository;
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.model.ParametersDetails;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;
import ru.zinovev.online.store.service.ProductEventPublisher;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDaoServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductEventPublisher productEventPublisher;
    @Captor
    private ArgumentCaptor<Product> productCaptor;
    @InjectMocks
    private ProductDaoService productDaoService;

    private Category mockCategory;
    private Product mockProduct;
    private ProductDetails mockProductDetailsRequest;
    private ProductDetails mockProductDetails;
    private ProductUpdateDetails mockProductUpdateDetailsRequest;
    private Product mockUpdatedProduct;
    private ProductDetails mockUpdatedProductDetails;
    private ProductForStandDto mockProductForStandDto;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id(1L)
                .name("Phones")
                .publicCategoryId("phonesId")
                .build();
        mockProductDetailsRequest =
                new ProductDetails("publicProductId", "phonesId", BigDecimal.valueOf(1234.00), null, new HashSet<>(),
                                   BigDecimal.valueOf(200),
                                   BigDecimal.valueOf(0.0036), 10, null, false, null, OffsetDateTime.now());
        mockProduct = Product.builder()
                .publicProductId(UUID.randomUUID().toString())
                .name(mockProductDetailsRequest.name())
                .category(mockCategory)
                .price(mockProductDetailsRequest.price())
                .weight(mockProductDetailsRequest.weight())
                .volume(mockProductDetailsRequest.volume())
                .stockQuantity(mockProductDetailsRequest.stockQuantity())
                .createdAt(OffsetDateTime.now())
                .build();
        mockProductDetails =
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
                .name(mockProductDetailsRequest.name())
                .category(mockCategory)
                .price(mockProductUpdateDetailsRequest.price())
                .weight(mockProductDetailsRequest.weight())
                .volume(mockProductDetailsRequest.volume())
                .parameters(Set.of(new ProductParameter(1L, mockProduct, "color", "black")))
                .stockQuantity(mockProductDetailsRequest.stockQuantity())
                .createdAt(mockProduct.getCreatedAt())
                .build();
        mockUpdatedProductDetails =
                new ProductDetails(mockUpdatedProduct.getPublicProductId(), mockUpdatedProduct.getName(),
                                   mockUpdatedProduct.getPrice(),
                                   mockCategory.getPublicCategoryId(),
                                   Set.of(new ParametersDetails("color", "black")), mockUpdatedProduct.getWeight(),
                                   mockUpdatedProduct.getVolume(),
                                   mockUpdatedProduct.getStockQuantity(), mockUpdatedProduct.getImagePath(),
                                   mockUpdatedProduct.getIsDiscount(), mockUpdatedProduct.getDiscountPrice(),
                                   mockUpdatedProduct.getCreatedAt());
        mockProductForStandDto =
                new ProductForStandDto(mockProduct.getPublicProductId(), mockProduct.getName(), mockProduct.getPrice(),
                                       null, "brand", "color", null, mockProduct.getStockQuantity(), null, null);
    }

    @Test
    void shouldSaveNewProduct() {
        when(categoryRepository.findByPublicCategoryId(eq(mockProductDetailsRequest.categoryPublicId()))).thenReturn(
                Optional.of(mockCategory));
        when(productMapper.toProductDetails(eq(mockProduct))).thenReturn(mockProductDetails);
        when(productRepository.save(productCaptor.capture())).thenReturn(mockProduct);

        var result = productDaoService.createProduct(mockProductDetailsRequest);

        assertNotNull(result);
        assertEquals(mockProductDetails, result);
        assertEquals(mockProduct.getName(), productCaptor.getValue().getName());
    }

    @Test
    void shouldUpdateProductFromProductUpdateDetails() {
        var publicId = mockProduct.getPublicProductId();

        when(productRepository.findByPublicProductId(publicId)).thenReturn(Optional.of(mockProduct));
        when(productMapper.updateProductFromDetails(mockProductUpdateDetailsRequest, mockProduct)).thenReturn(
                mockUpdatedProduct);
        when(productRepository.save(productCaptor.capture())).thenReturn(mockUpdatedProduct);
        when(productMapper.toProductForStandDto(mockUpdatedProduct)).thenReturn(mockProductForStandDto);
        when(productMapper.toProductDetails(mockUpdatedProduct)).thenReturn(mockUpdatedProductDetails);

        var result = productDaoService.updateProduct(mockProductUpdateDetailsRequest, publicId);

        assertNotNull(result);
        assertEquals(mockUpdatedProductDetails, result);
        assertEquals(mockUpdatedProduct.getName(), productCaptor.getValue().getName());
        verify(productMapper, times(1)).updateProductFromDetails(mockProductUpdateDetailsRequest, mockProduct);
    }

    @Test
    void shouldFindByPublicId() {
        when(productRepository.findByPublicProductId(mockProduct.getPublicProductId())).thenReturn(
                Optional.of(mockProduct));
        when(productMapper.toProductDetails(mockProduct)).thenReturn(mockProductDetails);

        var result = productDaoService.findByPublicId(mockProduct.getPublicProductId());

        assertTrue(result.isPresent());
        assertEquals(mockProductDetails, result.get());
    }

    @Test
    void shouldDeleteProductByPublicId() {
        when(productRepository.findByPublicProductId(mockProduct.getPublicProductId())).thenReturn(
                Optional.of(mockProduct));
        when(productMapper.toProductForStandDto(mockProduct)).thenReturn(mockProductForStandDto);

        productDaoService.deleteProduct(mockProduct.getPublicProductId());

        verify(productRepository, times(1)).delete(mockProduct);
    }
}