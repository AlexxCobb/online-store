package ru.zinovev.online.store.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.InvalidPasswordException;
import ru.zinovev.online.store.exception.model.NotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleInternalServerError(HttpServletRequest request, Exception ex, Model model) {
        var referer = request.getHeader("Referer");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/error");
        if (referer != null && !referer.isEmpty()) {
            modelAndView.addObject("previousUrl", referer);
        }
        modelAndView.addObject("error", "Ошибка запроса");
        modelAndView.addObject("message", ex.getMessage());
        log.error("Something unexpected happened. Please try again later. {}", ex.getMessage(), ex);
        return modelAndView;
    }


    @ExceptionHandler
    public ModelAndView handleNotFoundException(HttpServletRequest request, NotFoundException ex, Model model) {
        var referer = request.getHeader("Referer");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/404");
        if (referer != null && !referer.isEmpty()) {
            modelAndView.addObject("previousUrl", referer);
        }
        modelAndView.addObject("error", "Ошибка запроса");
        modelAndView.addObject("message", ex.getMessage());
        log.warn("The requested resource was not found. {}", ex.getMessage(), ex);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleBadRequestException(HttpServletRequest request, BadRequestException ex, Model model) {
        var referer = request.getHeader("Referer");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/400");
        if (referer != null && !referer.isEmpty()) {
            modelAndView.addObject("previousUrl", referer);
        }
        modelAndView.addObject("error", "Ошибка запроса");
        modelAndView.addObject("message", ex.getMessage());
        log.warn("Bad Request with invalid data. {}", ex.getMessage(), ex);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleForbiddenException(HttpServletRequest request, InvalidPasswordException ex, Model model) {
        var referer = request.getHeader("Referer");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/403");
        if (referer != null && !referer.isEmpty()) {
            modelAndView.addObject("previousUrl", referer);
        }
        modelAndView.addObject("error", "Ошибка запроса");
        modelAndView.addObject("message", ex.getMessage());
        log.warn("Bad Request with invalid data. {}", ex.getMessage(), ex);
        return modelAndView;
    }
}
