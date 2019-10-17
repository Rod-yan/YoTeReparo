package com.yotereparo.controller;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.yotereparo.model.User;
import com.yotereparo.service.UserService;
import com.yotereparo.util.CustomRequestError;
import com.yotereparo.util.ValidationUtils;

@RestController
public class UserController {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);
	
	@Autowired
    UserService userService;
	@Autowired
    MessageSource messageSource;
	
	@RequestMapping(value = { "/users/" }, method = RequestMethod.GET)
	public ResponseEntity<List<User>> listUsers() {
		
		logger.info("Fetching all users.");
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
        	logger.error("No users were found.");
        	return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }
	
	@RequestMapping(value = { "/users/{id}" }, method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("id") String id) {
		
		logger.info(String.format("Fetching User with id <%s>.", id));
        User user = userService.getUserById(id);
        if (user == null) {
            logger.error(String.format("User with id <%s> not found.", id));
            FieldError userExistenceError =new FieldError("user","id",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
            return new ResponseEntity<>(new CustomRequestError(userExistenceError.getObjectName(), 
            		userExistenceError.getField(), 
            		userExistenceError.getDefaultMessage()), 
            		HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
	
	@RequestMapping(value = { "/users/" }, method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {
		
		if (ValidationUtils.userInputValidation(clientInput, result).hasErrors()) {
			return new ResponseEntity<>(ValidationUtils.getFormatedRequestErrorList(result), HttpStatus.BAD_REQUEST);
		}
		
		if (userService.exist(clientInput.getId())) {
            logger.error(String.format("Unable to create user. User <%s> already exist.", clientInput.getId()));
            FieldError userExistenceError =new FieldError("user","id",messageSource.getMessage("user.already.exist", new String[]{clientInput.getId()}, Locale.getDefault()));
            result.addError(userExistenceError);
            return new ResponseEntity<>(ValidationUtils.getFormatedRequestErrorList(result), HttpStatus.CONFLICT);
        }		
				
		logger.info(String.format("Creating user with id <%s>.", clientInput.getId()));
		userService.createUser(clientInput);
		
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/yotereparo/user/{id}").buildAndExpand(clientInput.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = { "/users/{id}" }, method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @RequestBody User clientInput, BindingResult result) {
		
		clientInput.setId(id);
		
		if (!userService.exist(id)) {
            logger.error(String.format("Unable to update user. User <%s> doesn't exist.", id));
            FieldError userExistenceError =new FieldError("user","id",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
            result.addError(userExistenceError);
            return new ResponseEntity<>(ValidationUtils.getFormatedRequestErrorList(result), HttpStatus.NOT_FOUND);
        }
		
		if (ValidationUtils.userInputValidation(clientInput, result).hasErrors()) {
			return new ResponseEntity<>(ValidationUtils.getFormatedRequestErrorList(result), HttpStatus.BAD_REQUEST);
		}
		
		logger.info(String.format("Updating user with id <%s>.", id) );
		userService.updateUser(clientInput);
		
		return new ResponseEntity<User>(userService.getUserById(id), HttpStatus.OK);
    }
	
	@RequestMapping(value = { "/users/{id}" }, method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        
        if (!userService.exist(id)) {
        	logger.error(String.format("Unable to delete user. User <%s> doesn't exist.", id));
        	FieldError userExistenceError =new FieldError("user","id",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
            return new ResponseEntity<>(new CustomRequestError(userExistenceError.getObjectName(), 
            		userExistenceError.getField(), 
            		userExistenceError.getDefaultMessage()), 
            		HttpStatus.NOT_FOUND);
        }
        logger.info(String.format("Deleting user with id <%s>.", id));
        userService.deleteUserById(id);
        
        return new ResponseEntity<User>(HttpStatus.OK);
    }
}
