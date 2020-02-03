package com.yotereparo.util.customvalidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Object> {
	private String field;
 
    public void initialize(GreaterThan constraintAnnotation) {
        this.field = constraintAnnotation.field();
    }
 
    public boolean isValid(Object value,
      ConstraintValidatorContext context) {
    	if ( value == null ) {
            return true;
        }
 
        Object fieldValue = new BeanWrapperImpl(value)
          .getPropertyValue(field);
        
        // Lógica de validación.
        if ((float) value >= (float) fieldValue) 
        	return true;
        else
        	return false;
    }
}
