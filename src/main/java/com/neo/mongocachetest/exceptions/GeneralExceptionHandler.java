package com.neo.mongocachetest.exceptions;

import com.neo.mongocachetest.exceptions.httpError.BadRequestException;
import com.neo.mongocachetest.exceptions.httpError.UnAuthorizedException;
import com.neo.mongocachetest.exceptions.validation.ValidationErrorResponse;
import com.neo.mongocachetest.exceptions.validation.Violation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@RestControllerAdvice
public class GeneralExceptionHandler {

  @ExceptionHandler(UnAuthorizedException.class)
  public ErrorInfo handleLoginException(RuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return new ErrorInfo(UrlUtils.buildFullRequestUrl(request), ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  public ErrorInfo handleBadRequestException(RuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    return new ErrorInfo(UrlUtils.buildFullRequestUrl(request), ex.getMessage());
  }

  // -------- SPRING ---------

  @ExceptionHandler(AuthenticationException.class)
  public ErrorInfo handleAuthException(RuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    log.error("Wrong login or password!");
    return new ErrorInfo(UrlUtils.buildFullRequestUrl(request), ex.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
    final List<Violation> violations = e.getConstraintViolations().stream()
      .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
      .collect(Collectors.toList());
    return new ValidationErrorResponse(violations);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
      .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
      .collect(Collectors.toList());
    return new ValidationErrorResponse(violations);
  }
}

