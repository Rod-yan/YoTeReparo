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
import com.yotereparo.model.Address;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.service.UserService;

/**
 * Responsable de efectuar la validación de reglas de integridad de datos y reglas de negocio
 * para los requests recibidos para DTOs de Presupuesto.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class QuoteValidation {
	
	private static final Logger logger = LoggerFactory.getLogger(QuoteValidation.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private ServiceManager serviceManager;
	@Autowired
	private MessageSource messageSource;
	
	public BindingResult validateRequest(QuoteDto quoteDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<QuoteDto> violation : validator.validate(quoteDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("Quote", propertyPath, message));
	        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
	        		"Quote", propertyPath, message);
	    }
		
		User user = userService.getUserById(quoteDto.getUsuarioFinal().toLowerCase());
		// Validamos existencia del usuario
		if (user == null) {
			result.addError(new FieldError("User","usuarioFinal",
					messageSource.getMessage("user.doesnt.exist", 
							new String[]{quoteDto.getUsuarioFinal()}, Locale.getDefault())));
			logger.debug("Validation error in entity <{}>, entity does not exist.","User");
		}
		
		Service service = (quoteDto.getServicio() != null) ? serviceManager.getServiceById(quoteDto.getServicio()) : null;
		// Validamos existencia del servicio
		if (service == null && quoteDto.getServicio() != null) {
			result.addError(new FieldError("Service","servicio",
					messageSource.getMessage("service.doesnt.exist", 
							new Integer[]{quoteDto.getServicio()}, Locale.getDefault())));
			logger.debug("Validation error in entity <{}>, entity does not exist.","Service");
		}
		
		if (service != null && user != null) {
			// El usuario prestador no puede solicitar presupuestos a servicios que son de su autoría
			if (user.getId().equals(service.getUsuarioPrestador().getId())) {
				result.addError(new FieldError("Quote","usuarioFinal",
						messageSource.getMessage("quote.user.owns.service", 
								new String[]{user.getId()}, Locale.getDefault())));
				logger.info("Validation error in entity <{}>, user owns the service for the processed quote.","Quote");
			}
			// Si el servicio es Insitu...
			if (service.isInsitu()) {
				// Validamos que se haya ingresado una dirección de usuario final
				if (quoteDto.getDireccionUsuarioFinal() == null) {
					result.addError(new FieldError("Quote","direccionUsuarioFinal",
							messageSource.getMessage("quote.direccionUsuarioFinal.not.null", 
									null, Locale.getDefault())));
					logger.info("Validation error in entity <{}>, "
							+ "this service requires a customer address to be appointed.","Quote");
				}
				// Validamos que la dirección ingresada sea del usuario final en cuestión
				else {
					boolean addressIsOfCustomer = false;
					for (Address address : user.getDirecciones()) {
						addressIsOfCustomer = (address.equals(quoteDto.getDireccionUsuarioFinal()));
						if (addressIsOfCustomer)
							break;
					}
					if (!addressIsOfCustomer) {
						result.addError(new FieldError("Quote","direccionUsuarioFinal",
								messageSource.getMessage("quote.direccionUsuarioFinal.doesnt.belong.to.user",
										null, Locale.getDefault())));
						logger.info("Validation error in entity <{}>, appointed address doesn't belong to customer.","Quote");
					}
				}
			}
			else
				if (quoteDto.getDireccionUsuarioFinal() != null) {
					result.addError(new FieldError("Quote","direccionUsuarioFinal",
							messageSource.getMessage("quote.direccionUsuarioFinal.not.allowed",
									null, Locale.getDefault())));
					logger.info("Validation error in entity <{}>, "
							+ "this service does not allow to appoint a customer address.","Quote");
				}
		}
		
		return result;
	}
}
