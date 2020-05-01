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

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.controller.dto.mapping.QuoteMapper;
import com.yotereparo.controller.dto.validation.QuoteValidation;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.QuoteService;
import com.yotereparo.service.UserService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.error.CustomResponseError;
/**
 * Controlador REST SpringMVC que expone presupuestos básicos para la gestión de Presupuestos.
 * 
 * @author Rodrigo Yanis
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class QuoteController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);
	
	@Autowired
    QuoteService quoteService;
	@Autowired
    UserService userService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	QuoteValidation quoteValidation;
	@Autowired
	QuoteMapper quoteConverter;
	@Autowired
	MiscUtils miscUtils;

	/*
	 * Devuelve todos los presupuestos registrados en formato JSON.
	 */
	@RequestMapping(
			value = { "/quotes" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
	public ResponseEntity<?> listQuotes(@RequestParam(required = false) String userRole) {
		logger.info("ListQuotes - GET - Processing request for a list with all existing quotes.");
		try {
			Set<Quote> quotes = new HashSet<Quote>(0);
			
			String authenticatedUsername = 
					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			User authenticatedUser = userService.getUserById(authenticatedUsername);
			
			if (userRole == null || userRole.isEmpty()) {
				if (userService.isServiceAccountOrAdministrator(authenticatedUser))
					quotes = new HashSet<Quote>(quoteService.getAllQuotes());
				else {
					logger.warn(
    						String.format("ListQuotes - GET - Request failed - "
    								+ "User <%s> doesn't have access to all quotes.", authenticatedUsername));
					FieldError error = new FieldError(
							"Authorization","error",messageSource.getMessage(
									"client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
				}
			}
			else if ("customer".equalsIgnoreCase(userRole)) {
				logger.debug(String.format(
						"Fetching all quotes made by user: <%s>", authenticatedUser));
				quotes = authenticatedUser.getPresupuestos();
			}
			else if ("provider".equalsIgnoreCase(userRole)) {
				logger.debug(String.format(
						"Fetching all quotes directed to user: <%s>", authenticatedUser));
				for (Service service : authenticatedUser.getServicios())
					quotes.addAll(service.getPresupuestos());
			}
			
			if (quotes != null && !quotes.isEmpty()) {
				List<QuoteDto> quotesDto = quotes.stream()
		                .map(quote -> quoteConverter.convertToDto(quote))
		                .collect(Collectors.toList());
				
	        	logger.info("ListQuotes - GET - Exiting method, providing response resource to client.");
	            return new ResponseEntity<List<QuoteDto>>(quotesDto, HttpStatus.OK);
	        }
	        else {
	        	logger.warn("ListQuotes - GET - Request failed - No quotes were found.");
	        	return new ResponseEntity<List<QuoteDto>>(HttpStatus.NO_CONTENT);
	        }
		}
		catch (Exception e) {
			logger.error("ListQuotes - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError(
					"Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Devuelve el presupuesto solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/quotes/{id}" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
	public ResponseEntity<?> getQuote(@PathVariable("id") Integer id) {
		logger.info(String.format("GetQuote - GET - Processing request for quote <%s>.", id));
        try {
        	Quote quote = quoteService.getQuoteById(id);
    		if (quote != null) {
    			/* 
    			 * Validamos si el presupuesto siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario prestador, o como usuario final)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndCustomer = quote.getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
    			boolean isOwnerAndProvider = 
    					quote.getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer || isOwnerAndProvider) {
            		logger.info("GetQuote - GET - Exiting method, providing response resource to client.");
                    return new ResponseEntity<QuoteDto>(quoteConverter.convertToDto(quote), HttpStatus.OK);
            	}
    			else {
    				logger.warn(
    						String.format("GetQuote - GET - Request failed - "
    								+ "User <%s> doesn't have access to quote <%s> in this context.", authenticatedUsername, id));
    				FieldError error = new FieldError(
							"Authorization","error",messageSource.getMessage(
									"client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
            }
            else {
            	logger.warn(String.format("GetQuote - GET - Request failed - Quote with id <%s> not found.", id));
                FieldError error = new FieldError(
                		"Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
			logger.error("GetQuote - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Crea un presupuesto con los valores del JSON payload recibido.
	 */
	@RequestMapping(
			value = { "/quotes" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> createQuote(@RequestBody QuoteDto clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {	
		logger.info("CreateQuote - POST - Processing request for new quote.");
		try {
			String authenticatedUsername = 
					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			
			// Setteamos el usuario final de acuerdo al usuario autenticado que está registrando el request.
			clientInput.setUsuarioFinal(authenticatedUsername);
			if (!quoteValidation.validateRequest(clientInput, result).hasErrors()) {
				Quote quote = quoteConverter.convertToEntity(clientInput);
				if (!quoteService.activeQuoteExistBetween(quote.getUsuarioFinal(), quote.getServicio())) {
					quoteService.createQuote(quote);
					
					HttpHeaders headers = new HttpHeaders();
			        headers.setLocation(ucBuilder.path("/quotes/{id}").buildAndExpand(quote.getId()).toUri());
			        
			        logger.info("CreateQuote - POST - Exiting method, providing response resource to client.");
					return new ResponseEntity<>(headers, HttpStatus.CREATED);
				}
				else {
					logger.warn(
							String.format("CreateQuote - POST - Request failed - Unable to create quote. An active quote already exist between "
							+ "service <%s> and"
							+ "user <%s>.", quote.getServicio().getDescripcion(), quote.getUsuarioFinal().getId()));
		            FieldError error = new FieldError(
		            		"Quote","error",messageSource.getMessage(
		            				"quote.active.already.exist", 
		            				new String[]{quote.getServicio().getDescripcion(), quote.getUsuarioFinal().getId()}, Locale.getDefault()));
		            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.CONFLICT);
				}
			}
			else {
				logger.warn("CreateQuote - POST - Request failed - Input validation error(s) detected.");
				return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
			}
        }
		catch (CustomResponseError e) {
			logger.warn("CreateQuote - POST - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("CreateQuote - POST - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Actualiza los atributos del presupuesto con los valores recibidos en el JSON payload. 
	 * Si estos no se incluyen en el request body entonces se considera que se está intentando vaciar su valor. 
	 * Esto es legal solo para atributos no mandatorios en la entidad.
	 */
	@RequestMapping(
			value = { "/quotes/{id}" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> updateQuote(@PathVariable("id") Integer id, @RequestBody QuoteDto clientInput, BindingResult result) {	
		logger.info(String.format("UpdateQuote - PUT - Processing request for quote <%s>.", id));
		try {
			clientInput.setId(id);
			Quote quote = quoteService.getQuoteById(id);
			if (quote != null) {
				/* 
    			 * Validamos si el presupuesto siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario prestador, o como usuario final)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndCustomer = quote.getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
    			boolean isOwnerAndProvider = 
    					quote.getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer || isOwnerAndProvider) {
    				clientInput.setUsuarioFinal(quote.getUsuarioFinal().getId());
    				if (!quoteValidation.validateRequest(clientInput, result).hasErrors()) {
	    				if (isServiceAccountOrAdministrator || 
	    					(isOwnerAndCustomer && clientInput.getEstado().equalsIgnoreCase(Quote.AWAITING_PROVIDER)) ||
	    					(isOwnerAndProvider && clientInput.getEstado().equalsIgnoreCase(Quote.AWAITING_CUSTOMER))) {
	    					
	        					quoteService.updateQuote(quoteConverter.convertToEntity(clientInput));
	    						
	    						logger.info("UpdateQuote - PUT - Exiting method, providing response resource to client.");
	    						return new ResponseEntity<QuoteDto>(quoteConverter.convertToDto(quoteService.getQuoteById(id)), HttpStatus.OK);
	        				}
	    				else {
	    					logger.warn(String.format("UpdateQuote - PUT - Request failed - "
	        						+ "Status <%s> forbidden for user <%s> for this opperation.", clientInput.getEstado(), authenticatedUsername));
	    					FieldError error = new FieldError(
	    							"Quote","error",messageSource.getMessage(
	    									"quote.estado.forbidden.value", null, Locale.getDefault()));
	    					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
	    				}
    				}
    				else {
    					logger.warn("UpdateQuote - PUT - Request failed - Input validation error(s) detected.");
    					return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
    				}
    			}
    			else {
    				logger.warn(String.format("UpdateQuote - PUT - Request failed - "
    						+ "User <%s> doesn't have access to quote <%s> in this context.", authenticatedUsername, id));
    				FieldError error = new FieldError(
							"Authorization","error",messageSource.getMessage(
									"client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
	        }
			else {
				logger.warn(String.format("UpdateQuote - PUT - Request failed - Unable to update quote. Quote <%s> doesn't exist.", id));
	            FieldError error = new FieldError(
	            		"Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (CustomResponseError e) {
			logger.warn("UpdateQuote - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("UpdateQuote - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Acepta un presupuesto por parte del usuario final.
	 */
	@RequestMapping(
			value = { "/quotes/{id}/accept" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> acceptQuote(@PathVariable("id") Integer id) {
		logger.info(String.format("AcceptQuote - PUT - Processing request for quote <%s>.", id));
		try {
			Quote quote = quoteService.getQuoteById(id);
			if (quote != null) {
				/* 
    			 * Validamos si el presupuesto siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario prestador, o como usuario final)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndCustomer = quote.getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer) {
    				quoteService.customerAcceptsQuote(id);
    	        	
    	        	logger.info("AcceptQuote - PUT - Exiting method, providing response resource to client.");
    	            return new ResponseEntity<>(HttpStatus.OK);
    			}
    			else {
    				logger.warn(String.format("AcceptQuote - PUT - Request failed - "
    						+ "User <%s> doesn't have access to quote <%s> in this context.", authenticatedUsername, id));
    				FieldError error = new FieldError(
							"Authorization","error",messageSource.getMessage(
									"client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
	        }
	        else {
	        	logger.warn(String.format("AcceptQuote - PUT - Request failed - Unable to accept quote. Quote <%s> doesn't exist.", id));
	        	FieldError error = new FieldError(
	        			"Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.warn("AcceptQuote - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("AcceptQuote - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Rechaza un presupuesto por parte del usuario final o usuario prestador,
	 * de acuerdo a lo recibido en el URI path (customer/provider).
	 */
	@RequestMapping(
			value = { "/quotes/{id}/reject/{userRole}" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> rejectQuote(@PathVariable("id") Integer id, @PathVariable("userRole") String userRole) {
		logger.info(String.format("RejectQuote - PUT - Processing request for quote <%s> and entity <%s>.", id, userRole));
		try {
			if (userRole != null && !userRole.isEmpty() && ("customer".equalsIgnoreCase(userRole) || "provider".equalsIgnoreCase(userRole))) {
				Quote quote = quoteService.getQuoteById(id);
				if (quote != null) {
					/* 
	    			 * Validamos si el presupuesto siendo procesado le pertenezca 
	    			 * al usuario autenticado (como usuario prestador, o como usuario final)
	    			 */
	    			String authenticatedUsername = 
	    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
	    			boolean isServiceAccountOrAdministrator = 
	    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
	    			boolean isOwnerAndCustomer = quote.getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
	    			boolean isOwnerAndProvider = quote.getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
	    			
	    			if ((isServiceAccountOrAdministrator || isOwnerAndCustomer) && "customer".equalsIgnoreCase(userRole))
	    				quoteService.customerRejectsQuote(id);
	    			else if ((isServiceAccountOrAdministrator || isOwnerAndProvider) && "provider".equalsIgnoreCase(userRole))
	    				quoteService.providerRejectsQuote(id);
	    			else {
	    				logger.warn(String.format("RejectQuote - PUT - Request failed - "
	    						+ "User <%s> doesn't have access to quote <%s> in this context.", authenticatedUsername, id));
	    				FieldError error = new FieldError(
								"Authorization","error",messageSource.getMessage(
										"client.error.unauthorized", null, Locale.getDefault()));
						return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
	    			}
	    			
	    			logger.info("RejectQuote - PUT - Exiting method, providing response resource to client.");
		            return new ResponseEntity<>(HttpStatus.OK);
		        }
		        else {
		        	logger.warn(String.format("RejectQuote - PUT - Request failed - Unable to reject quote. Quote <%s> doesn't exist.", id));
		        	FieldError error = new FieldError(
		        			"Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
		        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
		        }
			}
			else {
				logger.warn(String.format("RejectQuote - PUT - Request failed - "
						+ "Incorrect URI path argument <%s> (must be customer | provider).", userRole));
	        	FieldError error = new FieldError(
	        			"Quote","error",messageSource.getMessage(
	        					"quote.incorrect.rejection.uri.argument", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.METHOD_NOT_ALLOWED);
			}
		}
		catch (CustomResponseError e) {
			logger.warn("RejectQuote - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("RejectQuote - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Archiva un presupuesto.
	 */
	@RequestMapping(
			value = { "/quotes/{id}/archive" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> archiveQuote(@PathVariable("id") Integer id) {
		logger.info(String.format("ArchiveQuote - PUT - Processing request for quote <%s>.", id));
		try {
			if (quoteService.getQuoteById(id) != null) {
				quoteService.archiveQuoteById(id);
	        	
	        	logger.info("ArchiveQuote - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn(String.format("ArchiveQuote - POST - Request failed - Unable to archive quote. Quote <%s> doesn't exist.", id));
	        	FieldError error = new FieldError(
	        			"Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.warn("ArchiveQuote - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("ArchiveQuote - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Elimina físicamente un presupuesto.
	 */
	@RequestMapping(
			value = { "/quotes/{id}" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> deleteQuote(@PathVariable("id") Integer id) {
		logger.info(String.format("DeleteQuote - DELETE - Processing request for quote <%s>.", id));
		try {
			if (quoteService.getQuoteById(id) != null) {
				quoteService.deleteQuoteById(id);
	        	
	        	logger.info("DeleteQuote - DELETE - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn(
	        			String.format("DeleteQuote - DELETE - Request failed - Unable to delete quote. Quote <%s> doesn't exist.", id));
	        	FieldError error = new FieldError(
	        			"Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.warn("DeleteQuote - DELETE - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("DeleteQuote - DELETE - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
}
