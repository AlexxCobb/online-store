package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.ProductForStandDto;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.entity.OrderItem;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.repository.CategoryRepository;
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.dao.repository.ProductSpecifications;
import ru.zinovev.online.store.dao.repository.ProductStatisticRepository;
import ru.zinovev.online.store.exception.model.AlreadyExistException;
import ru.zinovev.online.store.exception.model.DuplicateProductException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.exception.model.OutOfStockException;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductParamDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;
import ru.zinovev.online.store.model.TopProductDetails;
import ru.zinovev.online.store.service.ProductEventPublisher;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDaoService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductEventPublisher productEventPublisher;
    private final ProductStatisticRepository statisticRepository;

    @Transactional
    public ProductDetails createProduct(ProductDetails productDetails) {
        var category = categoryRepository.findByPublicCategoryId(productDetails.categoryPublicId())
                .orElseThrow(() -> new NotFoundException(
                        "Category with id - " + productDetails.categoryPublicId() + " not found"));

        var product = Product.builder()
                .publicProductId(UUID.randomUUID().toString())
                .name(productDetails.name())
                .category(category)
                .imagePath(productDetails.imagePath())
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

        product.calculateProductFingerprint();
        var existedProduct = productRepository.findByFingerprint(product.getFingerprint());
        if (existedProduct.isPresent()) {
            var price = existedProduct.get().getPrice();
            var quantity = existedProduct.get().getStockQuantity();

            if (!price.equals(productDetails.price()) || !quantity.equals(productDetails.stockQuantity())) {
                throw new DuplicateProductException("Product already exist", existedProduct.get().getPublicProductId(),
                                                    existedProduct.get().getName(),
                                                    productDetails.stockQuantity(),
                                                    productDetails.price());
            } else {
                throw new AlreadyExistException("Product already exist with the same parameters");
            }
        }
        return productMapper.toProductDetails(productRepository.save(product));
    }

    @Transactional
    public ProductDetails updateProduct(ProductUpdateDetails updateDetails, String publicProductId) {
        var existedProduct =
                productRepository.findByPublicProductId(publicProductId)
                        .orElseThrow(() -> new NotFoundException("Product with publicId = " + publicProductId
                                                                         + " , not found"));
        var category = existedProduct.getCategory();
        if (updateDetails.publicCategoryId() != null) {
            category = categoryRepository.findByPublicCategoryId(updateDetails.publicCategoryId())
                    .orElseThrow(() -> new NotFoundException(
                            "Category with id - " + updateDetails.publicCategoryId() + " not found"));
        }

        var updatedProductFromDetails = productMapper.updateProductFromDetails(updateDetails, existedProduct);
        var updatedProductWithCategory = updatedProductFromDetails.toBuilder().category(category).build();
        var product = productRepository.save(updatedProductWithCategory);
        productEventPublisher.publishProductUpdateEvent(productMapper.toProductForStandDto(product));
        return productMapper.toProductDetails(product);
    }

    public List<TopProductDetails> getOneProductFromEachCategory() {
        var categories = categoryRepository.findAll().stream().map(Category::getId).toList();
        return productRepository.getOneProductFromEachCategory(categories)
                .stream()
                .map(productMapper::toTopProductDetails)
                .collect(Collectors.toList());
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
        productEventPublisher.publishProductDeleteEvent(productMapper.toProductForStandDto(product));
    }

    public Page<ProductDetails> findProducts(List<String> categoryPublicIds, BigDecimal minPrice,
                                             BigDecimal maxPrice,
                                             ProductParamDetails productParamDetails, Integer page, Integer limit) {
        if (Objects.isNull(categoryPublicIds) && Objects.isNull(minPrice) && Objects.isNull(maxPrice) && Objects.isNull(
                productParamDetails.brand()) && Objects.isNull(productParamDetails.memory()) && Objects.isNull(
                productParamDetails.ram()) && Objects.isNull(productParamDetails.color())) {
            var sort = Sort.by(Sort.Direction.DESC, "stockQuantity");
            var pageable = PageRequest.of(page, limit, sort);
            var ids = productRepository.findProductIds(pageable);
            var products = productRepository.findProductsWithParametersInIds(ids.getContent());
            var productDetails = products
                    .stream()
                    .map(productMapper::toProductDetails)
                    .toList();
            return new PageImpl<>(productDetails, pageable, ids.getTotalElements());
        }

        List<Specification<Product>> spec = new ArrayList<>();
        if (Objects.nonNull(ProductSpecifications.hasPriceBetween(minPrice, maxPrice))) {
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
        var sort = Sort.by(Sort.Direction.DESC, "stockQuantity");
        var pageable = PageRequest.of(page, limit, sort);
        var products = productRepository.findAll(allConditions, pageable);
        var productDetails = products.stream().map(productMapper::toProductDetails).toList();
        return new PageImpl<>(productDetails, pageable, products.getTotalElements());
    }

    public Set<String> findUniqueParametersByKey(String key) {
        return productRepository.findUniqueParametersByKey(key);
    }

    public BigDecimal getMinPrice() {
        return productRepository.getMinPrice();
    }

    public BigDecimal getMaxPrice() {
        return productRepository.getMaxPrice();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Product> updateProductsQuantity(Map<Product, Integer> products) {
        var productsList = new ArrayList<Product>();
        products.forEach((product, integer) -> {
            if (product.getStockQuantity() < integer) {
                throw new OutOfStockException(
                        "You cannot order the selected quantity - @d of product name - %s , the remainder in the warehouse is - %s",
                        product.getName(), integer,
                        product.getStockQuantity());
            }
            var productToUpdate = product.toBuilder()
                    .stockQuantity(product.getStockQuantity() - integer)
                    .build();
            productsList.add(productToUpdate);
        });
        return productRepository.saveAll(productsList);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void returnProductsToWarehouse(List<OrderItem> items) {
        var productsList = items.stream().map(item -> {
            var product = item.getProduct();
            return product.toBuilder()
                    .stockQuantity(product.getStockQuantity() + item.getQuantity())
                    .build();
        }).toList();
        productRepository.saveAll(productsList);
    }

    public List<ProductForStandDto> getTopProducts() {
        var topSix = PageRequest.of(0, 6);
        return statisticRepository.findTopProductViews(topSix)
                .getContent()
                .stream()
                .map(productMapper::toTopProductDto)
                .toList(); // использование статистики здесь?
    }

    public List<ProductForStandDto> getDiscountProducts() {
        var discountSix = PageRequest.of(0, 6);
        return productRepository.findDiscountProducts(discountSix)
                .stream()
                .map(productMapper::toDiscountProductDto)
                .toList();
    }

    public List<ProductForStandDto> getNewProducts() {
        var newSix = PageRequest.of(0, 6);
        var lastMonth = OffsetDateTime.now().minusMonths(1);
        return productRepository.findNewProducts(lastMonth, newSix)
                .stream()
                .map(productMapper::toNewProductDto)
                .toList();
    }
}