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

import com.yotereparo.model.PaymentMethod;
import com.yotereparo.service.PaymentMethodService;
import com.yotereparo.util.MiscUtils;
/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Medios de Pago.
 * 
 * @author Rodrigo Yanis
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class PaymentMethodController {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentMethodController.class);
	
	@Autowired
    PaymentMethodService paymentMethodService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	MiscUtils miscUtils;

	/*
	 * Devuelve todos los Medios de Pago registradas en formato JSON.
	 */
	@RequestMapping(
			value = { "/paymentmethods" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	public ResponseEntity<?> listPaymentMethods() {
		logger.info("ListPaymentMethods - GET - Processing request for a list with all existing payment methods.");
        try {
        	List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods();
            
    		if (!paymentMethods.isEmpty()) {
            	logger.info("ListPaymentMethods - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<List<PaymentMethod>>(paymentMethods, HttpStatus.OK);
            }
            else {
            	logger.warn("ListPaymentMethods - GET - Request failed - No payment methods were found.");
            	return new ResponseEntity<List<PaymentMethod>>(HttpStatus.NO_CONTENT);
            }
        }
		catch (Exception e) {
			logger.error("ListPaymentMethods - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("PaymentMethod","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Devuelve el Medio de Pago solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/paymentmethods/{id}" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	public ResponseEntity<?> getPaymentMethod(@PathVariable("id") Integer id) {
		logger.info(String.format("GetPaymentMethod - GET - Processing request for payment method <%s>.", id));
        try {
        	PaymentMethod paymentMethod = paymentMethodService.getPaymentMethodById(id);
            
    		if (paymentMethod != null) {
            	logger.info("GetPaymentMethod - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<PaymentMethod>(paymentMethod, HttpStatus.OK);
            }
            else {
            	logger.warn(String.format("GetPaymentMethod - GET - Request failed - Payment Method with id <%s> not found.", id));
                FieldError error = new FieldError("PaymentMethod","error",messageSource.getMessage("paymentMethod.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
            } 
        }
        catch (Exception e) {
			logger.error("GetPaymentMethod - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("PaymentMethod","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
}
