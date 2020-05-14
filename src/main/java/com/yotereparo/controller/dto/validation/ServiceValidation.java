package com.yotereparo.controller.dto.validation;

import java.util.Locale;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.model.PaymentMethod;
import com.yotereparo.model.Requirement;
import com.yotereparo.service.PaymentMethodService;
import com.yotereparo.service.RequirementService;
import com.yotereparo.service.ServiceTypeService;
import com.yotereparo.service.UserService;

/**
 * Responsable de efectuar la validación de reglas de integridad de datos y reglas de negocio
 * para los requests recibidos para DTOs de Servicio.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class ServiceValidation {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceValidation.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ServiceTypeService serviceTypeService;
	@Autowired
	private PaymentMethodService paymentMethodService;
	@Autowired
	private RequirementService requirementService;
	
	public BindingResult validateRequest(ServiceDto serviceDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<ServiceDto> violation : validator.validate(serviceDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("Service", propertyPath, message));
	        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
	        		"Service", propertyPath, message);
	    }
		
		if (serviceDto.getUsuarioPrestador() != null && !serviceDto.getUsuarioPrestador().isEmpty())
			// Validamos existencia del usuario prestador
			if (userService.getUserById(serviceDto.getUsuarioPrestador().toLowerCase()) == null) {
				result.addError(new FieldError("User","usuarioPrestador",
						messageSource.getMessage("user.doesnt.exist", 
								new String[]{serviceDto.getUsuarioPrestador()}, Locale.getDefault())));
				logger.debug("Validation error in entity <{}>, entity does not exist.","User");
			}
		
		if (serviceDto.getTipoServicio() != null && !serviceDto.getTipoServicio().isEmpty())
			// Validamos existencia del tipo de servicio
			if (serviceTypeService.getServiceTypeByDescription(serviceDto.getTipoServicio()) == null) {
				result.addError(new FieldError("ServiceType","tipoServicio",
						messageSource.getMessage("serviceType.doesnt.exist", 
								new String[] {serviceDto.getTipoServicio()}, Locale.getDefault())));
				logger.debug("Validation error in entity <{}>, entity does not exist.","ServiceType");
			}
		
		for (PaymentMethod paymentMethod : serviceDto.getMediosDePago()) {
			for (ConstraintViolation<PaymentMethod> violation : validator.validate(paymentMethod)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("PaymentMethod", propertyPath, message));
		        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
        				"PaymentMethod", propertyPath, message);
		    }
			if (paymentMethod.getId() != null)
				// Validamos existencia del medio de pago
				if (paymentMethodService.getPaymentMethodById(paymentMethod.getId()) == null) {
					result.addError(new FieldError("PaymentMethod","mediosDePago",
							messageSource.getMessage("paymentMethod.doesnt.exist", 
									new Integer[] {paymentMethod.getId()}, Locale.getDefault())));
					logger.debug("Validation error in entity <{}>, entity does not exist.","PaymentMethod");
				}
		}
		
		for (Requirement requirement : serviceDto.getRequerimientos()) {
			for (ConstraintViolation<Requirement> violation : validator.validate(requirement)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Requirement", propertyPath, message));
		        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
        				"Requirement", propertyPath, message);
		    }
			if (requirement.getId() != null)
				// Validamos existencia del requerimiento
				if (requirementService.getRequirementById(requirement.getId()) == null) {
					result.addError(new FieldError("Requirement","requerimientos",
							messageSource.getMessage("requirement.doesnt.exist", 
									new Integer[] {requirement.getId()}, Locale.getDefault())));
					logger.debug("Validation error in entity <{}>, entity does not exist.","Requirement");
				}
		}
			
		return result;
	}
}
