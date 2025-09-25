package ru.zinovev.online.store.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.dao.RoleDaoService;
import ru.zinovev.online.store.dao.UserDaoService;
import ru.zinovev.online.store.dao.entity.enums.RoleName;
import ru.zinovev.online.store.exception.model.InvalidPasswordException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.model.UserDetails;
import ru.zinovev.online.store.model.UserRegistrationDetails;

import java.security.Principal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDaoService userDaoService;
    private final PasswordEncoder passwordEncoder;
    private final RoleDaoService roleDaoService;

    public UserDetails createUser(@NonNull UserRegistrationDetails userRegistrationDetails) {
        var password = passwordEncoder.encode(userRegistrationDetails.password());
        return userDaoService.createUser(userRegistrationDetails, password);
    }

    public void initAdmin(String adminEmail, String adminPassword, LocalDate adminBirthday, RoleName roleName) {
        var adminRole = roleDaoService.findByName(roleName);
        userDaoService.initAdmin(adminEmail, passwordEncoder.encode(adminPassword), adminBirthday, adminRole);
    }

    public UserDetails updateUser(@NonNull String publicUserId, @NonNull UserUpdateDto userUpdateDto) {
        var userDetails = findUserDetails(publicUserId);
        return userDaoService.updateUser(userDetails, userUpdateDto);
    }

    public String findPublicUserId(Principal principal) {
        var email = principal.getName();
        var user = userDaoService.findByEmailIgnoreCase(email).orElseGet(null);
               // .orElseThrow(() -> new NotFoundException("User with email - " + email + " not found"));
        return user.publicUserId();
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
        var password = passwordEncoder.encode(changePasswordDto.newPassword());
        return userDaoService.changePassword(userDetails, password);
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
