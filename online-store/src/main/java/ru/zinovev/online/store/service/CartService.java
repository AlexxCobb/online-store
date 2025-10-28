package ru.zinovev.online.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.CartDaoService;
import ru.zinovev.online.store.exception.dto.OutOfStockDto;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.exception.model.OutOfStockException;
import ru.zinovev.online.store.model.CartDetails;
import ru.zinovev.online.store.model.CartUpdateDetails;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartDaoService cartDaoService;
    private final ProductService productService;
    private final UserService userService;

    public CartDetails addProductToCart(String cartId, String publicUserId,
                                        String publicProductId,
                                        Integer quantity) {
        var cart = cartDaoService.getOrCreateCart(publicUserId, cartId);

        var product = productService.getByPublicId(publicProductId);
        var availableQuantity = product.stockQuantity();

        var existedCartItem = cart.cartItems()
                .stream()
                .filter(cartItemDetails -> cartItemDetails.publicProductId().equals(publicProductId))
                .findFirst();
        var newQuantity =
                existedCartItem.map(cartItemDetails -> cartItemDetails.quantity() + quantity).orElse(quantity);

        if (availableQuantity < newQuantity) {
            throw new OutOfStockException("Maximum quantity in stock that can be added - ",
                                          Collections.singletonList(
                                                  new OutOfStockDto(publicProductId, product.name(), newQuantity,
                                                                    availableQuantity)));
        }
        return cartDaoService.addProductToCart(cart, publicProductId, quantity);
    }

    public void updateCartWithRegisteredUser(String publicUserId, String publicCartId) {
        userService.findUserDetails(publicUserId);
        cartDaoService.updateCartWithRegisteredUser(publicUserId, publicCartId);
    }

    public CartDetails getUserCart(String publicUserId) {
        userService.findUserDetails(publicUserId);
        return cartDaoService.findUserCartDetails(publicUserId)
                .orElseThrow(() -> new NotFoundException("User cart with user id - " + publicUserId + " not found"));
    }

    public CartDetails getCart(String publicCartId, String publicUserId) {
        if (publicUserId != null) {
            return getUserCart(publicUserId);
        } else if (publicCartId != null) {
            return cartDaoService.findCartDetails(publicCartId).orElse(null);
        }
        return null;
    }

    public void removeProductFromCart(String publicCartId, String publicUserId, String publicProductId) {
        if (publicUserId != null) {
            userService.findUserDetails(publicUserId);
        }
        productService.getByPublicId(publicProductId);
        var cart = getCart(publicCartId, publicUserId);
        cartDaoService.removeItemFromCart(cart, publicProductId);
    }

    public void clearCart(String publicCartId, String publicUserId) {
        if (publicUserId != null) {
            userService.findUserDetails(publicUserId);
        }
        var cart = getCart(publicCartId, publicUserId);
        cartDaoService.clearCart(cart.publicCartId());
    }

    public void updateProductQuantityInCart(String publicUserId, List<CartUpdateDetails> cartUpdateDetails) {
        if (publicUserId != null) {
            userService.findUserDetails(publicUserId);
        }
        var cart = getCart(null, publicUserId);
        var cartDetailsMap = cartUpdateDetails.stream()
                .collect(Collectors.toMap(CartUpdateDetails::publicProductId, CartUpdateDetails::availableQuantity));
        cartDaoService.updateProductQuantityInCart(cart, cartDetailsMap);
    }
}
