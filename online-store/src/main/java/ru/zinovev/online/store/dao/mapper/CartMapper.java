package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zinovev.online.store.dao.entity.Cart;
import ru.zinovev.online.store.dao.entity.CartItem;
import ru.zinovev.online.store.dao.entity.ProductParameter;
import ru.zinovev.online.store.model.CartDetails;
import ru.zinovev.online.store.model.CartItemDetails;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cart))")
    @Mapping(target = "publicUserId", source = "user.publicUserId")
    @Mapping(target = "cartItems", source = "items")
    @Mapping(target = "totalQuantity", expression = "java(calculateTotalQuantity(cart))")
    CartDetails toCartDetails(Cart cart);

    @Mapping(target = "publicProductId", source = "product.publicProductId")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "imagePath", source = "product.imagePath")
    @Mapping(target = "brand", expression = "java(getBrand(cartItem))")
    @Mapping(target = "color", expression = "java(getColor(cartItem))")
    CartItemDetails toCartItemDetails(CartItem cartItem);

    default BigDecimal calculateTotalPrice(Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cart.getItems().stream().map(cartItem -> cartItem.getProduct().getPrice().multiply(
                BigDecimal.valueOf(cartItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default Integer calculateTotalQuantity(Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            return 0;
        }
        return cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    default String getBrand(CartItem cartItem) {
        return getParameterValue(cartItem, "brand");
    }

    default String getColor(CartItem cartItem) {
        return getParameterValue(cartItem, "color");
    }

    default String getParameterValue(CartItem cartItem, String parameterKey) {
        if (cartItem == null || cartItem.getProduct().getParameters() == null) {
            return "Не указан";
        }
        return cartItem.getProduct()
                .getParameters()
                .stream()
                .filter(productParameter -> productParameter.getKey().equals(parameterKey))
                .map(ProductParameter::getValue)
                .findFirst().orElse("Не указан");
    }
}
