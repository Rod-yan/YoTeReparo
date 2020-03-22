package com.yotereparo.controller;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yotereparo.model.Requirement;
import com.yotereparo.service.RequirementService;
import com.yotereparo.util.MiscUtils;
/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Requerimientos.
 * 
 * @author Rodrigo Yanis
 * 
 */
@RestController
public class RequirementController {
	
	private static final Logger logger = LogManager.getLogger(RequirementController.class);
	
	@Autowired
    RequirementService requirementService;
	@Autowired
    MessageSource messageSource;

	/*
	 * Devuelve todos los Requerimientos registradas en formato JSON.
	 */
	@RequestMapping(
			value = { "/requirements" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<?> listRequirements() {
		logger.info("ListRequirements - GET - Processing request for a list with all existing requirements.");
        try {
        	List<Requirement> requirements = requirementService.getAllRequirements();
            
    		if (!requirements.isEmpty()) {
            	logger.info("ListRequirements - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<List<Requirement>>(requirements, HttpStatus.OK);
            }
            else {
            	logger.info("ListRequirements - GET - Request failed - No requirements were found.");
            	return new ResponseEntity<List<Requirement>>(HttpStatus.NO_CONTENT);
            }
        }
		catch (Exception e) {
			logger.error("ListRequirements - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Requirement","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Devuelve el Requerimiento solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/requirements/{id}" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<?> getRequirement(@PathVariable("id") Integer id) {
		logger.info(String.format("GetRequirement - GET - Processing request for requirement <%s>.", id));
        try {
        	Requirement requirement = requirementService.getRequirementById(id);
            
    		if (requirement != null) {
            	logger.info("GetRequirement - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<Requirement>(requirement, HttpStatus.OK);
            }
            else {
            	logger.info(String.format("GetRequirement - GET - Request failed - Requirement with id <%s> not found.", id));
                FieldError error = new FieldError("Requirement","error",messageSource.getMessage("requirement.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
            } 
        }
        catch (Exception e) {
			logger.error("GetRequirement - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Requirement","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
}
