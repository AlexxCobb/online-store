package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserPublicUserId(String publicUserId);

    Optional<Cart> findByPublicCartId(String publicCartId);

}
