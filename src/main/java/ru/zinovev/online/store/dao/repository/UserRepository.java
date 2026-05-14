package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zinovev.online.store.dao.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByPublicUserId(String publicUserId);

    boolean existsByEmailIgnoreCase(String email);

    @Query("select u.passwordHash from User u where u.publicUserId = :publicUserId")
    Optional<String> findPasswordHashByPublicId(String publicUserId);

    @Modifying
    @Query("""
            update User u set
                  u.name = COALESCE(:name, u.name),
                  u.lastname = COALESCE(:lastname, u.lastname),
                  u.email = COALESCE(:email, u.email)
                  where u.publicUserId = :publicUserId
            """)
    int updateNameAndLastnameAndEmailByPublicUserId(String name, String lastname, String email, String publicUserId);

    @Modifying
    @Query("update User u set u.passwordHash = :passwordHash where u.publicUserId = :publicUserId")
    void updatePasswordHashByPublicUserId(String passwordHash, String publicUserId);

    @Modifying
    @Query("delete from User u where u.publicUserId = :publicUserId")
    int deleteByPublicUserId(@Param("publicUserId") String publicUserId);
}
