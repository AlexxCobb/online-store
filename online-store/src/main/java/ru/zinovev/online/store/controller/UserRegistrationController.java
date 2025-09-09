package ru.zinovev.online.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.zinovev.online.store.controller.dto.UserRegistrationDto;
import ru.zinovev.online.store.model.UserDetails;
import ru.zinovev.online.store.service.CartService;
import ru.zinovev.online.store.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/auth")
public class UserRegistrationController {

    private final UserService userService;
    private final CartService cartService;

    @PostMapping
    public String addUser(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                          @Valid UserRegistrationDto userRegistrationDto, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes, Model model) {
        log.debug("Received POST request to register user");
        if (!userRegistrationDto.password().equals(userRegistrationDto.confirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userRegistrationDto", "Пароли не совпадают");
        }
        if (bindingResult.hasErrors()) {
            return "add-user";
        }
        var user = userService.createUser(userRegistrationDto);
        cartService.updateCartWithRegisteredUser(user.publicUserId(), publicCartId);
        redirectAttributes.addFlashAttribute("successMessage", "ПОЛЬЗОВАТЕЛЬ " + user.name() + " " + user.lastname()
                + " УСПЕШНО ЗАРЕГИСТРИРОВАН");
        redirectAttributes.addFlashAttribute("publicUserId", user.publicUserId());
        return "redirect:/api/users/cart"; //login
    }

    @GetMapping
    public String getUserRegistryForm(@ModelAttribute UserRegistrationDto userRegistrationDto) {
        log.debug("Received GET request to register user form");
        return "add-user";
    }
}
