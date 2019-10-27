package com.yotereparo.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.yotereparo.model.Address;
import com.yotereparo.model.User;

/**
 * Clase utilitaria para métodos accesorios cuyo objetivo esté vinculado con la validación de entidades. 
 * 
 * @author Rodrigo Yanis
 * 
 */
public class ValidationUtils {
	
	public static BindingResult userInputValidation(User user, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		for (ConstraintViolation<User> violation : validator.validate(user))
	    {
	        String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("User", propertyPath, message));
	    }
		
		for (Address direccion : user.getDirecciones()) {
			for (ConstraintViolation<Address> violation : validator.validate(direccion))
		    {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Address", propertyPath, message));
		    }
		}
		
		return result;
	}
}
