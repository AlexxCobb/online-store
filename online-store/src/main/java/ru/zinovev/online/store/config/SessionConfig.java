package ru.zinovev.online.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import ru.zinovev.online.store.controller.dto.UserDto;

@Component
public class SessionConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserDto sessionUserDto() {
        var user = new UserDto();
        user.setIsAuthenticated(false);
        return user;
    }
}
