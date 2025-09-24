package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.ProductDaoService;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductParamDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;
import ru.zinovev.online.store.model.TopProductDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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

    public ProductDetails updateProduct(@NonNull String publicUserId,
                                        @NonNull ProductUpdateDetails productUpdateDetails,
                                        @NonNull String publicProductId) {
        userService.findUserDetails(publicUserId);
        getByPublicId(publicProductId);
        return productDaoService.updateProduct(productUpdateDetails, publicProductId);
    }

    public void deleteProduct(@NonNull String publicUserId, @NonNull String publicProductId) {
        userService.findUserDetails(publicUserId);
        productDaoService.deleteProduct(publicProductId);
    }

    public ProductDetails getByPublicId(String publicProductId) {
        return productDaoService.findByPublicId(publicProductId)
                .orElseThrow(() -> new NotFoundException("Product with publicId = " + publicProductId +
                                                                 " , not found"));
    }

    public List<TopProductDetails> getOneProductFromEachCategory() {
        return productDaoService.getOneProductFromEachCategory();
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

    public Set<String> getUniqueParametersByKey(String key) {
        return productDaoService.findUniqueParametersByKey(key);
    }

    public BigDecimal getMinPrice() {
        return productDaoService.getMinPrice();
    }

    public BigDecimal getMaxPrice() {
        return productDaoService.getMaxPrice();
    }
}