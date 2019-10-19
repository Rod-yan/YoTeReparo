package com.yotereparo.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Clase utilitaria para métodos accesorios de uso no específico. 
 * 
 * @author Rodrigo Yanis
 * 
 */
public class MiscUtils {
	public static List<CustomResponseError> getFormatedRequestErrorList(BindingResult result) {
	    List<CustomResponseError> errors = new ArrayList<>(result.getErrorCount());
	    for (FieldError fieldWithError : result.getFieldErrors()) {
	        errors.add(new CustomResponseError(fieldWithError.getObjectName(), fieldWithError.getField(), fieldWithError.getDefaultMessage()));
	    }
	    return errors;
	}
}
