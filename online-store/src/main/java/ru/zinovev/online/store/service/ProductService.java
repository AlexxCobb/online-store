package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.ProductForStandDto;
import ru.zinovev.online.store.controller.dto.enums.ProductType;
import ru.zinovev.online.store.dao.ProductDaoService;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductParamDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductDaoService productDaoService;
    private final CategoryService categoryService;
    private final UserService userService;

    public ProductDetails createProduct(@NonNull String publicUserId, @NonNull ProductDetails productDetails) {
        userService.findUserDetails(publicUserId);
        return productDaoService.createProduct(productDetails);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "productStand", key = "'stand'"),
                    @CacheEvict(value = "productPrices", allEntries = true)
            }
    )
    public ProductDetails updateProduct(@NonNull String publicUserId,
                                        @NonNull ProductUpdateDetails productUpdateDetails,
                                        @NonNull String publicProductId) {
        userService.findUserDetails(publicUserId);
        getByPublicId(publicProductId);
        return productDaoService.updateProduct(productUpdateDetails, publicProductId);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "productStand", key = "'stand'"),
                    @CacheEvict(value = "productPrices", allEntries = true),
                    @CacheEvict(value = "productParams", allEntries = true)
            }
    )
    public void deleteProduct(@NonNull String publicUserId, @NonNull String publicProductId) {
        userService.findUserDetails(publicUserId);
        productDaoService.deleteProduct(publicProductId);
    }

    public ProductDetails getByPublicId(String publicProductId) {
        return productDaoService.findByPublicId(publicProductId)
                .orElseThrow(() -> new NotFoundException("Product with publicId = " + publicProductId +
                                                                 " , not found"));
    }

    public void reserveProducts(Map<String, Integer> productToQuantity) {
        productDaoService.updateProductsQuantity(productToQuantity);
    }

    public void cancelReserveProducts(Map<String, Integer> productToQuantity) {
        productDaoService.cancelReserveProducts(productToQuantity);
    }

    public Page<ProductDetails> searchProductsWithParameters(
            List<String> categoryPublicIds, BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductParamDetails productParamDetails, Integer page, Integer limit) {
        if (categoryPublicIds != null) {
            var result = categoryService.existCategories(categoryPublicIds);
            if (!result) {
                throw new NotFoundException("Categories with ids - " + categoryPublicIds + "not found"); // доработать
            }
        }
        return productDaoService.findProducts(categoryPublicIds, minPrice, maxPrice, productParamDetails, page, limit);
    }

    @Cacheable(value = "productParams", key = "#key")
    public Set<String> getUniqueParametersByKey(String key) {
        return productDaoService.findUniqueParametersByKey(key);
    }

    @Cacheable(value = "productPrices", key = "'minPrice'")
    public BigDecimal getMinPrice() {
        return productDaoService.getMinPrice();
    }

    @Cacheable(value = "productPrices", key = "'maxPrice'")
    public BigDecimal getMaxPrice() {
        return productDaoService.getMaxPrice();
    }

    @Cacheable(value = "productStand", key = "'stand'")
    public Set<ProductForStandDto> getProductsForStand() {
        var topProducts = productDaoService.getTopProducts();
        var discountProducts = productDaoService.getDiscountProducts();
        var newProducts = productDaoService.getNewProducts();
        return new HashSet<>(Stream.of(topProducts, discountProducts, newProducts)
                                     .flatMap(Collection::stream)
                                     .collect(Collectors.toMap(ProductForStandDto::publicProductId, Function.identity(),
                                                               typeMerger))
                                     .values());
    }

    private Integer getPriority(ProductType type) {
        return switch (type) {
            case DISCOUNT -> 3;
            case TOP -> 2;
            case NEW -> 1;
        };
    }

    private final BinaryOperator<ProductForStandDto> typeMerger = (existingDto, newDto) -> {
        var existingPriority = getPriority(existingDto.type());
        var newPriority = getPriority(newDto.type());
        return (existingPriority > newPriority) ? existingDto : newDto;
    };
}