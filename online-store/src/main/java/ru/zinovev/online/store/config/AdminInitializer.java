package ru.zinovev.online.store.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.dao.entity.enums.RoleName;
import ru.zinovev.online.store.service.UserService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserService userService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.birthday}")
    private LocalDate adminBirthday;

    @PostConstruct
    public void initAdmin() {
        if (!userService.checkExistByEmail(adminEmail)) {
            userService.initAdmin(adminEmail, adminPassword, adminBirthday, RoleName.ROLE_ADMIN);
        }
    }
}