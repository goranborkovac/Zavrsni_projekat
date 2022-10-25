package com.example.spring_demo.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException exception, ServletWebRequest request) {

    ErrorDetails errorDetails = new ErrorDetails(
            HttpStatus.BAD_REQUEST,
            exception.getMessage(),
            ZonedDateTime.now(),
            request.getDescription(true));

    return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorDetails> UserAlreadyExistsExceptionHandler(UserAlreadyExistsException exception, ServletWebRequest request) {

    ErrorDetails errorDetails = new ErrorDetails(
            HttpStatus.NOT_FOUND,
            exception.getMessage(),
            ZonedDateTime.now(),
            request.getDescription(true));

    return new ResponseEntity(errorDetails, HttpStatus.CONFLICT);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {

    final String errors = exception.getBindingResult().getFieldErrors().stream()
            .map(error -> String.join(" : ", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining("; "));

    ErrorDetails errorDetails = new ErrorDetails(
            HttpStatus.BAD_REQUEST,
            errors,
            ZonedDateTime.now(),
            request.getDescription(true));

    return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
  }

}
