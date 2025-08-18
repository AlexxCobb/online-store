package ru.zinovev.online.store.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class PathControllerAdvice {
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }
}