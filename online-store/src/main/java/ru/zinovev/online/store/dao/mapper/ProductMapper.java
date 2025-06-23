package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.zinovev.online.store.controller.dto.ProductUpdateDto;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.model.ProductDetails;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "publicProductId", source = "publicProductId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "categoryDetails", source = "category")
    @Mapping(target = "parameters", source = "parameters")
    @Mapping(target = "weight", source = "weight")
    @Mapping(target = "volume", source = "volume")
    @Mapping(target = "stockQuantity", source = "stockQuantity")
    ProductDetails toProductDetails(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "parameters",
             expression = "java(updateParameters(product.getParameters(),productUpdateDto.parameters()))")
    void updateProductFromProductUpdateDto(@MappingTarget Product product, ProductUpdateDto productUpdateDto);

    default Map<String, String> updateParameters(Map<String, String> existParam, Map<String, String> updateParam) {
        if (updateParam.isEmpty()) {
            return existParam;
        }
        updateParam.forEach((k, v) -> {
            if (v == null) {
                existParam.remove(k);
            }
            existParam.put(k, v);
        });
        return existParam;
    }
}
