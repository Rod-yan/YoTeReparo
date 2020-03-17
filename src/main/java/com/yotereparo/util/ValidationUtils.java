package com.yotereparo.util;

import java.util.Locale;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.Address;
import com.yotereparo.model.District;
import com.yotereparo.model.PaymentMethod;
import com.yotereparo.model.Requirement;
import com.yotereparo.model.Service;
import com.yotereparo.service.CityService;
import com.yotereparo.service.DistrictService;
import com.yotereparo.service.PaymentMethodService;
import com.yotereparo.service.RequirementService;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.service.ServiceTypeService;
import com.yotereparo.service.UserService;

/**
 * Clase utilitaria para métodos accesorios cuyo objetivo esté vinculado con la validación de entidades. 
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class ValidationUtils {
	
	private static final Logger logger = LogManager.getLogger(ValidationUtils.class);

	@Autowired
	UserService userService;
	@Autowired
	ServiceManager serviceManager;
	@Autowired
	CityService cityService;
	@Autowired
	DistrictService districtService;
	@Autowired
	ServiceTypeService serviceTypeService;
	@Autowired
	PaymentMethodService paymentMethodService;
	@Autowired
	RequirementService requirementService;
	@Autowired
    MessageSource messageSource;
	
	public BindingResult userInputValidation(UserDto userDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<UserDto> violation : validator.validate(userDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("User", propertyPath, message));
	        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
	        				"User", propertyPath, message));
	    }
		
		if (userDto.getCiudad() != null && !userDto.getCiudad().isEmpty())
			// Validamos existencia de la ciudad
			if (cityService.getCityById(userDto.getCiudad()) == null) {
				result.addError(new FieldError("City","ciudad",messageSource.getMessage("city.doesnt.exist", null, Locale.getDefault())));
				logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","City"));
			}
		
		for (Address address : userDto.getDirecciones())
			for (ConstraintViolation<Address> violation : validator.validate(address)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Address", propertyPath, message));
		        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
        				"Address", propertyPath, message));
		    }
		
		if (userDto.getServicios() != null && !userDto.getServicios().isEmpty()) 
			for (ServiceDto serviceDto : userDto.getServicios()) {
				for (ConstraintViolation<ServiceDto> violation : validator.validate(serviceDto)) {
			        String propertyPath = violation.getPropertyPath().toString();
			        String message = violation.getMessage();
			        result.addError(new FieldError("Service", propertyPath, message));
			        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
	        				"Service", propertyPath, message));
			    }
				if (serviceDto.getId() != null) {
					Service service = serviceManager.getServiceById(serviceDto.getId());
					// Validamos existencia del servicio
					if (service == null) {
						result.addError(new FieldError("Service","servicios",messageSource.getMessage("service.doesnt.exist", new Integer[]{serviceDto.getId()}, Locale.getDefault())));
						logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","Service"));
					}
					else
						// Validamos que el servicio le pertenezca al usuario siendo validado
						if (!service.getUsuarioPrestador().getId().equals(userDto.getId())) {
							result.addError(new FieldError("Service","servicios",messageSource.getMessage("service.doesnt.belong.to.user", new Integer[]{service.getId()}, Locale.getDefault())));
							logger.debug(String.format("Validation error in entity <%s>, service does not belong to current user.","Service"));
						}
				}
			}
		
		if (userDto.getMembresia() != null && !userDto.getMembresia().isEmpty())
			if (userDto.getBarrios() != null && !userDto.getBarrios().isEmpty())
				for (District district : userDto.getBarrios()) {
					for (ConstraintViolation<District> violation : validator.validate(district)) {
				        String propertyPath = violation.getPropertyPath().toString();
				        String message = violation.getMessage();
				        result.addError(new FieldError("District", propertyPath, message));
				        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
		        				"District", propertyPath, message));
				    }
					// Validamos existencia del barrio
					if (district.getId() != null)
						if (districtService.getDistrictById(district.getId()) == null) {
							result.addError(new FieldError("District","barrios",messageSource.getMessage("district.doesnt.exist", null, Locale.getDefault())));
							logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","District"));
						}
				}
		
		return result;
	}
	
	public BindingResult serviceInputValidation(ServiceDto serviceDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<ServiceDto> violation : validator.validate(serviceDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("Service", propertyPath, message));
	        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
	        				"Service", propertyPath, message));
	    }
		
		if (serviceDto.getUsuarioPrestador() != null && !serviceDto.getUsuarioPrestador().isEmpty())
			// Validamos existencia del usuario prestador
			if (userService.getUserById(serviceDto.getUsuarioPrestador()) == null) {
				result.addError(new FieldError("User","usuarioPrestador",messageSource.getMessage("user.doesnt.exist", new String[]{serviceDto.getUsuarioPrestador()}, Locale.getDefault())));
				logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","User"));
			}
		
		if (serviceDto.getTipoServicio() != null && !serviceDto.getTipoServicio().isEmpty())
			// Validamos existencia del tipo de servicio
			if (serviceTypeService.getServiceTypeByDescription(serviceDto.getTipoServicio()) == null) {
				result.addError(new FieldError("ServiceType","tipoServicio",messageSource.getMessage("serviceType.doesnt.exist", null, Locale.getDefault())));
				logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","ServiceType"));
			}
		
		for (PaymentMethod paymentMethod : serviceDto.getMediosDePago()) {
			for (ConstraintViolation<PaymentMethod> violation : validator.validate(paymentMethod)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("PaymentMethod", propertyPath, message));
		        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
        				"PaymentMethod", propertyPath, message));
		    }
			if (paymentMethod.getId() != null)
				// Validamos existencia del medio de pago
				if (paymentMethodService.getPaymentMethodById(paymentMethod.getId()) == null) {
					result.addError(new FieldError("PaymentMethod","mediosDePago",messageSource.getMessage("paymentMethod.doesnt.exist", null, Locale.getDefault())));
					logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","PaymentMethod"));
				}
		}
		
		for (Requirement requirement : serviceDto.getRequerimientos()) {
			for (ConstraintViolation<Requirement> violation : validator.validate(requirement)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Requirement", propertyPath, message));
		        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
        				"Requirement", propertyPath, message));
		    }
			if (requirement.getId() != null)
				// Validamos existencia del requerimiento
				if (requirementService.getRequirementById(requirement.getId()) == null) {
					result.addError(new FieldError("Requirement","requerimientos",messageSource.getMessage("requirement.doesnt.exist", null, Locale.getDefault())));
					logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","Requirement"));
				}
		}
		return result;
	}
}
