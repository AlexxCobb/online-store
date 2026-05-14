package ru.zinovev.online.store.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.zinovev.online.store.exception.model.BadRequestException;
import ru.zinovev.online.store.exception.model.InvalidPasswordException;
import ru.zinovev.online.store.exception.model.NotFoundException;
import ru.zinovev.online.store.exception.model.UserNotAuthenticatedException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleInternalServerError(HttpServletRequest request, Exception ex) {
        log.error("Something unexpected happened. Please try again later. {}", ex.getMessage(), ex);
        return buildErrorView(
                request,
                "error/error",
                "Внутренняя ошибка",
                "Что-то пошло не так. Попробуйте позже"
        );
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ModelAndView handleUserNotAuthenticatedException(HttpServletRequest request,
                                                            UserNotAuthenticatedException ex) {
        log.warn("Unauthorized access: {}", ex.getMessage(), ex);
        return buildErrorView(
                request,
                "error/401",
                "Требуется авторизация",
                ex.getMessage()
        );
    }

    @ExceptionHandler
    public ModelAndView handleNotFoundException(HttpServletRequest request, NotFoundException ex) {
        log.warn("The requested resource was not found. {}", ex.getMessage(), ex);
        return buildErrorView(
                request,
                "error/404",
                "Ресурс не найден",
                ex.getMessage()
        );
    }

    @ExceptionHandler
    public ModelAndView handleBadRequestException(HttpServletRequest request, BadRequestException ex) {
        log.warn("Bad Request with invalid data. {}", ex.getMessage(), ex);
        return buildErrorView(
                request,
                "error/400",
                "Некорректный запрос",
                ex.getMessage()
        );
    }

    @ExceptionHandler
    public ModelAndView handleForbiddenException(HttpServletRequest request, InvalidPasswordException ex) {
        log.warn("Access forbidden. {}", ex.getMessage(), ex);
        return buildErrorView(
                request,
                "error/403",
                "Доступ запрещён",
                ex.getMessage()
        );
    }

    private ModelAndView buildErrorView(HttpServletRequest request, String viewName, String error, String message) {
        var modelAndView = new ModelAndView(viewName);

        var referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            modelAndView.addObject("previousUrl", referer);
        }
        modelAndView.addObject("error", error);
        modelAndView.addObject("message", message);
        return modelAndView;
    }
}
