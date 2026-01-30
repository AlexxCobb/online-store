package ru.zinovev.online.store.model;

public record UserDetails(
        String publicUserId,
        String email,
        String name,
        String lastname
) {
}
