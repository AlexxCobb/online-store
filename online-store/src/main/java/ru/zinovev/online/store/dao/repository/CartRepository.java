package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {
            "items",
            "items.product",
            "items.product.parameters"
    })
    Optional<Cart> findByUserPublicUserId(String publicUserId);

    @EntityGraph(attributePaths = {
            "items",
            "items.product",
            "items.product.parameters"
    })
    Optional<Cart> findByPublicCartId(String publicCartId);

}
