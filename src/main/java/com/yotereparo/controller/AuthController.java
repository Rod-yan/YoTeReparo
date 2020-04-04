package com.yotereparo.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.User;
import com.yotereparo.security.jwt.JwtResponse;
import com.yotereparo.security.jwt.JwtUtils;
import com.yotereparo.security.jwt.LoginRequest;
import com.yotereparo.security.service.UserDetailsImpl;
import com.yotereparo.service.UserService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.SecurityUtils;

/**
 * Controlador REST SpringMVC que expone servicios de autenticación.
 * 
 * @author Rodrigo Yanis
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	UserController userController;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserService userService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	MiscUtils miscUtils;
	
	/*
	 * Autentica las credenciales recibidas en el request y solicita la gestión del JWT correspondiente
	 */
	@RequestMapping(
			value = { "/signin" },
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.POST)
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		logger.info(String.format("AuthenticateUser - POST - Processing request for user <%s>.", loginRequest.getUsername()));
		try {
			User user = userService.getUserById(loginRequest.getUsername());
			if (user != null) {
				if (user.getFechaExpiracionContrasena().isAfterNow()) {
					if (user.getEstado().equals(User.ACTIVE)) {
						try {
							String password = SecurityUtils.encryptPassword(loginRequest.getPassword().concat(user.getSalt()));
							
							Authentication authentication = authenticationManager.authenticate(
									new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), password));
							
							// Si el usuario se autenticó correctamente, registramos este evento.
							if (authentication.isAuthenticated())
								userService.registerSuccessfulLoginAttempt(user);
							
							SecurityContextHolder.getContext().setAuthentication(authentication);
							String jwt = jwtUtils.generateJwtToken(authentication);
							
							UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
							List<String> roles = userDetails.getAuthorities().stream()
									.map(item -> item.getAuthority())
									.collect(Collectors.toList());
							
							logger.info(String.format("AuthenticateUser - POST - Successful Authentication for user <%s>.", loginRequest.getUsername()));
							return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
						}
						catch (BadCredentialsException e) {
							// Si el usuario se autenticó incorrectamente, registramos este evento.
							userService.registerFailedLoginAttempt(user);
							
							logger.warn("AuthenticateUser - POST - Request failed - Bad credentials.");
							FieldError error = new FieldError("Auth","error",messageSource.getMessage("bad.credentials", null, Locale.getDefault()));
							return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.UNAUTHORIZED);
						}
					}
					else {
						logger.warn("AuthenticateUser - POST - Request failed - User is not an active user.");
						FieldError error = new FieldError("User","error",messageSource.getMessage("user.not.active", new String[]{loginRequest.getUsername()}, Locale.getDefault()));
						return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.UNAUTHORIZED);
					}
				}
				else {
					logger.warn("AuthenticateUser - POST - Request failed - Password is expired.");
					FieldError error = new FieldError("User","error",messageSource.getMessage("user.password.expired", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.UNAUTHORIZED);
				}
			}
			else {
				logger.warn(String.format("AuthenticateUser - POST - Request failed - User with id <%s> not found.", loginRequest.getUsername()));
				FieldError error = new FieldError("User","error",messageSource.getMessage("user.doesnt.exist", new String[]{loginRequest.getUsername()}, Locale.getDefault()));
				return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			logger.error("AuthenticateUser - POST - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Auth","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/*
	 * Proxy público para la creación de nuevos usuarios
	 */
	@RequestMapping(
			value = { "/signup" },
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.POST)
	public ResponseEntity<?> registerUser(@RequestBody UserDto clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {
		return userController.createUser(clientInput, ucBuilder, result);
	}
}