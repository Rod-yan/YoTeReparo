package com.yotereparo.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.joda.time.DateTime;
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
        
        Class<?> lowerValueType = new BeanWrapperImpl(value)
  	          .getPropertyType(valueOf);
        Class<?> upperValueType = new BeanWrapperImpl(value)
      		  .getPropertyType(greaterThanValueOf);
        
        // Lógica de validación.
        if (lowerValue != null && upperValue != null && lowerValueType.equals(upperValueType)) {
        	if (lowerValueType.getSimpleName().equals("Float"))
		        if ((Float) lowerValue < (Float) upperValue) {
		        	context.disableDefaultConstraintViolation();
		    		context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
		    			.addPropertyNode(valueOf)
		    			.addConstraintViolation();
		    		return false;
		        }
        	if (lowerValueType.getSimpleName().equals("DateTime")) {
        		DateTime lowerValueDate = new DateTime(lowerValue);
        		DateTime upperValueDate = new DateTime(upperValue);
        		if (lowerValueDate.isBefore(upperValueDate)) {
		        	context.disableDefaultConstraintViolation();
		    		context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
		    			.addPropertyNode(valueOf)
		    			.addConstraintViolation();
		    		return false;
		        }
        	}
        }
        return true;
    }
}
