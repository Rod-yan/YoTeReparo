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

import com.yotereparo.controller.dto.MessageDto;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.service.UserService;

/**
 * Responsable de efectuar la validación de reglas de integridad de datos y reglas de negocio
 * para los requests recibidos para DTOs de Mensaje.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class MessageValidation {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageValidation.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private ServiceManager serviceManager;
	@Autowired
	private MessageSource messageSource;
	
	public BindingResult validateRequest(MessageDto messageDto, BindingResult result) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		for (ConstraintViolation<MessageDto> violation : validator.validate(messageDto)) {
			String propertyPath = violation.getPropertyPath().toString();
	        String message = violation.getMessage();
	        result.addError(new FieldError("Message", propertyPath, message));
	        logger.debug("Validation error in entity <{}>'s attribute <{}>, with message <{}>", 
	        		"Message", propertyPath, message);
	    }
		
		User user = userService.getUserById(messageDto.getUsuarioFinal().toLowerCase());
		// Validamos existencia del usuario
		if (user == null) {
			result.addError(new FieldError("User","usuarioFinal",
					messageSource.getMessage("user.doesnt.exist", 
							new String[]{messageDto.getUsuarioFinal()}, Locale.getDefault())));
			logger.debug("Validation error in entity <{}>, entity does not exist.","User");
		}
		
		Service service = (messageDto.getServicio() != null) ? serviceManager.getServiceById(messageDto.getServicio()) : null;
		// Validamos existencia del servicio
		if (service == null && messageDto.getServicio() != null) {
			result.addError(new FieldError("Service","servicio",
					messageSource.getMessage("service.doesnt.exist", 
							new Integer[]{messageDto.getServicio()}, Locale.getDefault())));
			logger.debug("Validation error in entity <{}>, entity does not exist.","Service");
		}
		
		if (service != null && user != null)
			// El usuario prestador no puede enviarse mensajes a sí mismo
			if (user.getId().equals(service.getUsuarioPrestador().getId())) {
				result.addError(new FieldError("Message","usuarioFinal",
						messageSource.getMessage("message.user.owns.service", 
								new String[]{user.getId()}, Locale.getDefault())));
				logger.info("Validation error in entity <{}>, user owns the service to which is trying to submit a message.","Message");
			}
		
		return result;
	}
}
