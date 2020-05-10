package com.yotereparo.controller.dto.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.yotereparo.controller.dto.ContractDto;

/**
 * Responsable de efectuar la validación de reglas de integridad de datos y reglas de negocio
 * para los requests recibidos para DTOs de Contrato.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class ContractValidation {
	
	private static final Logger logger = LoggerFactory.getLogger(ContractValidation.class);
	
	public BindingResult validateRequest(ContractDto contractDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<ContractDto> violation : validator.validate(contractDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("Contract", propertyPath, message));
	        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
	        		"Contract", propertyPath, message);
	    }
		
		return result;
	}
}
