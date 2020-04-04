package com.yotereparo.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yotereparo.util.error.CustomResponseError;

/**
 * Clase utilitaria para métodos accesorios de uso no específico. 
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class MiscUtils {
	
	public List<JsonNode> getFormatedResponseErrorList(BindingResult result) {
	    List<JsonNode> formatedErrors = new ArrayList<>(result.getErrorCount());
	    for (FieldError fieldWithError : result.getFieldErrors()) {
	        CustomResponseError error = new CustomResponseError(fieldWithError.getObjectName(), fieldWithError.getField(), fieldWithError.getDefaultMessage());
	        formatedErrors.add(getFormatedResponseError(error));
	    }
	    return formatedErrors;
	}
	
	public JsonNode getFormatedResponseError(FieldError error) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonError = objectMapper.createObjectNode();
		((ObjectNode)jsonError).put("field", error.getField());
		((ObjectNode)jsonError).put("defaultMessage", error.getDefaultMessage());
		((ObjectNode)jsonError).put("objectName", error.getObjectName());
		return jsonError;
	}
	
	public JsonNode getFormatedResponseError(CustomResponseError error) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonError = objectMapper.createObjectNode();
		((ObjectNode)jsonError).put("field", error.getField());
		((ObjectNode)jsonError).put("defaultMessage", error.getDefaultMessage());
		((ObjectNode)jsonError).put("objectName", error.getObjectName());
		return jsonError;
	}
}
