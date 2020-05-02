package com.yotereparo.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class FieldsNullityMatchValidator implements ConstraintValidator<FieldsNullityMatch, Object> {
	private String nullField;
    private String fieldNullityMatch;
 
    public void initialize(FieldsNullityMatch constraintAnnotation) {
        this.nullField = constraintAnnotation.nullField();
        this.fieldNullityMatch = constraintAnnotation.fieldNullityMatch();
    }
 
    public boolean isValid(Object value,
      ConstraintValidatorContext context) {
 
        Object nullFieldValue = new BeanWrapperImpl(value)
          .getPropertyValue(nullField);
        Object fieldNullityMatchValue = new BeanWrapperImpl(value)
          .getPropertyValue(fieldNullityMatch);
        
        // Lógica de validación.
        if (nullFieldValue == null)
        	if (fieldNullityMatchValue != null) {
        		context.disableDefaultConstraintViolation();
        		context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        			.addPropertyNode(nullField)
        			.addConstraintViolation();
        		return false;
        	}
        return true;
    }
}
