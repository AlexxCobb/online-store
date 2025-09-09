package ru.zinovev.online.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.CartDaoService;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.exception.model.OutOfStockException;
import ru.zinovev.online.store.model.CartDetails;

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
            throw new OutOfStockException("Maximum quantity in stock that can be added - " + availableQuantity);
        }
        return cartDaoService.addProductToCart(cart, publicProductId, quantity);
    }

    public void updateCartWithRegisteredUser(String publicUserId, String publicCartId){
        userService.findUserDetails(publicUserId);
      cartDaoService.updateCartWithRegisteredUser(publicUserId,publicCartId);
    }

    public CartDetails getUserCart(String publicUserId) {
        userService.findUserDetails(publicUserId);
        return cartDaoService.findUserCartDetails(publicUserId)
                .orElseThrow(() -> new NotFoundException("User cart with user id - " + publicUserId + " not found"));
    }

    public CartDetails getCart(String publicCartId, String publicUserId) {
        if (publicUserId != null) {
            getUserCart(publicUserId);
        }
        return cartDaoService.findCartDetails(publicCartId).orElse(null);
    }
}
