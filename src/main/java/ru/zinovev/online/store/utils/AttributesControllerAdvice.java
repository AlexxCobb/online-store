package ru.zinovev.online.store.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    }
}