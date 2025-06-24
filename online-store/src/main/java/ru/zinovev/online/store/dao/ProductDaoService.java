package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.mapper.ProductMapper;
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.model.ProductDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDaoService {

    private final CategoryDaoService categoryDaoService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDetails createProduct(ProductDto productDto) {
        var category = categoryDaoService.getByPublicId(productDto.categoryPublicId());

        var product = Product.builder()
                .publicProductId(UUID.randomUUID().toString())
                .name(productDto.name())
                .category(category)
                .price(productDto.price())
                .weight(productDto.weight())
                .volume(productDto.volume())
                .stockQuantity(productDto.stockQuantity())
                .build();

        if (productDto.parameters() != null) {
            product.getParameters().putAll(productDto.parameters());
        }
        return productMapper.toProductDetails(productRepository.save(product));
    }

    @Transactional
    public ProductDetails updateProduct(ProductUpdateDto updateDto, String publicProductId) {
        var existedProduct = productRepository.findByPublicProductId(publicProductId).get();
        productMapper.updateProductFromProductUpdateDto(existedProduct, updateDto);
        return productMapper.toProductDetails(productRepository.save(existedProduct));
    }

    public Optional<ProductDetails> findByPublicId(String publicProductId) {
        return productRepository.findByPublicProductId(publicProductId).map(productMapper::toProductDetails);
    }

    @Transactional
    public void deleteProduct(String publicProductId) {
        var product = productRepository.findByPublicProductId(publicProductId).get();
        productRepository.delete(product);
    }

    public List<ProductDetails> findProductsByCategoryId(String categoryPublicId) {
        return productRepository.findByCategoryPublicCategoryId(categoryPublicId)
                .stream()
                .map(productMapper::toProductDetails)
                .toList();
    }
}
