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

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.controller.dto.UserPasswordChangeDto;
import com.yotereparo.model.Address;
import com.yotereparo.model.District;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.service.CityService;
import com.yotereparo.service.DistrictService;
import com.yotereparo.service.QuoteService;
import com.yotereparo.service.ServiceManager;

/**
 * Responsable de efectuar la validación de reglas de integridad de datos y reglas de negocio
 * para los requests recibidos para DTOs de Usuario.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class UserValidation {
	
	private static final Logger logger = LoggerFactory.getLogger(UserValidation.class);
	
	@Autowired
	private CityService cityService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ServiceManager serviceManager;
	@Autowired
	private QuoteService quoteService;
	@Autowired
	private DistrictService districtService;
	
	public BindingResult validateRequest(UserDto userDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<UserDto> violation : validator.validate(userDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("User", propertyPath, message));
	        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
	        		"User", propertyPath, message);
	    }
		
		if (userDto.getCiudad() != null && !userDto.getCiudad().isEmpty())
			// Validamos existencia de la ciudad
			if (cityService.getCityById(userDto.getCiudad()) == null) {
				result.addError(new FieldError("City","ciudad",
						messageSource.getMessage("city.doesnt.exist", 
								new String[] {userDto.getCiudad()}, Locale.getDefault())));
				logger.debug("Validation error in entity <{}>, entity does not exist.","City");
			}
		
		for (Address address : userDto.getDirecciones())
			for (ConstraintViolation<Address> violation : validator.validate(address)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Address", propertyPath, message));
		        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
        				"Address", propertyPath, message);
		    }
		
		
		for (ServiceDto serviceDto : userDto.getServicios()) {
			for (ConstraintViolation<ServiceDto> violation : validator.validate(serviceDto)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Service", propertyPath, message));
		        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
        				"Service", propertyPath, message);
		    }
			if (serviceDto.getId() != null) {
				Service service = serviceManager.getServiceById(serviceDto.getId());
				// Validamos existencia del servicio
				if (service == null) {
					result.addError(new FieldError("Service","servicios",
							messageSource.getMessage("service.doesnt.exist", 
									new Integer[]{serviceDto.getId()}, Locale.getDefault())));
					logger.debug("Validation error in entity <{}>, entity does not exist.","Service");
				}
				else
					// Validamos que el servicio le pertenezca al usuario siendo validado
					if (!service.getUsuarioPrestador().getId().equals(userDto.getId())) {
						result.addError(new FieldError("Service","servicios",
								messageSource.getMessage("service.doesnt.belong.to.user", 
										new Integer[]{service.getId()}, Locale.getDefault())));
						logger.debug("Validation error in entity <{}>, service does not belong to current user.","Service");
					}
			}
		}
		
		for (QuoteDto quoteDto : userDto.getPresupuestos()) {
			for (ConstraintViolation<QuoteDto> violation : validator.validate(quoteDto)) {
		        String propertyPath = violation.getPropertyPath().toString();
		        String message = violation.getMessage();
		        result.addError(new FieldError("Quote", propertyPath, message));
		        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
        				"Quote", propertyPath, message);
		    }
			if (quoteDto.getId() != null) {
				Quote quote = quoteService.getQuoteById(quoteDto.getId());
				// Validamos existencia del presupuesto
				if (quote == null) {
					result.addError(new FieldError("Quote","presupuestos",
							messageSource.getMessage("quote.doesnt.exist", 
									new Integer[] {quoteDto.getId()}, Locale.getDefault())));
					logger.debug("Validation error in entity <{}>, entity does not exist.","Quote");
				}
				else
					// Validamos que el presupuesto le pertenezca al usuario siendo validado
					if (!quote.getUsuarioFinal().getId().equals(userDto.getId())) {
						result.addError(new FieldError("Quote","presupuestos",
								messageSource.getMessage("quote.doesnt.belong.to.user", 
										new Integer[]{quote.getId()}, Locale.getDefault())));
						logger.debug("Validation error in entity <{}>, quote does not belong to current user.","Quote");
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
				        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
		        				"District", propertyPath, message);
				    }
					// Validamos existencia del barrio
					if (district.getId() != null)
						if (districtService.getDistrictById(district.getId()) == null) {
							result.addError(new FieldError("District","barrios",
									messageSource.getMessage("district.doesnt.exist", 
											new Integer[] {district.getId()}, Locale.getDefault())));
							logger.debug("Validation error in entity <{}>, entity does not exist.","District");
						}
				}
		
		return result;
	}
	
	public BindingResult validateRequest(UserPasswordChangeDto userPasswordChangeDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<UserPasswordChangeDto> violation : validator.validate(userPasswordChangeDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("User", propertyPath, message));
	        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
	        		"User", propertyPath, message);
	    }
		
		return result;
	}
}
