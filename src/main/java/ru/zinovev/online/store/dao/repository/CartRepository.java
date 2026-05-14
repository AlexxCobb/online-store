package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.zinovev.online.store.dao.entity.Cart;
import ru.zinovev.online.store.dao.entity.User;

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

    @Modifying
    @Query("update Cart c set c.user = :user where c.publicCartId = :publicCartId and c.user is null")
    int updateUserBy(String publicCartId, User user);
}
