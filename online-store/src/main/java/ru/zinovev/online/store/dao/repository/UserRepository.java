package ru.zinovev.online.store.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zinovev.online.store.dao.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
