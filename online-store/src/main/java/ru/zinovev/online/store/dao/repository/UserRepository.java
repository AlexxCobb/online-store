package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.zinovev.online.store.dao.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByPublicUserId(String publicUserId);

    @Query("select u.passwordHash from User u where u.publicUserId = :publicUserId")
    Optional<String> findPasswordHashByPublicId(String publicUserId);
}
