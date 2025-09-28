package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.exception.model.InvalidPasswordException;
import ru.zinovev.online.store.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/users")
public class UserProfileController {

    private final UserService userService;
    private final UserDto sessionUserDto;

    @GetMapping()
    public String getUserDetails(Model model) {
        if (sessionUserDto.getPublicUserId() != null) {
            log.debug("Received GET request to get user with id = {}", sessionUserDto.getPublicUserId());

            var publicUserId = sessionUserDto.getPublicUserId();
            var user = userService.findUserDetails(publicUserId);
            model.addAttribute("user", user);
        }
        return "user-info";
    }

    @GetMapping("/edit")
    public String editUserDetails(@ModelAttribute UserUpdateDto userUpdateDto,
                                  Model model) {
        if (sessionUserDto.getPublicUserId() != null) {
            log.debug("Received GET request to edit user with id = {}", sessionUserDto.getPublicUserId());

            var publicUserId = sessionUserDto.getPublicUserId();
            var user = userService.findUserDetails(publicUserId);
            model.addAttribute("user", user);
        }
        return "edit-user";
    }

    @PatchMapping("/profile")
    public String updateUser(@Valid UserUpdateDto userUpdateDto,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (userService.checkExistByEmail(userUpdateDto.email())) {
            bindingResult.rejectValue("email", "error.userRegistrationDto",
                                      "Пользователь с таким email - " + userUpdateDto.email()
                                              + " уже существует.");
        }
        if (bindingResult.hasErrors()) {
            var publicUserId = sessionUserDto.getPublicUserId();
            var user = userService.findUserDetails(publicUserId);
            model.addAttribute("user", user);
            return "edit-user";
        }
        if (sessionUserDto.getPublicUserId() != null) {
            log.debug("Received PATCH request to update user with id = {}", sessionUserDto.getPublicUserId());
            var publicUserId = sessionUserDto.getPublicUserId();
            redirectAttributes.addFlashAttribute("successMessage", "ДАННЫЕ ПОЛЬЗОВАТЕЛЯ УСПЕШНО ОБНОВЛЕНЫ");
            userService.updateUser(publicUserId, userUpdateDto);
        }
        return "redirect:/api/users";
    }

    @GetMapping("/password")
    public String editUserPassword(
            @ModelAttribute ChangePasswordDto changePasswordDto, Model model) {
        if (sessionUserDto.getPublicUserId() != null) {
            log.debug("Received GET request to change password user with id = {}", sessionUserDto.getPublicUserId());

            var publicUserId = sessionUserDto.getPublicUserId();
            var user = userService.findUserDetails(publicUserId);
            model.addAttribute("user", user);
        }
        return "edit-password";
    }

    @PatchMapping("/password")
    public String changePassword(@Valid ChangePasswordDto changePasswordDto,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (sessionUserDto.getPublicUserId() != null) {
            log.debug("Received PATCH request to update user password with id = {}", sessionUserDto.getPublicUserId());
            var publicUserId = sessionUserDto.getPublicUserId();

            if (bindingResult.hasErrors()) {
                return "edit-password";
            }
            try {
                userService.changePassword(publicUserId, changePasswordDto);
                redirectAttributes.addFlashAttribute("successMessage", "ПАРОЛЬ УСПЕШНО ОБНОВЛЕН");
            } catch (InvalidPasswordException e) {
                if (e.getMessage().equals("Current password is incorrect")) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                                                         "Вы ввели неверный текущий пароль");
                    return "redirect:/api/users/password";
                } else if (e.getMessage().equals("New password cannot match current password")) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                                                         "Старый и новый пароль должный отличаться!");
                    return "redirect:/api/users/password";
                }
            }
        }
        return "redirect:/api/users";
    }

    @DeleteMapping()
    public String deleteUser(RedirectAttributes redirectAttributes) {
        if (sessionUserDto.getPublicUserId() != null) {
            log.debug("Received DELETE request to delete user with id = {}", sessionUserDto.getPublicUserId());
            var publicUserId = sessionUserDto.getPublicUserId();
            userService.deleteUser(publicUserId);
            redirectAttributes.addFlashAttribute("successMessage", "ПОЛЬЗОВАТЕЛЬ УСПЕШНО ОБНОВЛЕН");
        }
        return "redirect:/api/users/home";
    }
}