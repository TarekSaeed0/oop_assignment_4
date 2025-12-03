package com.github.oop_assignment_4.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ApiException {
	public UserAlreadyExistsException() {
		super("USER_ALREADY_EXISTS", "User already exists", HttpStatus.CONFLICT);
	}

	public UserAlreadyExistsException(String email) {
		super("USER_ALREADY_EXISTS", "User with email " + email + " already exists",
				HttpStatus.CONFLICT);
	}
}
