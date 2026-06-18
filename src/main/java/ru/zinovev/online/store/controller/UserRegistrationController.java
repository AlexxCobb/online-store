package ru.zinovev.online.store.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.zinovev.online.store.config.CustomAuthenticationSuccessHandler;
import ru.zinovev.online.store.controller.dto.UserLoginDto;
import ru.zinovev.online.store.controller.dto.UserRegistrationDto;
import ru.zinovev.online.store.dao.mapper.UserMapper;
import ru.zinovev.online.store.service.CartService;
import ru.zinovev.online.store.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/auth")
public class UserRegistrationController {

    private final UserService userService;
    private final CartService cartService;
    private final UserMapper userMapper;
    private final CustomAuthenticationSuccessHandler successHandler;

    @PostMapping
    public String addUser(@CookieValue(value = "CART_ID", required = false) String publicCartId,
                          @Valid UserRegistrationDto userRegistrationDto, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes, HttpServletRequest request,
                          HttpServletResponse response) {
        log.debug("Received POST request to register user");
        if (!userRegistrationDto.password().equals(userRegistrationDto.confirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userRegistrationDto", "Пароли не совпадают");
        }
        if (userService.checkExistByEmail(userRegistrationDto.email())) {
            bindingResult.rejectValue("email", "error.userRegistrationDto",
                                      "Пользователь с таким email - " + userRegistrationDto.email()
                                              + " уже существует.");
        }
        if (bindingResult.hasErrors()) {
            return "add-user";
        }
        var user = userService.createUser(userMapper.toUserRegistrationDetails(userRegistrationDto));
        cartService.updateCartWithRegisteredUser(user.publicUserId(), publicCartId);
        redirectAttributes.addFlashAttribute("successMessage", "ПОЛЬЗОВАТЕЛЬ " + user.name() + " " + user.lastname()
                + " УСПЕШНО ЗАРЕГИСТРИРОВАН");
        try {
            request.login(userRegistrationDto.email(), userRegistrationDto.password());
            var auth = SecurityContextHolder.getContext().getAuthentication();
            var securityContextRepository = new HttpSessionSecurityContextRepository();
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
            successHandler.handleAuthenticationSuccess(request, response, auth);
            return "redirect:/api/users/products";
        } catch (ServletException ex) {
            log.warn("Auto-login failed, but registration successful", ex);
            redirectAttributes.addFlashAttribute("successMessage",
                                                 "Регистрация успешна! Пожалуйста, войдите в систему.");
        }
        return "redirect:/api/auth/sign-in";
    }

    @GetMapping("/sign-in")
    public String singIn(@ModelAttribute UserLoginDto userLoginDto,
                         @RequestParam(value = "error", required = false) String error,
                         Model model) {
        log.debug("Received GET request to login user form");

        if (error != null) {
            model.addAttribute("errorMessage", "Неправильный адрес электронной почты или пароль.");
        }
        return "login-user";
    }

    @GetMapping
    public String getUserRegistryForm(@ModelAttribute UserRegistrationDto userRegistrationDto) {
        log.debug("Received GET request to register user form");
        return "add-user";
    }
}
