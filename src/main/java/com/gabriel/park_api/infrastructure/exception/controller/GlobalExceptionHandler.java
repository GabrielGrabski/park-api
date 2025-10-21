package com.gabriel.park_api.infrastructure.exception.controller;

import com.gabriel.park_api.infrastructure.exception.dto.ErrorResponse;
import com.gabriel.park_api.infrastructure.exception.model.CustomerAlreadyExistsException;
import com.gabriel.park_api.infrastructure.exception.model.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static com.gabriel.park_api.infrastructure.exception.enums.ErrorCode.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ErrorResponse> handle(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .map(message -> new ErrorResponse(VALIDATION_ERROR.name(), message))
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomerNotFoundException.class)
    public List<ErrorResponse> handle(CustomerNotFoundException ex) {
        return List.of(new ErrorResponse(NOT_FOUND.name(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public List<ErrorResponse> handle(CustomerAlreadyExistsException ex) {
        return List.of(new ErrorResponse(ALREADY_EXISTENT_CONTENT.name(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public List<ErrorResponse> handle(HttpMessageNotReadableException ex) {
        return List.of(
                new ErrorResponse(UNKNOWN_ERROR.name(),
                        "Request is invalid, please double check and try again. If the error persists, please contact an admin. Error: " + ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<ErrorResponse> handle(Exception ex) {
        return List.of(new ErrorResponse(UNKNOWN_ERROR.name(),
                "An unexpected Error occurred, please contact an admin."));
    }
}
