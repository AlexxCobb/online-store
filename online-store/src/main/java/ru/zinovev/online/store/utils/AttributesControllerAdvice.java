package ru.zinovev.online.store.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.zinovev.online.store.service.UserService;

@ControllerAdvice
@RequiredArgsConstructor
public class AttributesControllerAdvice {

    private final UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails userDetails) {
            var email = userDetails.getUsername();
            var user = userService.findUserDetailsByEmail(email);
            model.addAttribute("publicUserId", user.publicUserId());
        } else {
            model.addAttribute("publicUserId", null);
        }
    }
}