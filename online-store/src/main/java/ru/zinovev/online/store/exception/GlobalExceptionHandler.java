package ru.zinovev.online.store.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.InvalidPasswordException;
import ru.zinovev.online.store.exception.model.NotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleInternalServerError(Exception ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/error");
        modelAndView.addObject("error", "Ошибка запроса");
        modelAndView.addObject("message", ex.getMessage());
        log.error("Something unexpected happened. Please try again later. {}", ex.getMessage(), ex);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleNotFoundException(NotFoundException ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/404");
        modelAndView.addObject("error", "Ошибка запроса");
        modelAndView.addObject("message", ex.getMessage());
        log.warn("The requested resource was not found. {}", ex.getMessage(), ex);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView handleBadRequestException(BadRequestException ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/400");
        modelAndView.addObject("error", "Ошибка запроса");
        modelAndView.addObject("message", ex.getMessage());
        log.warn("Bad Request with invalid data. {}", ex.getMessage(), ex);
        return modelAndView;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleForbiddenException(InvalidPasswordException ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/403");
        modelAndView.addObject("error", "Ошибка запроса");
        modelAndView.addObject("message", ex.getMessage());
        log.warn("Bad Request with invalid data. {}", ex.getMessage(), ex);
        return modelAndView;
    }
}
