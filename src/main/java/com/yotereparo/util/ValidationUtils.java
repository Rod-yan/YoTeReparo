package com.yotereparo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.yotereparo.model.User;

public class ValidationUtils {
	
	public static BindingResult userInputValidation(User user, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		for (ConstraintViolation<User> violation : violations)
	    {
	        String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("user", propertyPath, message));
	    }
		
		return result;
	}
	
	public static List<CustomRequestError> getFormatedRequestErrorList(BindingResult result) {
	    List<CustomRequestError> errors = new ArrayList<>(result.getErrorCount());
	    for (FieldError fieldWithError : result.getFieldErrors()) {
	        errors.add(new CustomRequestError(fieldWithError.getObjectName(), fieldWithError.getField(), fieldWithError.getDefaultMessage()));
	    }
	    return errors;
	}
	
}
