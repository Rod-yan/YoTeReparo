package com.yotereparo.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.Address;
import com.yotereparo.model.District;
import com.yotereparo.model.PaymentMethod;

/**
 * Clase utilitaria para métodos accesorios cuyo objetivo esté vinculado con la validación de entidades. 
 * 
 * @author Rodrigo Yanis
 * 
 */
public class ValidationUtils {
	
	private static final Logger logger = LogManager.getLogger(ValidationUtils.class);
	
	public static BindingResult userInputValidation(UserDto userDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<UserDto> violation : validator.validate(userDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("User", propertyPath, message));
	        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
	        				"User", propertyPath, message));
	    }
		
		for (Address address : userDto.getDirecciones())
			for (ConstraintViolation<Address> violation : validator.validate(address)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Address", propertyPath, message));
		        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
        				"Address", propertyPath, message));
		    }
		
		for (ServiceDto serviceDto : userDto.getServicios())
			for (ConstraintViolation<ServiceDto> violation : validator.validate(serviceDto)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Service", propertyPath, message));
		        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
        				"Service", propertyPath, message));
		    }
		
		if (userDto.getMembresia() != null)
			if (userDto.getBarrios() != null)
				for (District district : userDto.getBarrios()) 
					for (ConstraintViolation<District> violation : validator.validate(district)) {
				        String propertyPath = violation.getPropertyPath().toString();
				        String message = violation.getMessage();
				        result.addError(new FieldError("District", propertyPath, message));
				        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
		        				"District", propertyPath, message));
				    }
		
		return result;
	}
	
	public static BindingResult serviceInputValidation(ServiceDto serviceDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<ServiceDto> violation : validator.validate(serviceDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("Service", propertyPath, message));
	        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
	        				"Service", propertyPath, message));
	    }
		
		for (PaymentMethod paymentMethod : serviceDto.getMediosDePago())
			for (ConstraintViolation<PaymentMethod> violation : validator.validate(paymentMethod)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("PaymentMethod", propertyPath, message));
		        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
        				"PaymentMethod", propertyPath, message));
		    }
		
		return result;
	}
}
