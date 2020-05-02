package com.yotereparo.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = FieldsNullityMatchValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsNullityMatch {
	String message();
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
	 
    String nullField();
 
    String fieldNullityMatch();
 
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldsNullityMatch[] value();
    }
}
