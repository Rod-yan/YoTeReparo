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

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.converter.ServiceConverter;
import com.yotereparo.model.Service;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.service.UserService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.ValidationUtils;
import com.yotereparo.util.error.CustomResponseError;
/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Servicios.
 * 
 * @author Rodrigo Yanis
 * 
 */
@RestController
public class ServiceController {
	
	private static final Logger logger = LogManager.getLogger(ServiceController.class);
	
	@Autowired
    ServiceManager serviceManager;
	@Autowired
    UserService userService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	ServiceConverter serviceConverter;

	/*
	 * Devuelve todos los servicios registrados en formato JSON.
	 */
	@RequestMapping(
			value = { "/services/" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<List<ServiceDto>> listServices() {
		logger.info("ListServices - GET - Processing request for a list with all existing services.");

        List<Service> services = serviceManager.getAllServices();
        
		if (!services.isEmpty()) {
			
			List<ServiceDto> servicesDto = services.stream()
	                .map(service -> serviceConverter.convertToDto(service))
	                .collect(Collectors.toList());
			
        	logger.info("ListServices - GET - Exiting method, providing response resource to client.");
            return new ResponseEntity<List<ServiceDto>>(servicesDto, HttpStatus.OK);
        }
        else {
        	logger.info("ListServices - GET - Request failed - No services were found.");
        	return new ResponseEntity<List<ServiceDto>>(HttpStatus.NO_CONTENT);
        }
    }
	
	/*
	 * Devuelve el servicio solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/services/{id}" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<?> getService(@PathVariable("id") Integer id) {
		logger.info(String.format("GetService - GET - Processing request for service <%s>.", id));
        
		Service service = serviceManager.getServiceById(id);
        
		if (service != null) {
        	logger.info("GetService - GET - Exiting method, providing response resource to client.");
            return new ResponseEntity<ServiceDto>(serviceConverter.convertToDto(service), HttpStatus.OK);
        }
        else {
        	logger.info(String.format("GetService - GET - Request failed - Service with id <%s> not found.", id));
            FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
        } 
    }
	
	/*
	 * Crea un servicio con los valores del JSON payload recibido.
	 */
	@RequestMapping(
			value = { "/services/" }, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
    public ResponseEntity<?> createService(@RequestBody ServiceDto clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {
		
		if (!ValidationUtils.serviceInputValidation(clientInput, result).hasErrors()) {
			logger.info(String.format("CreateService - POST - Processing request for service <%s>.", clientInput.getId()));
			if (!serviceManager.exist(clientInput.getId())) {
				try {
					serviceManager.createService(serviceConverter.convertToEntity(clientInput));
					
					HttpHeaders headers = new HttpHeaders();
			        headers.setLocation(ucBuilder.path("/services/{id}").buildAndExpand(clientInput.getId()).toUri());
			        
			        logger.info("CreateService - POST - Exiting method, providing response resource to client.");
					return new ResponseEntity<>(headers, HttpStatus.CREATED);
				}
				catch (CustomResponseError error) {
					logger.error(String.format("CreateService - POST - Request failed - Error procesing request: <%s>", error.toString()));
					return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
	        }
			else {
				logger.info(String.format("CreateService - POST - Request failed - Unable to create service. Service <%s> already exist.", clientInput.getId()));
	            FieldError error = new FieldError("Service","error",messageSource.getMessage("service.already.exist", new Integer[]{clientInput.getId()}, Locale.getDefault()));
	            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.CONFLICT);
			}
		}
		else {
			logger.info("CreateService - POST - Request failed - Input validation error(s) detected.");
			return new ResponseEntity<>(MiscUtils.getFormatedResponseErrorList(result).toString(), HttpStatus.BAD_REQUEST);
		}	
    }
}
