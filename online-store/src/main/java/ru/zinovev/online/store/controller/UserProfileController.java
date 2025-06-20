package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.model.UserDetails;
import ru.zinovev.online.store.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/users/{userId}")
public class UserProfileController {

    private final UserService userService;

    @GetMapping
    public UserDetails getUserDetails(@PathVariable String publicUserId) {
        log.debug("Received GET request to get user with id = {}", publicUserId);
        return userService.findUserDetails(publicUserId);
    }

    @PatchMapping("/info") //смену email выделять в отдельный эндпоинт?
    public UserDetails updateUser(@PathVariable String publicUserId, @Valid @RequestBody
    UserUpdateDto userUpdateDto) {
        log.debug("Received PATCH request to update user with id = {}", publicUserId);
        return userService.updateUser(publicUserId, userUpdateDto);
    }

    @PatchMapping("/password")
    public UserDetails changePassword(@PathVariable String publicUserId, @Valid @RequestBody
    ChangePasswordDto changePasswordDto) {
        log.debug("Received PATCH request to update user password with id = {}", publicUserId);
        return userService.changePassword(publicUserId, changePasswordDto);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String publicUserId) {
        log.debug("Received DELETE request to delete user with id = {}", publicUserId);
        userService.deleteUser(publicUserId);
    }
}
