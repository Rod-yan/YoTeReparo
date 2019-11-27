package com.yotereparo.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.json.JSONObject;

import com.yotereparo.util.error.CustomResponseError;

/**
 * Clase utilitaria para métodos accesorios de uso no específico. 
 * 
 * @author Rodrigo Yanis
 * 
 */
public class MiscUtils {
	public static List<JSONObject> getFormatedResponseErrorList(BindingResult result) {
	    List<JSONObject> formatedErrors = new ArrayList<>(result.getErrorCount());
	    for (FieldError fieldWithError : result.getFieldErrors()) {
	        CustomResponseError error = new CustomResponseError(fieldWithError.getObjectName(), fieldWithError.getField(), fieldWithError.getDefaultMessage());
	        formatedErrors.add(getFormatedResponseError(error));
	    }
	    return formatedErrors;
	}
	
	public static JSONObject getFormatedResponseError(CustomResponseError error) {
		JSONObject formatedError = new JSONObject();
		formatedError.put("field", error.getField());
		formatedError.put("defaultMessage", error.getDefaultMessage());
		formatedError.put("objectName", error.getObjectName());
		
		return formatedError;
	}
	
	public static JSONObject getFormatedResponseError(FieldError error) {
		JSONObject formatedError = new JSONObject();
		formatedError.put("field", error.getField());
		formatedError.put("defaultMessage", error.getDefaultMessage());
		formatedError.put("objectName", error.getObjectName());
		
		return formatedError;
	}
}
