package com.yotereparo.util.customvalidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Object> {
	private String valueOf;
	private String greaterThanValueOf;
 
    public void initialize(GreaterThan constraintAnnotation) {
    	this.valueOf = constraintAnnotation.valueOf();
        this.greaterThanValueOf = constraintAnnotation.greaterThanValueOf();
    }
 
    public boolean isValid(Object value,
      ConstraintValidatorContext context) {
    	Object lowerValue = new BeanWrapperImpl(value)
    	          .getPropertyValue(valueOf);
        Object upperValue = new BeanWrapperImpl(value)
        		  .getPropertyValue(greaterThanValueOf);
        
        // Lógica de validación.
        if (lowerValue != null && upperValue != null)
	        if ((Float) lowerValue < (Float) upperValue) {
	        	context.disableDefaultConstraintViolation();
	    		context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
	    			.addPropertyNode(valueOf)
	    			.addConstraintViolation();
	    		return false;
	        }
        return true;
    }
}
