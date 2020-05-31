package com.yotereparo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.yotereparo.controller.dto.MessageDto;
import com.yotereparo.controller.dto.validation.MessageValidation;
import com.yotereparo.controller.mapping.MessageMapper;
import com.yotereparo.model.Message;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.MessageService;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.service.UserService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.error.CustomResponseError;
/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Mensajes.
 * 
 * @author Rodrigo Yanis
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class MessageController {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	@Autowired
    MessageService messageService;
	@Autowired
    UserService userService;
	@Autowired
    ServiceManager serviceManager;
	@Autowired
    MessageSource messageSource;
	@Autowired
	MessageValidation messageValidation;
	@Autowired
	MessageMapper messageMapper;
	@Autowired
	MiscUtils miscUtils;

	/*
	 * Devuelve todos los mensajes registrados en formato JSON.
	 */
	@RequestMapping(
			value = { "/messages" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
	public ResponseEntity<?> listMessages(@RequestParam(required = false) String userRole) {
		logger.info("ListMessages - GET - Processing request for a list with all existing messages.");
		try {
			Set<Message> messages = new HashSet<Message>(0);
			
			String authenticatedUsername = 
					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			User authenticatedUser = userService.getUserById(authenticatedUsername);
			
			if (userRole == null || userRole.isEmpty()) {
				if (userService.isServiceAccountOrAdministrator(authenticatedUser))
					messages = new HashSet<Message>(messageService.getAllMessages());
				else {
					logger.warn("ListMessages - GET - Request failed - User <{}> doesn't have access to all messages.", 
							authenticatedUsername);
					FieldError error = new FieldError("Authorization","error",
							messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
				}
			}
			else if ("customer".equalsIgnoreCase(userRole)) {
				logger.debug("Fetching all messages made by user: <{}>", authenticatedUsername);
				messages = authenticatedUser.getMensajes();
			}
			else if ("provider".equalsIgnoreCase(userRole)) {
				logger.debug("Fetching all messages directed to user: <{}>", authenticatedUsername);
				for (Service service : authenticatedUser.getServicios())
					messages.addAll(service.getMensajes());
			}
			
			if (messages != null && !messages.isEmpty()) {
				List<MessageDto> messagesDto = messages.stream()
		                .map(message -> messageMapper.convertToDto(message))
		                .collect(Collectors.toList());
				
	        	logger.info("ListMessages - GET - Exiting method, providing response resource to client.");
	            return new ResponseEntity<List<MessageDto>>(messagesDto, HttpStatus.OK);
	        }
	        else {
	        	logger.warn("ListMessages - GET - Request failed - No messages were found.");
	        	return new ResponseEntity<List<MessageDto>>(HttpStatus.NO_CONTENT);
	        }
		}
		catch (Exception e) {
			logger.error("ListMessages - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError(
					"Message","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Devuelve el mensaje solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/messages/{id}" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
	public ResponseEntity<?> getMessage(@PathVariable("id") Integer id) {
		logger.info("GetMessage - GET - Processing request for message <{}>.", id);
        try {
        	Message message = messageService.getMessageById(id);
    		if (message != null) {
    			/* 
    			 * Validamos si el mensaje siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario prestador, o como usuario final)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndCustomer = message.getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
    			boolean isOwnerAndProvider = 
    					message.getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer || isOwnerAndProvider) {
            		logger.info("GetMessage - GET - Exiting method, providing response resource to client.");
                    return new ResponseEntity<MessageDto>(messageMapper.convertToDto(message), HttpStatus.OK);
            	}
    			else {
    				logger.warn("GetMessage - GET - Request failed - User <{}> doesn't have access to message <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
            }
            else {
            	logger.warn("GetMessage - GET - Request failed - Message with id <{}> not found.", id);
                FieldError error = new FieldError("Message","error",
                		messageSource.getMessage("message.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
			logger.error("GetMessage - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Message","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Crea un mensaje con los valores del JSON payload recibido.
	 */
	@RequestMapping(
			value = { "/messages" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> createMessage(@RequestBody MessageDto clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {	
		logger.info("CreateMessage - POST - Processing request for new message.");
		try {
			String authenticatedUsername = 
					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			
			// Setteamos el usuario final de acuerdo al usuario autenticado que está registrando el request.
			clientInput.setUsuarioFinal(authenticatedUsername);
			if (!messageValidation.validateRequest(clientInput, result).hasErrors()) {
				Service service = serviceManager.getServiceById(clientInput.getServicio());
				User user = userService.getUserById(authenticatedUsername);
				if (!messageService.wasServiceRecentlyMessagedByUser(service, user)) {
					Message message = messageMapper.convertToEntity(clientInput);
					messageService.createMessage(message);
					
					HttpHeaders headers = new HttpHeaders();
			        headers.setLocation(ucBuilder.path("/messages/{id}").buildAndExpand(message.getId()).toUri());
			        
			        logger.info("CreateMessage - POST - Exiting method, providing response resource to client.");
					return new ResponseEntity<>(headers, HttpStatus.CREATED);
				}
				else {
					logger.warn("CreateMessage - POST - Request failed - Violation of cooldown restrictions for new messages.");
					FieldError error = new FieldError("Message","error",
							messageSource.getMessage("too.many.requests", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.TOO_MANY_REQUESTS);
				}
			}
			else {
				logger.warn("CreateMessage - POST - Request failed - Input validation error(s) detected.");
				return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
			}
        }
		catch (CustomResponseError e) {
			logger.warn("CreateMessage - POST - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("CreateMessage - POST - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Message","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Actualiza los atributos del mensaje con los valores recibidos en el JSON payload. 
	 */
	@RequestMapping(
			value = { "/messages/{id}" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('USUARIO_PRESTADOR_GRATUITA')"
			+ " or hasAuthority('USUARIO_PRESTADOR_PLATA')"
			+ " or hasAuthority('USUARIO_PRESTADOR_ORO')"
			+ " or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> updateMessage(@PathVariable("id") Integer id, @RequestBody MessageDto clientInput, BindingResult result) {	
		logger.info("UpdateMessage - PUT - Processing request for message <{}>.", id);
		try {
			clientInput.setId(id);
			Message message = messageService.getMessageById(id);
			if (message != null) {
				/* 
    			 * Validamos si el mensaje siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario prestador)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndProvider = 
    					message.getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndProvider) {
    				clientInput.setUsuarioFinal(message.getUsuarioFinal().getId());
    				clientInput.setServicio(message.getServicio().getId());
    				if (!messageValidation.validateRequest(clientInput, result).hasErrors()) {
    					messageService.updateMessage(messageMapper.convertToEntity(clientInput));
						
						logger.info("UpdateMessage - PUT - Exiting method, providing response resource to client.");
						return new ResponseEntity<MessageDto>(
								messageMapper.convertToDto(messageService.getMessageById(id)), HttpStatus.OK);
    				}
    				else {
    					logger.warn("UpdateMessage - PUT - Request failed - Input validation error(s) detected.");
    					return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
    				}
    			}
    			else {
    				logger.warn("UpdateMessage - PUT - Request failed - User <{}> doesn't have access to message <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
	        }
			else {
				logger.warn("UpdateMessage - PUT - Request failed - Unable to update message. Message <{}> doesn't exist.", id);
	            FieldError error = new FieldError("Message","error",
	            		messageSource.getMessage("message.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (CustomResponseError e) {
			logger.warn("UpdateMessage - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("UpdateMessage - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Message","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Elimina físicamente un mensaje.
	 */
	@RequestMapping(
			value = { "/messages/{id}" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> deleteMessage(@PathVariable("id") Integer id) {
		logger.info("DeleteMessage - DELETE - Processing request for message <{}>.", id);
		try {
			if (messageService.getMessageById(id) != null) {
				messageService.deleteMessageById(id);
	        	
	        	logger.info("DeleteMessage - DELETE - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("DeleteMessage - DELETE - Request failed - Unable to delete message. Message <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Message","error",
	        			messageSource.getMessage("message.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.warn("DeleteMessage - DELETE - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("DeleteMessage - DELETE - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Message","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
}
