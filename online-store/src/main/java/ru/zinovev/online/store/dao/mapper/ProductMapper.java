package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zinovev.online.store.controller.dto.ParametersDto;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductParamDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.entity.ProductParameter;
import ru.zinovev.online.store.dao.entity.ProductView;
import ru.zinovev.online.store.model.ParametersDetails;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductParamDetails;
import ru.zinovev.online.store.model.ProductShortDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;
import ru.zinovev.online.store.model.TopProductDetails;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryPublicId", source = "category.publicCategoryId")
    ProductDetails toProductDetails(Product product);

    ParametersDetails toParametersDetails(ProductParameter productParameter);

    @Mapping(target = "publicProductId", ignore = true)
    ProductDetails toProductDetails(ProductDto productDto);

    ParametersDetails toParametersDetails(ParametersDto parametersDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductParameter toProductParameter(ParametersDetails parametersDetails);

    Set<ProductParameter> toProductParameters(Set<ParametersDetails> parametersDetails);

    ProductShortDetails toProductShortDetails(Product product);

    TopProductDetails toTopProductDetails(ProductView productView);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ProductParamDetails toProductParamDetails(ProductParamDto productParamDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "name", expression = "java(checkValue(productUpdateDto.name()))")
    @Mapping(target = "publicCategoryId", expression = "java(checkValue(productUpdateDto.categoryPublicId()))")
    ProductUpdateDetails toProductUpdateDetails(ProductUpdateDto productUpdateDto);

    default Product updateProductFromDetails(ProductUpdateDetails productUpdateDetails,
                                             Product product) {
        return product.toBuilder()
                .name(productUpdateDetails.name() != null && !productUpdateDetails.name().isEmpty()
                              ? productUpdateDetails.name() : product.getName())
                .price(productUpdateDetails.price() != null ? productUpdateDetails.price() : product.getPrice())
                .stockQuantity(productUpdateDetails.stockQuantity() != null ? productUpdateDetails.stockQuantity()
                                       : product.getStockQuantity())
                .build();
    }

    default Set<ParametersDetails> toParametersDetails(List<ParametersDto> parameters) {
        if (parameters == null) {
            return Collections.emptySet();
        }
        return parameters.stream()
                .map(this::toParametersDetails)
                .collect(Collectors.toSet());
    }

    default String checkValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }
}