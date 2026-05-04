package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zinovev.online.store.dao.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Modifying
    @Query("delete from CartItem ci where ci.cart.publicCartId = :cartId and ci.product.publicProductId = :productId")
    void deleteByCartIdAndProductId(@Param("cartId") String cartId, @Param("productId") String productId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update CartItem ci
            set ci.quantity = :quantity
            where ci.cart.publicCartId = :cartId and ci.product.publicProductId = :productId
            """)
    int updateQuantity(@Param("cartId") String cartId, @Param("productId") String productId, @Param("quantity") int quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}
