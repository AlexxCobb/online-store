package ru.zinovev.online.store.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.zinovev.online.store.controller.dto.ProductEventDto;
import ru.zinovev.online.store.controller.dto.ProductForStandDto;
import ru.zinovev.online.store.controller.dto.enums.EventType;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.entity.ProductParameter;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.repository.CategoryRepository;
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.exception.model.AlreadyExistException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.ParametersDetails;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
    private ApplicationEventPublisher applicationEventPublisher;
    @Captor
    private ArgumentCaptor<Product> productCaptor;
    @Captor
    private ArgumentCaptor<ProductEventDto> eventCaptor;
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
                .isDiscount(false)
                .discountPrice(null)
                .fingerprint("abc123")
                .parameters(new HashSet<>())
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
        verify(productRepository).save(productCaptor.capture());
        var savedProduct = productCaptor.getValue();
        assertEquals(mockProductDetails.name(), savedProduct.getName());
        assertEquals(mockCategory, savedProduct.getCategory());
        assertNotNull(savedProduct.getFingerprint());
    }

    @Test
    void shouldThrowNotFoundWhenCategoryMissingOnCreate() {
        when(categoryRepository.findByPublicCategoryId(mockCategory.getPublicCategoryId())).thenReturn(
                Optional.empty());

        assertThrows(NotFoundException.class,
                     () -> productDaoService.createProduct(mockProductDetails));
    }

    @Test
    void shouldThrowAlreadyExistExceptionWhenFingerprintExistsAndSamePriceQuantity() {
        Product existing = mockProduct.toBuilder()
                .price(mockProductDetailsRequest.price())
                .stockQuantity(mockProductDetailsRequest.stockQuantity())
                .build();
        when(categoryRepository.findByPublicCategoryId(mockProductDetails.categoryPublicId()))
                .thenReturn(Optional.of(mockCategory));
        when(productRepository.findByFingerprint(anyString()))
                .thenReturn(Optional.of(existing));
        assertThrows(AlreadyExistException.class,
                     () -> productDaoService.createProduct(mockProductDetails));
    }

    @Test
    void shouldUpdateProductWithoutCategoryChange() {
        var updateDetails = new ProductUpdateDetails(
                "New Name", BigDecimal.valueOf(1500), null, 20, true, BigDecimal.valueOf(1200)
        );
        Product existingProduct = mockProduct;
        Product updatedProduct = existingProduct.toBuilder()
                .name(updateDetails.name())
                .price(updateDetails.price())
                .stockQuantity(updateDetails.stockQuantity())
                .isDiscount(updateDetails.isDiscount())
                .discountPrice(updateDetails.discountPrice())
                .build();
        ProductForStandDto productForStandDto = mockProductForStandDto;
        ProductDetails expectedDetails = mockUpdatedProductDetails;

        String productId = existingProduct.getPublicProductId();

        when(productRepository.findByPublicProductId(productId))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.findByPublicProductId(productId))
                .thenReturn(Optional.of(updatedProduct));
        when(productMapper.toProductForStandDto(updatedProduct)).thenReturn(productForStandDto);
        when(productMapper.toProductDetails(updatedProduct)).thenReturn(expectedDetails);

        ProductDetails result = productDaoService.updateProduct(updateDetails, productId);

        assertNotNull(result);
        assertEquals(expectedDetails, result);

        verify(productRepository, times(2)).findByPublicProductId(productId);
        verify(productRepository).updateProductFields(
                updateDetails.name(),
                updateDetails.price(),
                updateDetails.stockQuantity(),
                updateDetails.isDiscount(),
                updateDetails.discountPrice(),
                productId
        );
        verify(categoryRepository, never()).findByPublicCategoryId(any());
        verify(productRepository, never()).updateCategory(any(), any());

        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        ProductEventDto publishedEvent = eventCaptor.getValue();
        assertEquals("UPDATE", publishedEvent.eventType().name());
        assertEquals(productId, publishedEvent.publicProductId());
        assertEquals(productForStandDto, publishedEvent.product());
        assertNotNull(publishedEvent.eventId());
        assertNotNull(publishedEvent.createdAt());
    }

    @Test
    void shouldThrowNotFoundWhenProductMissingOnUpdate() {
        when(productRepository.findByPublicProductId(mockProduct.getPublicProductId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                     () -> productDaoService.updateProduct(mockProductUpdateDetailsRequest,
                                                           mockProduct.getPublicProductId()));
    }

    @Test
    void shouldThrowNotFoundWhenNewCategoryMissing() {
        ProductUpdateDetails updateWithNewCat = new ProductUpdateDetails(
                null, null, "invalidCat", null, false, null
        );
        when(productRepository.findByPublicProductId(mockProduct.getPublicProductId()))
                .thenReturn(Optional.of(mockProduct));
        when(categoryRepository.findByPublicCategoryId("invalidCat"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                     () -> productDaoService.updateProduct(updateWithNewCat, mockProduct.getPublicProductId()));
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
    void shouldReturnEmptyWhenProductNotFound() {
        when(productRepository.findByPublicProductId(mockProduct.getPublicProductId()))
                .thenReturn(Optional.empty());

        Optional<ProductDetails> result = productDaoService.findByPublicId(mockProduct.getPublicProductId());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteProductByPublicId() {
        when(productRepository.findByPublicProductId(mockProduct.getPublicProductId())).thenReturn(
                Optional.of(mockProduct));
        when(productMapper.toProductForStandDto(mockProduct)).thenReturn(mockProductForStandDto);

        productDaoService.deleteProduct(mockProduct.getPublicProductId());

        verify(productRepository).delete(mockProduct);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        ProductEventDto event = eventCaptor.getValue();
        assertEquals(EventType.DELETE, event.eventType());
    }
}