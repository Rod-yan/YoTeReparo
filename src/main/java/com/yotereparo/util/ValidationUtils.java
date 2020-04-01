package com.yotereparo.util;

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

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.controller.dto.UserPasswordChangeDto;
import com.yotereparo.model.Address;
import com.yotereparo.model.District;
import com.yotereparo.model.PaymentMethod;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Requirement;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.CityService;
import com.yotereparo.service.DistrictService;
import com.yotereparo.service.PaymentMethodService;
import com.yotereparo.service.QuoteService;
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
	
	private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

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
	QuoteService quoteService;
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
				result.addError(new FieldError("City","ciudad",messageSource.getMessage("city.doesnt.exist", new String[] {userDto.getCiudad()}, Locale.getDefault())));
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
		
		for (QuoteDto quoteDto : userDto.getPresupuestos()) {
			for (ConstraintViolation<QuoteDto> violation : validator.validate(quoteDto)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Quote", propertyPath, message));
		        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
        				"Quote", propertyPath, message));
		    }
			if (quoteDto.getId() != null) {
				Quote quote = quoteService.getQuoteById(quoteDto.getId());
				// Validamos existencia del presupuesto
				if (quote == null) {
					result.addError(new FieldError("Quote","presupuestos",messageSource.getMessage("quote.doesnt.exist", new Integer[] {quoteDto.getId()}, Locale.getDefault())));
					logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","Quote"));
				}
				else
					// Validamos que el presupuesto le pertenezca al usuario siendo validado
					if (!quote.getUsuarioFinal().getId().equals(userDto.getId())) {
						result.addError(new FieldError("Quote","presupuestos",messageSource.getMessage("quote.doesnt.belong.to.user", new Integer[]{quote.getId()}, Locale.getDefault())));
						logger.debug(String.format("Validation error in entity <%s>, quote does not belong to current user.","Quote"));
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
							result.addError(new FieldError("District","barrios",messageSource.getMessage("district.doesnt.exist", new Integer[] {district.getId()}, Locale.getDefault())));
							logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","District"));
						}
				}
		
		return result;
	}
	
	public BindingResult userPasswordChangeInputValidation(UserPasswordChangeDto userPasswordChangeDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<UserPasswordChangeDto> violation : validator.validate(userPasswordChangeDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("User", propertyPath, message));
	        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
	        				"User", propertyPath, message));
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
			if (userService.getUserById(serviceDto.getUsuarioPrestador().toLowerCase()) == null) {
				result.addError(new FieldError("User","usuarioPrestador",messageSource.getMessage("user.doesnt.exist", new String[]{serviceDto.getUsuarioPrestador()}, Locale.getDefault())));
				logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","User"));
			}
		
		if (serviceDto.getTipoServicio() != null && !serviceDto.getTipoServicio().isEmpty())
			// Validamos existencia del tipo de servicio
			if (serviceTypeService.getServiceTypeByDescription(serviceDto.getTipoServicio()) == null) {
				result.addError(new FieldError("ServiceType","tipoServicio",messageSource.getMessage("serviceType.doesnt.exist", new String[] {serviceDto.getTipoServicio()}, Locale.getDefault())));
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
					result.addError(new FieldError("PaymentMethod","mediosDePago",messageSource.getMessage("paymentMethod.doesnt.exist", new Integer[] {paymentMethod.getId()}, Locale.getDefault())));
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
					result.addError(new FieldError("Requirement","requerimientos",messageSource.getMessage("requirement.doesnt.exist", new Integer[] {requirement.getId()}, Locale.getDefault())));
					logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","Requirement"));
				}
		}
		
		for (QuoteDto quoteDto : serviceDto.getPresupuestos()) {
			for (ConstraintViolation<QuoteDto> violation : validator.validate(quoteDto)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Quote", propertyPath, message));
		        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
        				"Quote", propertyPath, message));
		    }
			if (quoteDto.getId() != null) {
				Quote quote = quoteService.getQuoteById(quoteDto.getId());
				// Validamos existencia del presupuesto
				if (quote == null) {
					result.addError(new FieldError("Quote","presupuestos",messageSource.getMessage("quote.doesnt.exist", new Integer[]{quoteDto.getId()}, Locale.getDefault())));
					logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","Quote"));
				}
				else
					// Validamos que el presupuesto le pertenezca al servicio siendo validado
					if (!quote.getServicio().getId().equals(serviceDto.getId())) {
						result.addError(new FieldError("Quote","presupuestos",messageSource.getMessage("quote.doesnt.belong.to.service", new Integer[]{quote.getId()}, Locale.getDefault())));
						logger.debug(String.format("Validation error in entity <%s>, quote does not belong to current service.","Quote"));
					}
			}
		}
			
		return result;
	}
	
	public BindingResult quoteInputValidation(QuoteDto quoteDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<QuoteDto> violation : validator.validate(quoteDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("Quote", propertyPath, message));
	        logger.debug(String.format("Validation error in entity <%s>'s attribute <%s>, with message <%s>", 
	        				"Quote", propertyPath, message));
	    }
		
		User user = userService.getUserById(quoteDto.getUsuarioFinal().toLowerCase());
		// Validamos existencia del usuario
		if (user == null) {
			result.addError(new FieldError("User","usuarioFinal",messageSource.getMessage("user.doesnt.exist", new String[]{quoteDto.getUsuarioFinal()}, Locale.getDefault())));
			logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","User"));
		}
		
		Service service = serviceManager.getServiceById(quoteDto.getServicio());
		// Validamos existencia del servicio
		if (service == null) {
			result.addError(new FieldError("Service","servicio",messageSource.getMessage("service.doesnt.exist", new Integer[]{quoteDto.getServicio()}, Locale.getDefault())));
			logger.debug(String.format("Validation error in entity <%s>, entity does not exist.","Service"));
		}
		
		if (service != null && user != null)
			if (user.getId().equals(service.getUsuarioPrestador().getId())) {
				result.addError(new FieldError("Quote","usuarioFinal",messageSource.getMessage("quote.user.owns.service", new String[]{user.getId()}, Locale.getDefault())));
				logger.info(String.format("Validation error in entity <%s>, user owns the service for the processed quote.","Quote"));
			}
		
		return result;
	}
}
