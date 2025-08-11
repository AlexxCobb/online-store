package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.repository.CategoryRepository;
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.dao.repository.ProductSpecifications;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductParamDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDaoService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDetails createProduct(ProductDetails productDetails) {
        var category = categoryRepository.findByPublicCategoryId(productDetails.categoryPublicId())
                .orElseThrow(() -> new NotFoundException(
                        "Category with id - " + productDetails.categoryPublicId() + " not found"));

        var product = Product.builder()
                .publicProductId(UUID.randomUUID().toString())
                .name(productDetails.name())
                .category(category)
                .price(productDetails.price())
                .weight(productDetails.weight())
                .volume(productDetails.volume())
                .parameters(new HashSet<>())
                .stockQuantity(productDetails.stockQuantity())
                .build();

        if (productDetails.parameters() != null) {
            productDetails.parameters()
                    .stream()
                    .map(productMapper::toProductParameter)
                    .map(productParameter -> productParameter.toBuilder().product(product).build())
                    .forEach(product.getParameters()::add);
        }
        return productMapper.toProductDetails(productRepository.save(product));
    }

    @Transactional
    public ProductDetails updateProduct(ProductUpdateDetails updateDetails, String publicProductId) {
        var existedProduct =
                productRepository.findByPublicProductId(publicProductId)
                        .orElseThrow(() -> new NotFoundException("Product with publicId = " + publicProductId
                                                                         + " , not found"));
        productMapper.updateProductFromProductUpdateDetails(existedProduct, updateDetails);
        return productMapper.toProductDetails(productRepository.save(existedProduct));
    }

    public Optional<ProductDetails> findByPublicId(String publicProductId) {
        return productRepository.findByPublicProductId(publicProductId).map(productMapper::toProductDetails);
    }

    @Transactional
    public void deleteProduct(String publicProductId) {
        var product = productRepository.findByPublicProductId(publicProductId)
                .orElseThrow(() -> new NotFoundException("Product with publicId = " + publicProductId +
                                                                 " , not found"));
        productRepository.delete(product);
    }

    public List<ProductDetails> findProducts(List<String> categoryPublicIds, BigDecimal minPrice,
                                             BigDecimal maxPrice,
                                             ProductParamDetails productParamDetails) {
        if (Objects.isNull(categoryPublicIds) && Objects.isNull(minPrice) && Objects.isNull(maxPrice) && Objects.isNull(
                productParamDetails.brand()) && Objects.isNull(productParamDetails.memory()) && Objects.isNull(
                productParamDetails.ram()) && Objects.isNull(productParamDetails.color())) {
            return productRepository.findAllWithParameters()
                    .stream()
                    .map(productMapper::toProductDetails)
                    .collect(Collectors.toList());
        }

        List<Specification<Product>> spec = new ArrayList<>();
        if(Objects.nonNull(ProductSpecifications.hasPriceBetween(minPrice, maxPrice))){
            spec.add(ProductSpecifications.hasPriceBetween(minPrice, maxPrice));
        }

        if (Objects.nonNull(categoryPublicIds)) {
            spec.add(ProductSpecifications.hasCategories(categoryPublicIds));
        }

        if (Objects.nonNull(productParamDetails.brand())) {
            spec.add(ProductSpecifications.hasBrand(productParamDetails.brand()));
        }

        if (Objects.nonNull(productParamDetails.color())) {
            spec.add(ProductSpecifications.hasColor(productParamDetails.color()));
        }

        if (Objects.nonNull(productParamDetails.ram())) {
            spec.add(ProductSpecifications.hasRam(productParamDetails.ram()));
        }

        if (Objects.nonNull(productParamDetails.memory())) {
            spec.add(ProductSpecifications.hasMemory(productParamDetails.memory()));
        }

        Specification<Product> allConditions = spec.stream()
                .reduce(Specification::and)
                .get();

        return productRepository.findAll(allConditions).stream().map(productMapper::toProductDetails).toList();
    }

    public boolean existProducts(List<String> productIds) {
        return productRepository.existsByPublicProductIdIn(productIds);
    }

    @Transactional
    public void reserveProduct(String publicProductId) {
        var existedProduct = productRepository.findByPublicProductId(publicProductId).get();
        var updatedProduct = existedProduct.toBuilder().stockQuantity(existedProduct.getStockQuantity() - 1).build();
        productRepository.save(updatedProduct);
    }
}
