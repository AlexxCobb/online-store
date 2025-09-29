package ru.zinovev.online.store.controller.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class UserDto {
    private String name;
    private String email;
    private String publicUserId;
    private Boolean isAuthenticated;

    public Optional<String> getPublicUserId() {
        return Optional.ofNullable(publicUserId);
    }
}
