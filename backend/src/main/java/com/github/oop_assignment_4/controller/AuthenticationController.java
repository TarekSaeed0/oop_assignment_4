package com.github.oop_assignment_4.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.oop_assignment_4.dto.SigninRequest;
import com.github.oop_assignment_4.dto.SignupRequest;
import com.github.oop_assignment_4.dto.UserDTO;
import com.github.oop_assignment_4.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/signup")
	public ResponseEntity<UserDTO> signup(
			@Valid @RequestBody SignupRequest request) {
		return ResponseEntity.ok(authenticationService.signup(request));
	}

	@PostMapping({"/signin", "/login"})
	public ResponseEntity<UserDTO> signin(
			@Valid @RequestBody SigninRequest signinRequest,
			HttpServletRequest request, HttpServletResponse response) {
		return ResponseEntity.ok(authenticationService.signin(signinRequest, request, response));
	}

	@GetMapping("/me")
	public ResponseEntity<UserDTO> me(Authentication authentication) {
		return ResponseEntity.ok(authenticationService.me(authentication));
	}
}
