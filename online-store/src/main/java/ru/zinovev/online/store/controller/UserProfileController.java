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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.zinovev.online.store.controller.dto.ChangePasswordDto;
import ru.zinovev.online.store.controller.dto.UserUpdateDto;
import ru.zinovev.online.store.exception.model.InvalidPasswordException;
import ru.zinovev.online.store.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/users")
public class UserProfileController {

    private final UserService userService;

    @GetMapping("/{publicUserId}")
    public String getUserDetails(@PathVariable String publicUserId, Model model) {
        log.debug("Received GET request to get user with id = {}", publicUserId);
        var user = userService.findUserDetails(publicUserId);
        model.addAttribute("user", user);

        return "user-info";
    }

    @GetMapping("/{publicUserId}/edit")
    public String editUserDetails(@PathVariable String publicUserId, @ModelAttribute UserUpdateDto userUpdateDto,
                                  Model model) {
        log.debug("Received GET request to edit user with id = {}", publicUserId);
        var user = userService.findUserDetails(publicUserId);
        model.addAttribute("user", user);
        return "edit-user";
    }

    @PatchMapping("/{publicUserId}/profile")
    public String updateUser(@PathVariable String publicUserId, @Valid UserUpdateDto userUpdateDto,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received PATCH request to update user with id = {}", publicUserId);
        if (bindingResult.hasErrors()) {
            var user = userService.findUserDetails(publicUserId);
            model.addAttribute("user", user);
            return "edit-user";
        }
        redirectAttributes.addFlashAttribute("successMessage", "ДАННЫЕ ПОЛЬЗОВАТЕЛЯ УСПЕШНО ОБНОВЛЕНЫ");
        userService.updateUser(publicUserId, userUpdateDto);
        return "redirect:/api/users/" + publicUserId;
    }

    @GetMapping("/{publicUserId}/password")
    public String editUserPassword(@PathVariable String publicUserId,
                                   @ModelAttribute ChangePasswordDto changePasswordDto, Model model) {
        log.debug("Received GET request to change password user with id = {}", publicUserId);
        var user = userService.findUserDetails(publicUserId);
        model.addAttribute("user", user);
        return "edit-password";
    }

    @PatchMapping("/{publicUserId}/password")
    public String changePassword(@PathVariable String publicUserId, @Valid ChangePasswordDto changePasswordDto,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.debug("Received PATCH request to update user password with id = {}", publicUserId);
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
            } else if (e.getMessage().equals("New password cannot match current password")){
                redirectAttributes.addFlashAttribute("errorMessage",
                                                     "Старый и новый пароль должный отличаться!");
            }
        }
        return "redirect:/api/users/" + publicUserId;
    }

    @DeleteMapping("/{publicUserId}")
    public String deleteUser(@PathVariable String publicUserId, RedirectAttributes redirectAttributes) {
        log.debug("Received DELETE request to delete user with id = {}", publicUserId);
        userService.deleteUser(publicUserId);
        redirectAttributes.addFlashAttribute("successMessage", "ПОЛЬЗОВАТЕЛЬ УСПЕШНО ОБНОВЛЕН");
        return "redirect:/api/users/home";
    }
}
