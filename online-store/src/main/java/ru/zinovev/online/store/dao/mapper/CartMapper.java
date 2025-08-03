package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zinovev.online.store.dao.entity.Cart;
import ru.zinovev.online.store.model.CartDetails;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cart))")
    @Mapping(target = "publicUserId", source = "user.publicUserId")
    CartDetails toCartDetails(Cart cart);

    default BigDecimal calculateTotalPrice(Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cart.getItems().stream().map(cartItem -> cartItem.getProduct().getPrice().multiply(
                BigDecimal.valueOf(cartItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
