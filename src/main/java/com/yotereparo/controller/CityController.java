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

import com.yotereparo.model.City;
import com.yotereparo.service.CityService;
import com.yotereparo.util.MiscUtils;
/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Ciudades.
 * 
 * @author Rodrigo Yanis
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class CityController {
	
	private static final Logger logger = LoggerFactory.getLogger(CityController.class);
	
	@Autowired
    CityService cityService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	MiscUtils miscUtils;

	/*
	 * Devuelve todas las ciudades registradas en formato JSON.
	 */
	@RequestMapping(
			value = { "/cities" },
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	public ResponseEntity<?> listCities() {
		logger.info("ListCities - GET - Processing request for a list with all existing cities.");
        try {
        	List<City> cities = cityService.getAllCities();
            
    		if (!cities.isEmpty()) {
            	logger.info("ListCities - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<List<City>>(cities, HttpStatus.OK);
            }
            else {
            	logger.warn("ListCities - GET - Request failed - No cities were found.");
            	return new ResponseEntity<List<City>>(HttpStatus.NO_CONTENT);
            }
        }
		catch (Exception e) {
			logger.error("ListCities - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("City","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Devuelve la ciudad solicitada en formato JSON.
	 */
	@RequestMapping(
			value = { "/cities/{id}" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	public ResponseEntity<?> getCity(@PathVariable("id") String id) {
		id = id.toLowerCase();
		logger.info("GetCity - GET - Processing request for city <{}>.", id);
        try {
        	City city = cityService.getCityById(id);
            
    		if (city != null) {
            	logger.info("GetCity - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<City>(city, HttpStatus.OK);
            }
            else {
            	logger.warn("GetCity - GET - Request failed - City with id <{}> not found.", id);
                FieldError error = new FieldError("City","error",
                		messageSource.getMessage("city.doesnt.exist", new String[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
            } 
        }
        catch (Exception e) {
			logger.error("GetCity - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("City","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
}
