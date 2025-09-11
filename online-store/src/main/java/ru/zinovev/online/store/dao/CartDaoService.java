package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.dao.entity.Cart;
import ru.zinovev.online.store.dao.entity.CartItem;
import ru.zinovev.online.store.dao.mapper.CartMapper;
import ru.zinovev.online.store.dao.repository.CartRepository;
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CartDetails;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartDaoService {

    private final CartRepository cartRepository;
    private final UserDaoService userDaoService;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Transactional
    public CartDetails addProductToCart(CartDetails cartDetails,
                                        String publicProductId,
                                        Integer quantity) {
        var product = productRepository.findByPublicProductId(publicProductId)
                .orElseThrow(() -> new NotFoundException("Product with publicId = " + publicProductId +
                                                                 " , not found"));
        var cart = cartRepository.findByPublicCartId(cartDetails.publicCartId())
                .orElseThrow(
                        () -> new NotFoundException("Cart with id - " + cartDetails.publicCartId() + " not found"));
        var item = cart.getItems()
                .stream()
                .filter(cartItem -> cartItem.getProduct().getPublicProductId().equals(publicProductId))
                .findFirst();
        if (item.isPresent()) {
            var existingItem = item.get();
            var totalQuantity = item.get().getQuantity() + quantity;
            var updatedItem = existingItem.toBuilder()
                    .quantity(totalQuantity)
                    .cart(cart)
                    .build();
            cart.getItems().remove(existingItem);
            cart.getItems().add(updatedItem);
        } else {
            var cartItem = CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(cartItem);
        }
        return cartMapper.toCartDetails(cartRepository.save(cart));
    }

    @Transactional
    public CartDetails getOrCreateCart(String publicUserId, String publicCartId) {
        if (publicUserId != null) {
            var user = userDaoService.getByPublicId(publicUserId);
            var userCart = cartRepository.findByUserPublicUserId(publicUserId)
                    .orElseGet(() -> cartRepository.save(Cart.builder()
                                                                 .user(user)
                                                                 .publicCartId(UUID.randomUUID().toString())
                                                                 .build()));
            return cartMapper.toCartDetails(userCart);
        } else {
            var guestCart = cartRepository.findByPublicCartId(publicCartId)
                    .orElseGet(() -> cartRepository.save(Cart.builder()
                                                                 .publicCartId(UUID.randomUUID().toString())
                                                                 .build()));

            return cartMapper.toCartDetails(guestCart);
        }
    }

    @Transactional
    public void updateCartWithRegisteredUser(String publicUserId, String publicCartId) {
        var user = userDaoService.getByPublicId(publicUserId);
        var cart = cartRepository.findByPublicCartId(publicCartId)
                .orElseThrow(() -> new NotFoundException("Cart with id - " + publicCartId + " not found"));
        var cartWithUser = cart.toBuilder()
                .user(user)
                .build();
        cartRepository.save(cartWithUser);
    }

    public Optional<CartDetails> findCartDetails(String publicCartId) {
        return cartRepository.findByPublicCartId(publicCartId)
                .map(cartMapper::toCartDetails);
    }


    public Optional<CartDetails> findUserCartDetails(String publicUserId) {
        return cartRepository.findByUserPublicUserId(publicUserId).map(cartMapper::toCartDetails);
    }

    @Transactional
    public CartDetails removeItemFromCart(CartDetails cartDetails, String publicProductId) {
        var cart = cartRepository.findByPublicCartId(cartDetails.publicCartId())
                .orElseThrow(
                        () -> new NotFoundException("Cart with id - " + cartDetails.publicCartId() + " not found"));
        var item = cart.getItems()
                .stream()
                .filter(cartItem -> cartItem.getProduct().getPublicProductId().equals(publicProductId))
                .findFirst();

        item.ifPresent(cartItem -> cart.getItems().remove(cartItem));
        return cartMapper.toCartDetails(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(String publicCartId) {
        var cart = cartRepository.findByPublicCartId(publicCartId)
                .orElseThrow(() -> new NotFoundException("Cart with id - " + publicCartId + " not found"));
        cartRepository.delete(cart);
    }
}