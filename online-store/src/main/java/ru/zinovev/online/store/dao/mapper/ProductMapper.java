package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zinovev.online.store.controller.dto.ParametersDto;
import ru.zinovev.online.store.controller.dto.ProductDto;
import ru.zinovev.online.store.controller.dto.ProductParamDto;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.entity.OrderItem;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.entity.ProductParameter;
import ru.zinovev.online.store.dao.entity.ProductView;
import ru.zinovev.online.store.model.ParametersDetails;
import ru.zinovev.online.store.model.ProductDetails;
import ru.zinovev.online.store.model.ProductParamDetails;
import ru.zinovev.online.store.model.ProductShortDetails;
import ru.zinovev.online.store.model.ProductUpdateDetails;
import ru.zinovev.online.store.model.TopProductDetails;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryPublicId", source = "category.publicCategoryId")
    ProductDetails toProductDetails(Product product);

    // @Mapping(target = "value", expression = "java(productParameter.getValue().toLowerCase())")
    ParametersDetails toParametersDetails(ProductParameter productParameter);

    @Mapping(target = "publicProductId", ignore = true)
    ProductDetails toProductDetails(ProductDto productDto);

    ParametersDetails toParametersDetails(ParametersDto parametersDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductParameter toProductParameter(ParametersDetails parametersDetails);

    Set<ProductParameter> toProductParameters(Set<ParametersDetails> parametersDetails);

    ProductShortDetails toProductShortDetails(Product product);

    @Mapping(target = "publicProductId", source = "product.publicProductId")
    @Mapping(target = "name", source = "product.name")
    ProductShortDetails toProductShortDetails(OrderItem orderItem);

    TopProductDetails toTopProductDetails(ProductView productView);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "parameters", ignore = true)
    void updateProductFromProductUpdateDetails(ProductUpdateDetails updateDetails, @MappingTarget Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ProductParamDetails toProductParamDetails(ProductParamDto productParamDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ProductUpdateDetails toProductUpdateDetails(ProductUpdateDto productUpdateDto);
}
