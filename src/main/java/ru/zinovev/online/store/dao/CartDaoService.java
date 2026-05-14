package ru.zinovev.online.store.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.dao.entity.Cart;
import ru.zinovev.online.store.dao.entity.CartItem;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.mapper.CartMapper;
import ru.zinovev.online.store.dao.repository.CartItemRepository;
import ru.zinovev.online.store.dao.repository.CartRepository;
import ru.zinovev.online.store.dao.repository.ProductRepository;
import ru.zinovev.online.store.exception.model.AlreadyExistException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.CartDetails;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CartDaoService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
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
        var cartItems = cart.getItems();
        if (item.isPresent()) {
            var existingItem = item.get();
            var totalQuantity = item.get().getQuantity() + quantity;
            var updatedItem = existingItem.toBuilder()
                    .quantity(totalQuantity)
                    .cart(cart)
                    .build();
            cartItems.remove(existingItem);
            cartItems.add(updatedItem);
        } else {
            var cartItem = CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(quantity)
                    .build();
            cartItems.add(cartItem);
        }
        var cartWithUpdateItems = cart.toBuilder().items(cartItems).build();
        return cartMapper.toCartDetails(cartRepository.save(cartWithUpdateItems));
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
        var cart = cartRepository.findByPublicCartId(publicCartId);
        var userCart = cartRepository.findByUserPublicUserId(publicUserId);

        if (cart.isEmpty() && userCart.isEmpty()) {
            cartRepository.save(Cart.builder()
                                        .user(user)
                                        .publicCartId(UUID.randomUUID().toString())
                                        .build());
        } else if (cart.isPresent() && userCart.isEmpty()) {
            var updated = cartRepository.updateUserBy(publicCartId, user);
            if (updated == 0) {
                throw new AlreadyExistException("Cart with id - " + publicCartId + " assigned to another user");
            }
        } else if (cart.isPresent()) {
            if (cart.get().getUser() != null) {
                return;
            }
            mergeCarts(cart.get(), userCart.get());
            cartItemRepository.deleteAllByCartId(cart.get().getId());
            cartRepository.delete(cart.get());
        }
    }

    @Transactional
    public void updateProductQuantityInCart(CartDetails cartDetails, Map<String, Integer> cartUpdateDetails) {
        cartUpdateDetails.forEach((productId, quantity) -> {
            var cartId = cartDetails.publicCartId();
            if (quantity <= 0) {
                cartItemRepository.deleteByCartIdAndProductId(cartId, productId);
            } else {
                var updated = cartItemRepository.updateQuantity(cartId, productId, quantity);
                if (updated == 0) {
                    log.warn("Product {} not found in cart {}", productId, cartId);
                }
            }
        });
    }

    @Transactional
    public void removeItemFromCart(CartDetails cartDetails, String publicProductId) {
        var cart = cartRepository.findByPublicCartId(cartDetails.publicCartId())
                .orElseThrow(
                        () -> new NotFoundException("Cart with id - " + cartDetails.publicCartId() + " not found"));
        var item = cart.getItems()
                .stream()
                .filter(cartItem -> cartItem.getProduct().getPublicProductId().equals(publicProductId))
                .findFirst();

        item.ifPresent(cartItem -> cart.getItems().remove(cartItem));
        cartMapper.toCartDetails(cart);
    }

    @Transactional
    public void clearCart(String publicCartId) {
        var cart = cartRepository.findByPublicCartId(publicCartId)
                .orElseThrow(() -> new NotFoundException("Cart with id - " + publicCartId + " not found"));
        cart.getItems().clear();
    }

    public Optional<CartDetails> findCartDetails(String publicCartId) {
        return cartRepository.findByPublicCartId(publicCartId)
                .map(cartMapper::toCartDetails);
    }

    public Optional<CartDetails> findUserCartDetails(String publicUserId) {
        return cartRepository.findByUserPublicUserId(publicUserId).map(cartMapper::toCartDetails);
    }

    private void mergeCarts(Cart cart, Cart userCart) {
        var allProductIds = Stream.concat(userCart.getItems().stream(), cart.getItems().stream())
                .map(item -> item.getProduct().getId())
                .distinct()
                .toList();
        var stockProducts = productRepository.findAllById(allProductIds).stream().collect(
                Collectors.toMap(Product::getPublicProductId, Product::getStockQuantity));

        var tempItems = cart.getItems();
        var userItemsMap = userCart.getItems()
                .stream()
                .collect(
                        Collectors.toMap(cartItem -> cartItem.getProduct().getPublicProductId(), cartItem -> cartItem));

        tempItems.forEach(cartItem -> {
            var item = userItemsMap.get(cartItem.getProduct().getPublicProductId());
            var productStockQuantity = stockProducts.getOrDefault(cartItem.getProduct().getPublicProductId(), 0);

            if (item != null) {
                var newQuantity = Math.min(item.getQuantity() + cartItem.getQuantity(), productStockQuantity);
                cartItemRepository.updateQuantity(userCart.getPublicCartId(), item.getProduct().getPublicProductId(),
                                                  newQuantity);
            } else if (productStockQuantity > 0) {
                var quantity = Math.min(cartItem.getQuantity(), productStockQuantity);
                var newItem = CartItem.builder()
                        .cart(userCart)
                        .product(cartItem.getProduct())
                        .quantity(quantity)
                        .build();
                cartItemRepository.save(newItem);
            }
        });
    }
}