package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.UserDaoService;
import ru.zinovev.online.store.exception.model.InvalidPasswordException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.UserDetails;
import ru.zinovev.online.store.model.UserRegistrationDetails;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDaoService userDaoService;
    private final PasswordEncoder passwordEncoder;

    public UserDetails createUser(@NonNull UserRegistrationDetails userRegistrationDetails) {
        var password = passwordEncoder.encode(userRegistrationDetails.password());
        return userDaoService.createUser(userRegistrationDetails, password);
    }

    public void updateUser(@NonNull String publicUserId, @NonNull UserUpdateDto userUpdateDto) {
        var userDetails = findUserDetails(publicUserId);
        userDaoService.updateUser(userDetails, userUpdateDto);
    }

    public void deleteUser(@NonNull String publicUserId) {
        userDaoService.deleteUser(findUserDetails(publicUserId));
    }

    public void changePassword(@NonNull String publicUserId, @NonNull ChangePasswordDto changePasswordDto) {
        var userDetails = findUserDetails(publicUserId);
        var userPassword = userDaoService.findPasswordHashByPublicId(userDetails)
                .orElseThrow(() -> new NotFoundException("Password not found"));
        if (!passwordEncoder.matches(changePasswordDto.currentPassword(), userPassword)) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        if (passwordEncoder.matches(changePasswordDto.newPassword(), userPassword)) {
            throw new InvalidPasswordException("New password cannot match current password");
        }
        var password = passwordEncoder.encode(changePasswordDto.newPassword());
        userDaoService.changePassword(userDetails, password);
    }

    public UserDetails findUserDetails(String publicUserId) {
        return userDaoService.findByPublicId(publicUserId).orElseThrow(
                () -> new NotFoundException("User with id - " + publicUserId + " not found"));
    }

    public Boolean checkExistByEmail(String email) {
        var user = userDaoService.findByEmailIgnoreCase(email);
        return user.isPresent();
    }

    public UserDetails findUserDetailsByEmail(String email) {
        return userDaoService.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User with email - " + email + " not found"));
    }
}
