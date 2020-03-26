package com.yotereparo.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.controller.dto.converter.QuoteConverter;
import com.yotereparo.model.Quote;
import com.yotereparo.service.QuoteService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.ValidationUtils;
import com.yotereparo.util.error.CustomResponseError;
/**
 * Controlador REST SpringMVC que expone presupuestos básicos para la gestión de Presupuestos.
 * 
 * @author Rodrigo Yanis
 * 
 */
@RestController
public class QuoteController {
	
	private static final Logger logger = LogManager.getLogger(QuoteController.class);
	
	@Autowired
    QuoteService quoteService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	ValidationUtils validationUtils;
	@Autowired
	QuoteConverter quoteConverter;

	/*
	 * Devuelve todos los presupuestos registrados en formato JSON.
	 */
	@RequestMapping(
			value = { "/quotes" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<?> listQuotes() {
		logger.info("ListQuotes - GET - Processing request for a list with all existing quotes.");
		try {
			List<Quote> quotes = quoteService.getAllQuotes();
			
			if (quotes != null && !quotes.isEmpty()) {
				List<QuoteDto> quotesDto = quotes.stream()
		                .map(quote -> quoteConverter.convertToDto(quote))
		                .collect(Collectors.toList());
				
	        	logger.info("ListQuotes - GET - Exiting method, providing response resource to client.");
	            return new ResponseEntity<List<QuoteDto>>(quotesDto, HttpStatus.OK);
	        }
	        else {
	        	logger.info("ListQuotes - GET - Request failed - No quotes were found.");
	        	return new ResponseEntity<List<QuoteDto>>(HttpStatus.NO_CONTENT);
	        }
		}
		catch (Exception e) {
			logger.error("ListQuotes - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Devuelve el presupuesto solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/quotes/{id}" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<?> getQuote(@PathVariable("id") Integer id) {
		logger.info(String.format("GetQuote - GET - Processing request for quote <%s>.", id));
        try {
        	Quote quote = quoteService.getQuoteById(id);
            
    		if (quote != null) {
            	logger.info("GetQuote - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<QuoteDto>(quoteConverter.convertToDto(quote), HttpStatus.OK);
            }
            else {
            	logger.info(String.format("GetQuote - GET - Request failed - Quote with id <%s> not found.", id));
                FieldError error = new FieldError("Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
			logger.error("GetQuote - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Crea un presupuesto con los valores del JSON payload recibido.
	 */
	@RequestMapping(
			value = { "/quotes" }, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
    public ResponseEntity<?> createQuote(@RequestBody QuoteDto clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {	
		logger.info("CreateQuote - POST - Processing request for new quote.");
		try {
			if (!validationUtils.quoteInputValidation(clientInput, result).hasErrors()) {
				// Seteamos id en null ya que el mismo es autogenerado en tiempo de creación
				clientInput.setId(null);
				Quote quote = quoteConverter.convertToEntity(clientInput);
				if (!quoteService.activeQuoteExistBetween(quote.getUsuarioFinal(), quote.getServicio())) {
					quoteService.createQuote(quote);
					
					HttpHeaders headers = new HttpHeaders();
			        headers.setLocation(ucBuilder.path("/quotes/{id}").buildAndExpand(quote.getId()).toUri());
			        
			        logger.info("CreateQuote - POST - Exiting method, providing response resource to client.");
					return new ResponseEntity<>(headers, HttpStatus.CREATED);
				}
				else {
					logger.info(String.format("CreateQuote - POST - Request failed - Unable to create quote. An active quote already exist between "
							+ "service <%s> and"
							+ "user <%s>.", quote.getServicio().getDescripcion(), quote.getUsuarioFinal().getId()));
		            FieldError error = new FieldError("Quote","error",messageSource.getMessage("quote.active.already.exist", new String[]{quote.getServicio().getDescripcion(), quote.getUsuarioFinal().getId()}, Locale.getDefault()));
		            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.CONFLICT);
				}
			}
			else {
				logger.info("CreateQuote - POST - Request failed - Input validation error(s) detected.");
				return new ResponseEntity<>(MiscUtils.getFormatedResponseErrorList(result).toString(), HttpStatus.BAD_REQUEST);
			}
        }
		catch (CustomResponseError e) {
			logger.error("CreateQuote - POST - Request failed - Error procesing request.");
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(e).toString(), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("CreateQuote - POST - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Actualiza los atributos del presupuesto con los valores recibidos en el JSON payload. 
	 * Si estos no se incluyen en el request body entonces se considera que se está intentando vaciar su valor. 
	 * Esto es legal solo para atributos no mandatorios en la entidad.
	 */
	@RequestMapping(
			value = { "/quotes/{id}" }, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.PUT)
    public ResponseEntity<?> updateQuote(@PathVariable("id") Integer id, @RequestBody QuoteDto clientInput, BindingResult result) {	
		logger.info(String.format("UpdateQuote - PUT - Processing request for quote <%s>.", id));
		try {
			clientInput.setId(id);
			
			if (quoteService.getQuoteById(id) != null) {
				if (!validationUtils.quoteInputValidation(clientInput, result).hasErrors()) {
					Quote quote = quoteConverter.convertToEntity(clientInput);
					
					Boolean quoteIsRegisteredToUser = false;
					for (Quote s : quote.getUsuarioFinal().getPresupuestos())
						if (s.getId().equals(id)) {
							quoteIsRegisteredToUser = true;
							break;
						}
					Boolean quoteIsRegisteredToService = false;
					for (Quote s : quote.getServicio().getPresupuestos())
						if (s.getId().equals(id)) {
							quoteIsRegisteredToService = true;
							break;
						}
					if (quoteIsRegisteredToUser && quoteIsRegisteredToService) {
						quoteService.updateQuote(quote);
						
						logger.info("UpdateQuote - PUT - Exiting method, providing response resource to client.");
						return new ResponseEntity<QuoteDto>(quoteConverter.convertToDto(quoteService.getQuoteById(id)), HttpStatus.OK);
					}
					else {
						logger.info(String.format("UpdateQuote - PUT - Request failed - Unable to update quote. Quote <%s> doesn't belong to user <%s> and service <%s>.", id, quote.getUsuarioFinal().getId(), quote.getServicio().getDescripcion()));
						FieldError error = new FieldError("Quote","presupuestos",messageSource.getMessage("quote.doesnt.belong.to.user.and.service", new String[]{quote.getUsuarioFinal().getId(), quote.getServicio().getDescripcion()}, Locale.getDefault()));
						return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.BAD_REQUEST);
					}
				}
				else {
					logger.info("UpdateQuote - PUT - Request failed - Input validation error(s) detected.");
					return new ResponseEntity<>(MiscUtils.getFormatedResponseErrorList(result).toString(), HttpStatus.BAD_REQUEST);
				}
	        }
			else {
				logger.info(String.format("UpdateQuote - PUT - Request failed - Unable to update quote. Quote <%s> doesn't exist.", id));
	            FieldError error = new FieldError("Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
			}
		}
		catch (CustomResponseError e) {
			logger.error("UpdateQuote - PUT - Request failed - Error procesing request.");
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(e).toString(), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("UpdateQuote - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Acepta un presupuesto por parte del usuario final.
	 */
	@RequestMapping(
			value = { "/quotes/{id}/accept" }, 
			produces = MediaType.APPLICATION_JSON_VALUE,			
			method = RequestMethod.PUT)
    public ResponseEntity<?> acceptQuote(@PathVariable("id") Integer id) {
		logger.info(String.format("AcceptQuote - PUT - Processing request for quote <%s>.", id));
		try {
			if (quoteService.getQuoteById(id) != null) {
				quoteService.customerAcceptsQuote(id);
	        	
	        	logger.info("AcceptQuote - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.info(String.format("AcceptQuote - PUT - Request failed - Unable to accept quote. Quote <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.error("AcceptQuote - PUT - Request failed - Error procesing request.");
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(e).toString(), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("AcceptQuote - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Rechaza un presupuesto por parte del usuario final o usuario prestador,
	 * de acuerdo a lo recibido en el URI path (customer/provider).
	 */
	@RequestMapping(
			value = { "/quotes/{id}/reject/{userType}" }, 
			produces = MediaType.APPLICATION_JSON_VALUE,			
			method = RequestMethod.PUT)
    public ResponseEntity<?> rejectQuote(@PathVariable("id") Integer id, @PathVariable("userType") String userType) {
		logger.info(String.format("RejectQuote - PUT - Processing request for quote <%s> and entity <%s>.", id, userType));
		try {
			if (userType != null && !userType.isEmpty() && (userType.equals("customer") || userType.equals("provider"))) {
				if (quoteService.getQuoteById(id) != null) {
					userType = userType.toLowerCase();
					switch (userType) {
						case "customer":
							quoteService.customerRejectsQuote(id);
							break;
						case "provider":
							quoteService.providerRejectsQuote(id);
							break;
					}
					
					logger.info("RejectQuote - PUT - Exiting method, providing response resource to client.");
		            return new ResponseEntity<>(HttpStatus.OK);
		        }
		        else {
		        	logger.info(String.format("RejectQuote - PUT - Request failed - Unable to reject quote. Quote <%s> doesn't exist.", id));
		        	FieldError error = new FieldError("Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
		        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
		        }
			}
			else {
				logger.info(String.format("RejectQuote - PUT - Request failed - Incorrect URI path argument <%s> (must be customer | provider).", userType));
	        	FieldError error = new FieldError("Quote","error",messageSource.getMessage("quote.incorrect.rejection.uri.argument", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.BAD_REQUEST);
			}
		}
		catch (CustomResponseError e) {
			logger.error("RejectQuote - PUT - Request failed - Error procesing request.");
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(e).toString(), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("RejectQuote - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Archiva un presupuesto.
	 */
	@RequestMapping(
			value = { "/quotes/{id}/archive" }, 
			produces = MediaType.APPLICATION_JSON_VALUE,			
			method = RequestMethod.PUT)
    public ResponseEntity<?> archiveQuote(@PathVariable("id") Integer id) {
		logger.info(String.format("ArchiveQuote - PUT - Processing request for quote <%s>.", id));
		try {
			if (quoteService.getQuoteById(id) != null) {
				quoteService.archiveQuoteById(id);
	        	
	        	logger.info("ArchiveQuote - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.info(String.format("ArchiveQuote - POST - Request failed - Unable to archive quote. Quote <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.error("ArchiveQuote - PUT - Request failed - Error procesing request.");
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(e).toString(), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("ArchiveQuote - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Elimina físicamente un presupuesto.
	 */
	@RequestMapping(
			value = { "/quotes/{id}" }, 
			produces = MediaType.APPLICATION_JSON_VALUE,			
			method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteQuote(@PathVariable("id") Integer id) {
		logger.info(String.format("DeleteQuote - DELETE - Processing request for quote <%s>.", id));
		try {
			if (quoteService.getQuoteById(id) != null) {
				quoteService.deleteQuoteById(id);
	        	
	        	logger.info("DeleteQuote - DELETE - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.info(String.format("DeleteQuote - DELETE - Request failed - Unable to delete quote. Quote <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("Quote","error",messageSource.getMessage("quote.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.error("DeleteQuote - DELETE - Request failed - Error procesing request.");
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(e).toString(), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("DeleteQuote - DELETE - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Quote","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
}
