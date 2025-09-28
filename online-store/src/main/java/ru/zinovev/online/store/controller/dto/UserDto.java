package ru.zinovev.online.store.controller.dto;

import lombok.Data;

@Data
public class UserDto {
    private String name;
    private String email;
    private String publicUserId;
    private Boolean isAuthenticated;
}
