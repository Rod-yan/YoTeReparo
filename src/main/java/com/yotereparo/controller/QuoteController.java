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
import com.yotereparo.controller.dto.validation.QuoteValidation;
import com.yotereparo.controller.mapping.QuoteMapper;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.QuoteService;
import com.yotereparo.service.UserService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.error.CustomResponseError;
/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Presupuestos.
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
	QuoteMapper quoteMapper;
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
					logger.warn("ListQuotes - GET - Request failed - User <{}> doesn't have access to all quotes.", 
							authenticatedUsername);
					FieldError error = new FieldError("Authorization","error",
							messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
				}
			}
			else if ("customer".equalsIgnoreCase(userRole)) {
				logger.debug("Fetching all quotes made by user: <{}>", authenticatedUsername);
				quotes = authenticatedUser.getPresupuestos();
			}
			else if ("provider".equalsIgnoreCase(userRole)) {
				logger.debug("Fetching all quotes directed to user: <{}>", authenticatedUsername);
				for (Service service : authenticatedUser.getServicios())
					quotes.addAll(service.getPresupuestos());
			}
			
			if (quotes != null && !quotes.isEmpty()) {
				List<QuoteDto> quotesDto = quotes.stream()
		                .map(quote -> quoteMapper.convertToDto(quote))
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
		logger.info("GetQuote - GET - Processing request for quote <{}>.", id);
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
                    return new ResponseEntity<QuoteDto>(quoteMapper.convertToDto(quote), HttpStatus.OK);
            	}
    			else {
    				logger.warn("GetQuote - GET - Request failed - User <{}> doesn't have access to quote <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
            }
            else {
            	logger.warn("GetQuote - GET - Request failed - Quote with id <{}> not found.", id);
                FieldError error = new FieldError("Quote","error",
                		messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
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
				Quote quote = quoteMapper.convertToEntity(clientInput);
				if (!quoteService.activeQuoteExistBetween(quote.getUsuarioFinal(), quote.getServicio())) {
					quoteService.createQuote(quote);
					
					HttpHeaders headers = new HttpHeaders();
			        headers.setLocation(ucBuilder.path("/quotes/{id}").buildAndExpand(quote.getId()).toUri());
			        
			        logger.info("CreateQuote - POST - Exiting method, providing response resource to client.");
					return new ResponseEntity<>(headers, HttpStatus.CREATED);
				}
				else {
					logger.warn("CreateQuote - POST - Request failed - Unable to create quote. An active quote already exist between "
							+ "service <{}> and user <{}>.", quote.getServicio().getDescripcion(), quote.getUsuarioFinal().getId());
		            FieldError error = new FieldError("Quote","error",
		            		messageSource.getMessage("quote.active.already.exist", 
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
		logger.info("UpdateQuote - PUT - Processing request for quote <{}>.", id);
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
	    					
	        					quoteService.updateQuote(quoteMapper.convertToEntity(clientInput));
	    						
	    						logger.info("UpdateQuote - PUT - Exiting method, providing response resource to client.");
	    						return new ResponseEntity<QuoteDto>(quoteMapper.convertToDto(quoteService.getQuoteById(id)), HttpStatus.OK);
	        				}
	    				else {
	    					logger.warn("UpdateQuote - PUT - Request failed - Status <{}> forbidden for user <{}> for this opperation.", 
	    							clientInput.getEstado(), authenticatedUsername);
	    					FieldError error = new FieldError("Quote","error",
	    							messageSource.getMessage("quote.estado.forbidden.value", null, Locale.getDefault()));
	    					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
	    				}
    				}
    				else {
    					logger.warn("UpdateQuote - PUT - Request failed - Input validation error(s) detected.");
    					return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
    				}
    			}
    			else {
    				logger.warn("UpdateQuote - PUT - Request failed - User <{}> doesn't have access to quote <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
	        }
			else {
				logger.warn("UpdateQuote - PUT - Request failed - Unable to update quote. Quote <{}> doesn't exist.", id);
	            FieldError error = new FieldError("Quote","error",
	            		messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
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
		logger.info("AcceptQuote - PUT - Processing request for quote <{}>.", id);
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
    				quoteService.customerAcceptsQuoteById(id);
    	        	
    	        	logger.info("AcceptQuote - PUT - Exiting method, providing response resource to client.");
    	            return new ResponseEntity<>(HttpStatus.OK);
    			}
    			else {
    				logger.warn("AcceptQuote - PUT - Request failed - User <{}> doesn't have access to quote <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
	        }
	        else {
	        	logger.warn("AcceptQuote - PUT - Request failed - Unable to accept quote. Quote <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Quote","error",
	        			messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
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
	 * Rechaza un presupuesto por parte del usuario final o usuario prestador.
	 */
	@RequestMapping(
			value = { "/quotes/{id}/reject" },
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> rejectQuote(@PathVariable("id") Integer id) {
		logger.info("RejectQuote - PUT - Processing request for quote <{}>.", id);
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
    			boolean isOwnerAndProvider = quote.getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer)
    				quoteService.customerRejectsQuoteById(id);
    			else if (isOwnerAndProvider)
    				quoteService.providerRejectsQuoteById(id);
    			else {
    				logger.warn("RejectQuote - PUT - Request failed - User <{}> doesn't have access to quote <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
    			
    			logger.info("RejectQuote - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("RejectQuote - PUT - Request failed - Unable to reject quote. Quote <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Quote","error",
	        			messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
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
		logger.info("ArchiveQuote - PUT - Processing request for quote <{}>.", id);
		try {
			if (quoteService.getQuoteById(id) != null) {
				quoteService.archiveQuoteById(id);
	        	
	        	logger.info("ArchiveQuote - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("ArchiveQuote - POST - Request failed - Unable to archive quote. Quote <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Quote","error",
	        			messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
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
		logger.info("DeleteQuote - DELETE - Processing request for quote <{}>.", id);
		try {
			if (quoteService.getQuoteById(id) != null) {
				quoteService.deleteQuoteById(id);
	        	
	        	logger.info("DeleteQuote - DELETE - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("DeleteQuote - DELETE - Request failed - Unable to delete quote. Quote <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Quote","error",
	        			messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
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
