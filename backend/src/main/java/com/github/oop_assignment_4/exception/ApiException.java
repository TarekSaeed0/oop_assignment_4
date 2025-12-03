package com.github.oop_assignment_4.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {
	private final String type;
	private final HttpStatus status;

	protected ApiException(String type, String message, HttpStatus status) {
		super(message);
		this.type = type;
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public HttpStatus getStatus() {
		return status;
	}
}

