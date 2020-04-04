package com.yotereparo.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yotereparo.model.ServiceType;
import com.yotereparo.service.ServiceTypeService;
import com.yotereparo.util.MiscUtils;
/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Tipos de Servicio.
 * 
 * @author Rodrigo Yanis
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ServiceTypeController {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceTypeController.class);
	
	@Autowired
    ServiceTypeService serviceTypeService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	MiscUtils miscUtils;

	/*
	 * Devuelve todos los Tipos de Servicio registradas en formato JSON.
	 */
	@RequestMapping(
			value = { "/servicetypes" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	public ResponseEntity<?> listServiceTypes() {
		logger.info("ListServiceTypes - GET - Processing request for a list with all existing service types.");
        try {
        	List<ServiceType> serviceTypes = serviceTypeService.getAllServiceTypes();
            
    		if (!serviceTypes.isEmpty()) {
            	logger.info("ListServiceTypes - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<List<ServiceType>>(serviceTypes, HttpStatus.OK);
            }
            else {
            	logger.warn("ListServiceTypes - GET - Request failed - No service types were found.");
            	return new ResponseEntity<List<ServiceType>>(HttpStatus.NO_CONTENT);
            }
        }
		catch (Exception e) {
			logger.error("ListServiceTypes - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("ServiceType","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Devuelve el Tipo de Servicio solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/servicetypes/{id}" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	public ResponseEntity<?> getServiceType(@PathVariable("id") Integer id) {
		logger.info(String.format("GetServiceType - GET - Processing request for service type <%s>.", id));
        try {
        	ServiceType serviceType = serviceTypeService.getServiceTypeById(id);
            
    		if (serviceType != null) {
            	logger.info("GetServiceType - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<ServiceType>(serviceType, HttpStatus.OK);
            }
            else {
            	logger.warn(String.format("GetServiceType - GET - Request failed - Service Type with id <%s> not found.", id));
                FieldError error = new FieldError("ServiceType","error",messageSource.getMessage("serviceType.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
            } 
        }
        catch (Exception e) {
			logger.error("GetServiceType - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("ServiceType","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
}
