package com.yotereparo.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = GreaterThanValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GreaterThan {
	String message();
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
	 
    String valueOf();
    
    String greaterThanValueOf();
 
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        GreaterThan[] value();
    }
}
