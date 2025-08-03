package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zinovev.online.store.dao.entity.CartItem;
import ru.zinovev.online.store.model.CartItemDetails;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "publicProductId", source = "product.publicProductId")
    CartItemDetails toCartItemDetails(CartItem cartItem);
}
