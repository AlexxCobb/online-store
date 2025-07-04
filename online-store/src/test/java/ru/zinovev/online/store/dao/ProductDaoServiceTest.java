package ru.zinovev.online.store.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.repository.CategoryRepository;
import ru.zinovev.online.store.dao.repository.ProductRepository;
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
    @Captor
    private ArgumentCaptor<Product> productCaptor;
    @InjectMocks
    private ProductDaoService productDaoService;

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
                new ProductUpdateDto(Optional.empty(), Optional.of(BigDecimal.valueOf(1000.00)), Optional.empty(),
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
    void shouldSaveNewProduct() {
        when(categoryRepository.findByPublicCategoryId(eq(mockProductDto.categoryPublicId()))).thenReturn(
                Optional.of(mockCategory));
        when(productMapper.toProductDetails(eq(mockProduct))).thenReturn(mockProductDetails);
        when(productRepository.save(productCaptor.capture())).thenReturn(mockProduct);

        var result = productDaoService.createProduct(mockProductDto);

        assertNotNull(result);
        assertEquals(mockProductDetails, result);
        assertEquals(mockProduct.getName(), productCaptor.getValue().getName());
    }

    @Test
    void shouldUpdateProductFromProductUpdateDto() {
        var publicId = mockProduct.getPublicProductId();

        when(productRepository.findByPublicProductId(publicId)).thenReturn(Optional.of(mockProduct));
        when(productMapper.toProductDetails(eq(mockUpdatedProduct))).thenReturn(mockUpdatedProductDetails);
        when(productRepository.save(productCaptor.capture())).thenReturn(mockUpdatedProduct);

        var result = productDaoService.updateProduct(mockUpdateDto, publicId);

        assertNotNull(result);
        assertEquals(mockUpdatedProductDetails, result);
        assertEquals(mockUpdatedProduct.getName(), productCaptor.getValue().getName());
        verify(productMapper, times(1)).updateProductFromProductUpdateDto(mockProduct, mockUpdateDto);
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
        productDaoService.deleteProduct(mockProduct.getPublicProductId());

        verify(productRepository, times(1)).delete(mockProduct);
    }

    @Test
    void shouldFindProductsByCategoryId() {
        var categoryId = mockProduct.getCategory().getPublicCategoryId();
        var expectedProductList = List.of(mockProductDetails, mockUpdatedProductDetails);
        when(productRepository.findByCategoryPublicCategoryId(categoryId)).thenReturn(
                List.of(mockProduct, mockUpdatedProduct));
        when(productMapper.toProductDetails(mockProduct)).thenReturn(mockProductDetails);
        when(productMapper.toProductDetails(mockUpdatedProduct)).thenReturn(mockUpdatedProductDetails);

        var result = productDaoService.findProductsByCategoryId(categoryId);

        assertEquals(2, result.size());
        assertEquals(expectedProductList.get(0), result.get(0));
        assertTrue(result.containsAll(List.of(mockProductDetails, mockUpdatedProductDetails)));
    }

    @Test
    void shouldReturnEmptyListWhenProductsNotFoundByCategoryId() {
        var categoryId = mockProduct.getCategory().getPublicCategoryId();

        when(productRepository.findByCategoryPublicCategoryId(categoryId)).thenReturn(Collections.emptyList());

        var result = productDaoService.findProductsByCategoryId(categoryId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}