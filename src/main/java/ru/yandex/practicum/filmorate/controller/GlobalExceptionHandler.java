package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.model.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.exceptions.ValidationException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e, HttpServletRequest request) {
        log.debug("Custom validation FAILED: {} for {}", e.getMessage(), e.getObject());
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        Object target = e.getBindingResult().getTarget();
        log.debug("Spring validation FAILED: {} for {}", errorMessage, target);
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                errorMessage,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        log.debug("Not found: {} for {}", e.getMessage(), e.getObject());
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND,
                "Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(RuntimeException e, HttpServletRequest request) {
        log.debug("Illegal Argument: {}", e.getMessage());
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST,
                "Illegal Argument",
                e.getMessage(),
                request.getRequestURI()
        );
    }

//    @ExceptionHandler(RuntimeException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest request) {
//        log.debug("Internal Server Error: {}", e.getMessage());
//        return new ErrorResponse(
//                Instant.now(),
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                "Unexpected " + e.getClass().getSimpleName(),
//                e.getMessage(),
//                request.getRequestURI()
//        );
//    }

}
