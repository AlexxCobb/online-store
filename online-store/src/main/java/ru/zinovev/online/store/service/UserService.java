package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserRegistrationDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.UserDaoService;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.InvalidPasswordException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.UserDetails;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDaoService userDaoService;
    private final PasswordEncoder passwordEncoder;

    public UserDetails createUser(@NonNull UserRegistrationDto userRegistrationDto) {
        userDaoService.findByEmailIgnoreCase(userRegistrationDto)
                .ifPresent(userDetails -> {
                    throw new BadRequestException(
                            "User with email - " + userRegistrationDto.email() + " already exist");
                });
        return userDaoService.createUser(userRegistrationDto);
    }

    public UserDetails updateUser(@NonNull String publicUserId, @NonNull UserUpdateDto userUpdateDto) {
        var userDetails = findUserDetails(publicUserId);
        return userDaoService.updateUser(userDetails, userUpdateDto);
    }

    public void deleteUser(@NonNull String publicUserId) {
        userDaoService.deleteUser(findUserDetails(publicUserId));
    }

    public UserDetails changePassword(@NonNull String publicUserId, @NonNull ChangePasswordDto changePasswordDto) {
        var userDetails = findUserDetails(publicUserId);
        var userPassword = userDaoService.findPasswordHashByPublicId(userDetails)
                .orElseThrow(() -> new NotFoundException("Password not found"));
        if (!passwordEncoder.matches(changePasswordDto.currentPassword(), userPassword)) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        if (passwordEncoder.matches(changePasswordDto.newPassword(), userPassword)) {
            throw new InvalidPasswordException("New password cannot match current password");
        }
        return userDaoService.changePassword(userDetails, changePasswordDto);
    }

    public UserDetails findUserDetails(String publicUserId) {
        return userDaoService.findByPublicId(publicUserId).orElseThrow(
                () -> new NotFoundException("User with id - " + publicUserId + " not found"));
    }
}
