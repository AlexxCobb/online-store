package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.ProductDaoService;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductParamDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;

import java.math.BigDecimal;
import java.util.List;

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

    public ProductDetails updateProduct(@NonNull String publicUserId, @NonNull ProductUpdateDetails productUpdateDetails,
                                        @NonNull String publicProductId) {
        userService.findUserDetails(publicUserId);
        getByPublicId(publicProductId);
        if (productUpdateDetails.categoryPublicId() != null) {
            categoryService.existCategory(productUpdateDetails.categoryPublicId());
        }
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

    public List<ProductDetails> searchProductsWithParameters(
            List<String> categoryPublicIds, BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductParamDetails productParamDetails) {
        if (!categoryPublicIds.isEmpty()) {
            var result = categoryService.existCategories(categoryPublicIds);
            if (!result) {
                throw new NotFoundException("Categories with ids - " + categoryPublicIds + "not found");
            }
        }
        return productDaoService.findProducts(categoryPublicIds, minPrice, maxPrice, productParamDetails);
    }

    public void reserveProduct(String publicProductId) { // добавить количество товаров
        productDaoService.reserveProduct(publicProductId);
    }
}
