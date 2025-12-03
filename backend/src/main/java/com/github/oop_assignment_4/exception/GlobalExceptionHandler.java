package com.github.oop_assignment_4.exception;

import java.time.Instant;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.github.oop_assignment_4.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log =
			LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiErrorResponse handleValidationExceptions(
			MethodArgumentNotValidException ex, HttpServletRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining("; "));

		ApiErrorResponse response = new ApiErrorResponse("VALIDATION_ERROR",
				message, HttpStatus.BAD_REQUEST.value(), request.getRequestURI(),
				Instant.now());

		return response;
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex,
			HttpServletRequest request) {
		ApiErrorResponse response =
				new ApiErrorResponse(ex.getType(), ex.getMessage(),
						ex.getStatus().value(), request.getRequestURI(), Instant.now());

		return new ResponseEntity<>(response, ex.getStatus());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
			AuthenticationException ex, HttpServletRequest request) {
		ApiErrorResponse response = new ApiErrorResponse("AUTHENTICATION_FAILED",
				ex.getMessage(), HttpStatus.UNAUTHORIZED.value(),
				request.getRequestURI(), Instant.now());

		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
			AccessDeniedException ex, HttpServletRequest request) {
		ApiErrorResponse response = new ApiErrorResponse("ACCESS_DENIED",
				ex.getMessage(), HttpStatus.FORBIDDEN.value(), request.getRequestURI(),
				Instant.now());

		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex,
			HttpServletRequest request) {
		log.error("Unhandled exception caught: {}", ex.toString(), ex);

		ApiErrorResponse response = new ApiErrorResponse("INTERNAL_SERVER_ERROR",
				ex.getMessage() == null ? "An unexpected error occurred"
						: ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI(),
				Instant.now());

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
