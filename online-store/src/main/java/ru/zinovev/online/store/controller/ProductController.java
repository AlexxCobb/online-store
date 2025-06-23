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
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.service.ProductService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/admin/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetails addProduct(@Valid @RequestBody ProductDto productDto) {
        log.debug("Received POST request to add product");
        return productService.createProduct(productDto);
    }

    @PatchMapping("/{productId}")
    public ProductDetails updateProduct(@PathVariable String publicProductId, @Valid @RequestBody
    ProductUpdateDto productUpdateDto) {
        log.debug("Received PATCH request to update product with id = {}", publicProductId);
        return productService.updateProduct(productUpdateDto, publicProductId);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable String publicProductId) {
        log.debug("Received DELETE request to delete product with id = {}", publicProductId);
        productService.deleteProduct(publicProductId);
    }

    @GetMapping("/{categoryId}")
    public List<ProductDetails> getAllProductsByCategoryId(
            @PathVariable String publicCategoryId) {
        log.debug("Received GET request to get all products in categoryId = {}", publicCategoryId);
        return productService.getProductsByCategoryId(publicCategoryId);
    }
}
