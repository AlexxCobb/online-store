package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.ProductDaoService;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.ProductDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductDaoService productDaoService;
    private final CategoryService categoryService;

    public ProductDetails createProduct(@NonNull ProductDto productDto) {
        return productDaoService.createProduct(productDto);
    }

    public ProductDetails updateProduct(@NonNull ProductUpdateDto productUpdateDto,@NonNull String publicProductId) {
        existByPublicId(publicProductId);
        if (productUpdateDto.categoryPublicId().isPresent()) {
            categoryService.existCategoryDetails(productUpdateDto.categoryPublicId().get());
        }
        return productDaoService.updateProduct(productUpdateDto, publicProductId);
    }

    public void deleteProduct(@NonNull String publicProductId) {
        existByPublicId(publicProductId);
        productDaoService.deleteProduct(publicProductId);
    }

    public void existByPublicId(String publicProductId) {
        productDaoService.findByPublicId(publicProductId)
                .orElseThrow(() -> new NotFoundException("Product with publicId = " + publicProductId +
                                                                 " , not found"));
    }

    public List<ProductDetails> getProductsByCategoryId(@NonNull String categoryPublicId) { //замена на лист id категорий
        categoryService.existCategoryDetails(categoryPublicId);
        var products = productDaoService.findProductsByCategoryId(categoryPublicId);
        if (products.isEmpty()) {
            throw new NotFoundException("Products in category with categoryId = " + categoryPublicId + " , not found");
        }
        return products;
    }
}
