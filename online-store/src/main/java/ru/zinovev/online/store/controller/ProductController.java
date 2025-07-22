package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductParamDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetails addProduct(@Valid @RequestBody ProductDto productDto) {
        log.debug("Received POST request to add product");
        return productService.createProduct(productMapper.toProductDetails(productDto));
    }

    @PatchMapping("/admin/{publicProductId}")
    public ProductDetails updateProduct(@PathVariable String publicProductId, @Valid @RequestBody
    ProductUpdateDto productUpdateDto) {
        log.debug("Received PATCH request to update product with id = {}", publicProductId);
        return productService.updateProduct(productMapper.toProductUpdateDetails(productUpdateDto), publicProductId);
    }

    @DeleteMapping("/admin/{publicProductId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable String publicProductId) {
        log.debug("Received DELETE request to delete product with id = {}", publicProductId);
        productService.deleteProduct(publicProductId);
    }

    @GetMapping("/search")
    public List<ProductDetails> searchProducts(
            @RequestParam(required = false) List<String> publicCategoryIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @Valid ProductParamDto productParamDto) { // если все параметры null вернуть все товары постранично
        log.debug("Received GET request to search products with parameters");
        return productService.searchProductsWithParameters(publicCategoryIds, minPrice, maxPrice,
                                                           productMapper.toProductParamDetails(productParamDto));
    }
}
