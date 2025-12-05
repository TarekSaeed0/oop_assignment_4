package com.github.oop_assignment_4.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import com.github.oop_assignment_4.model.User;
import com.github.oop_assignment_4.dto.SigninRequest;
import com.github.oop_assignment_4.dto.SignupRequest;
import com.github.oop_assignment_4.dto.UserDTO;
import com.github.oop_assignment_4.exception.UserAlreadyExistsException;
import com.github.oop_assignment_4.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import com.github.oop_assignment_4.mapper.UserMapper;

@Service
public class AuthenticationService {
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
	private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
			.getContextHolderStrategy();

	public AuthenticationService(AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder, UserRepository userRepository,
			UserMapper userMapper) {
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	public UserDTO signup(SignupRequest signupRequest) {
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			throw new UserAlreadyExistsException(
					"User with email " + signupRequest.getEmail() + " already exists");
		}

		User user = User.builder().email(signupRequest.getEmail())
				.password(passwordEncoder.encode(signupRequest.getPassword()))
				.name(signupRequest.getName()).build();

		return userMapper.toDTO(userRepository.save(user));
	}

	public void signin(SigninRequest signinRequest, HttpServletRequest request,
			HttpServletResponse response) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),
				signinRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);

		SecurityContext context = securityContextHolderStrategy.createEmptyContext();
		context.setAuthentication(authentication);
		securityContextHolderStrategy.setContext(context);
		securityContextRepository.saveContext(context, request, response);
	}

	public UserDTO me(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return userMapper.toDTO(user);
	}
}
