package com.yotereparo.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.converter.ServiceConverter;
import com.yotereparo.controller.filter.ServiceFilter;
import com.yotereparo.model.Service;
import com.yotereparo.service.CityService;
import com.yotereparo.service.PaymentMethodService;
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
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ServiceController {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);
	
	@Autowired
    ServiceManager serviceManager;
	@Autowired
    UserService userService;
	@Autowired
    CityService cityService;
	@Autowired
    PaymentMethodService paymentMethodService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	ValidationUtils validationUtils;
	@Autowired
	ServiceConverter serviceConverter;
	@Autowired
	ServiceFilter supportedFilters;
	@Autowired
	MiscUtils miscUtils;

	/*
	 * Devuelve todos los servicios registrados en formato JSON.
	 */
	@RequestMapping(
			value = { "/services" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	public ResponseEntity<?> listServices(@RequestParam(required = false) Map<String,String> filters) {
		logger.info("ListServices - GET - Processing request for a list with all existing services.");
		try {
			List<Service> services = null;
			services = supportedFilters.contains(filters) ? serviceManager.getAllServices(filters) : serviceManager.getAllServices();
			
			if (services != null && !services.isEmpty()) {
				List<ServiceDto> servicesDto = services.stream()
		                .map(service -> serviceConverter.convertToDto(service))
		                .collect(Collectors.toList());
				
	        	logger.info("ListServices - GET - Exiting method, providing response resource to client.");
	            return new ResponseEntity<List<ServiceDto>>(servicesDto, HttpStatus.OK);
	        }
	        else {
	        	logger.warn("ListServices - GET - Request failed - No services were found.");
	        	return new ResponseEntity<List<ServiceDto>>(HttpStatus.NO_CONTENT);
	        }
		}
		catch (Exception e) {
			logger.error("ListServices - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Devuelve el servicio solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/services/{id}" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	public ResponseEntity<?> getService(@PathVariable("id") Integer id) {
		logger.info(String.format("GetService - GET - Processing request for service <%s>.", id));
        try {
        	Service service = serviceManager.getServiceById(id);
            
    		if (service != null) {
            	logger.info("GetService - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<ServiceDto>(serviceConverter.convertToDto(service), HttpStatus.OK);
            }
            else {
            	logger.warn(String.format("GetService - GET - Request failed - Service with id <%s> not found.", id));
                FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
			logger.error("GetService - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Crea un servicio con los valores del JSON payload recibido.
	 */
	@RequestMapping(
			value = { "/services" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.POST)
    public ResponseEntity<?> createService(@RequestBody ServiceDto clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {	
		logger.info(String.format("CreateService - POST - Processing request for service <%s>.", clientInput.getTitulo()));
		try {
			if (!validationUtils.serviceInputValidation(clientInput, result).hasErrors()) {
				Service service = serviceConverter.convertToEntity(clientInput);
				if (!serviceManager.similarExist(service)) {
					serviceManager.createService(service);
					
					HttpHeaders headers = new HttpHeaders();
			        headers.setLocation(ucBuilder.path("/services/{id}").buildAndExpand(service.getId()).toUri());
			        
			        logger.info("CreateService - POST - Exiting method, providing response resource to client.");
					return new ResponseEntity<>(headers, HttpStatus.CREATED);
				}
				else {
					logger.warn(String.format("CreateService - POST - Request failed - Unable to create service. Service <%s> is too similar to another service", service.getTitulo()));
		            FieldError error = new FieldError("Service","error",messageSource.getMessage("service.too.similar", new String[]{service.getTitulo()}, Locale.getDefault()));
		            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.CONFLICT);
				}
			}
			else {
				logger.warn("CreateService - POST - Request failed - Input validation error(s) detected.");
				return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
			}
        }
		catch (CustomResponseError e) {
			logger.warn("CreateService - POST - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("CreateService - POST - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Actualiza los atributos del servicio con los valores recibidos en el JSON payload. 
	 * Si estos no se incluyen en el request body entonces se considera que se está intentando vaciar su valor. 
	 * Esto es legal solo para atributos no mandatorios en la entidad.
	 * Las imagenes del servicio seran ignoradas por este método (Ignorados, no ilegales).
	 */
	@RequestMapping(
			value = { "/services/{id}" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.PUT)
    public ResponseEntity<?> updateService(@PathVariable("id") Integer id, @RequestBody ServiceDto clientInput, BindingResult result) {	
		logger.info(String.format("UpdateService - PUT - Processing request for service <%s>.", id));
		try {
			clientInput.setId(id);
			if (serviceManager.getServiceById(id) != null) {
				if (!validationUtils.serviceInputValidation(clientInput, result).hasErrors()) {
					Service service = serviceConverter.convertToEntity(clientInput);
					Boolean serviceIsRegisteredToUser = false;
					for (Service s : service.getUsuarioPrestador().getServicios())
						if (s.getId().equals(id)) {
							serviceIsRegisteredToUser = true;
							break;
						}
					if (serviceIsRegisteredToUser)
						if (!serviceManager.similarExist(service)) {
							serviceManager.updateService(service);
							
							logger.info("UpdateService - PUT - Exiting method, providing response resource to client.");
							return new ResponseEntity<ServiceDto>(serviceConverter.convertToDto(serviceManager.getServiceById(id)), HttpStatus.OK);
						}
						else {
							logger.warn(String.format("UpdateService - PUT - Request failed - Unable to update service. Service <%s> is too similar to another service", service.getTitulo()));
				            FieldError error = new FieldError("Service","error",messageSource.getMessage("service.too.similar", new String[]{service.getTitulo()}, Locale.getDefault()));
				            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.CONFLICT);
						}
					else {
						logger.warn(String.format("UpdateService - PUT - Request failed - Unable to update service. Service <%s> doesn't belong to user <%s>.", id, service.getUsuarioPrestador().getId()));
						FieldError error = new FieldError("Service","servicios",messageSource.getMessage("service.doesnt.belong.to.user", new Integer[]{service.getId()}, Locale.getDefault()));
						return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.BAD_REQUEST);
					}
				}
				else {
					logger.warn("UpdateService - PUT - Request failed - Input validation error(s) detected.");
					return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
				}
	        }
			else {
				logger.warn(String.format("UpdateService - PUT - Request failed - Unable to update service. Service <%s> doesn't exist.", id));
	            FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (CustomResponseError e) {
			logger.warn("UpdateService - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("UpdateService - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Habilita un servicio.
	 */
	@RequestMapping(
			value = { "/services/{id}/enable" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
    public ResponseEntity<?> enableService(@PathVariable("id") Integer id) {
		logger.info(String.format("EnableService - PUT - Processing request for service <%s>.", id));
		try {
			if (serviceManager.getServiceById(id) != null) {
				serviceManager.enableServiceById(id);
	        	
	        	logger.info("EnableService - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn(String.format("EnableService - PUT - Request failed - Unable to enable service. Service <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (Exception e) {
			logger.error("EnableService - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Deshabilita un servicio.
	 */
	@RequestMapping(
			value = { "/services/{id}/disable" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
    public ResponseEntity<?> disableService(@PathVariable("id") Integer id) {
		logger.info(String.format("DisableService - PUT - Processing request for service <%s>.", id));
		try {
			if (serviceManager.getServiceById(id) != null) {
				serviceManager.disableServiceById(id);
	        	
	        	logger.info("DisableService - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn(String.format("DisableService - PUT - Request failed - Unable to disable service. Service <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (Exception e) {
			logger.error("DisableService - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Elimina físicamente un servicio.
	 */
	@RequestMapping(
			value = { "/services/{id}" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteService(@PathVariable("id") Integer id) {
		logger.info(String.format("DeleteService - DELETE - Processing request for service <%s>.", id));
		try {
			if (serviceManager.getServiceById(id) != null) {
				serviceManager.deleteServiceById(id);
	        	
	        	logger.info("DeleteService - DELETE - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn(String.format("DeleteService - DELETE - Request failed - Unable to delete service. Service <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (Exception e) {
			logger.error("DeleteService - DELETE - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Obtiene la imagen del servicio, o el thumbnail de la misma, 
	 * de acuerdo con el URI path al que se suscriba el request.
	 */
	@RequestMapping(
			value = { "/services/{id}/photo", "/services/{id}/photo/thumbnail" }, 
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.GET)
    public ResponseEntity<?> getServiceImage(@PathVariable("id") Integer id) {
		logger.info(String.format("GetServiceImage - GET - Processing request for service's <%s> image.", id));
		try {
			Service service = serviceManager.getServiceById(id);
			if (service != null) {			
				// evaluamos el uri path del request para determinar si vamos a estar trabajando con la foto o con el thumbnail
				String requestUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
				byte[] serviceImage;
	 			if (requestUri.contains("thumbnail")) {
	 				serviceImage = service.getThumbnail();
	 			}
				else {
					serviceImage = service.getImagen();
				}
	 			// procesamos el request
	 			if (serviceImage != null) {
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
			        InputStream is = new ByteArrayInputStream(serviceImage);
			        BufferedImage img = ImageIO.read(is);
			        ImageIO.write(img, "png", bao);
			        
			        HttpHeaders headers = new HttpHeaders();
					headers.setCacheControl(CacheControl.noCache().getHeaderValue());
					headers.setContentType(MediaType.IMAGE_PNG);
					
					serviceImage = bao.toByteArray();
					bao.close();
					img.flush();
					
					logger.info("GetServiceImage - GET - Exiting method, providing response resource to client.");
					return new ResponseEntity<byte[]>(serviceImage, headers, HttpStatus.OK);
				}
				else {
					logger.warn(String.format("GetServiceImage - GET - Request failed - Unable to fetch service's image. No image was found for service <%s>.", id));
		        	FieldError error = new FieldError("Service","imagen",messageSource.getMessage("service.doesnt.have.image", new Integer[]{id}, Locale.getDefault()));
		        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
				}
	        }
			else {
				logger.warn(String.format("GetServiceImage - GET - Request failed - Unable to fetch service's image. Service <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			logger.error("GetServiceImage - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
	}
	
	/*
	 * Actualiza la imagen (y su thumbnail) del servicio. 
	 * El JSON payload es la foto codificada en un string base64.
	 */
	@RequestMapping(
			value = { "/services/{id}/photo" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.PUT)
    public ResponseEntity<?> updateServiceImage(@PathVariable("id") Integer id, @RequestBody ObjectNode photoPayload) {
		logger.info(String.format("UpdateServiceImage - PUT - Processing request for service's <%s> image.", id));
		// parseamos el json object recibido y generamos el byte array validando la estructura del request al mismo tiempo.
		try { 
			JsonNode jsonPhotoPayload = photoPayload.get("foto");
			if (jsonPhotoPayload != null) {
				byte[] b64photo = jsonPhotoPayload.asText().getBytes();
				if (serviceManager.getServiceById(id) != null) {
					serviceManager.updateServiceImageById(id, Base64.getDecoder().decode(b64photo));
					
					logger.info("UpdateServiceImage - PUT - Exiting method, providing response resource to client.");
					return new ResponseEntity<String>(HttpStatus.OK);
		        }
				else {
					logger.warn(String.format("UpdateServiceImage - PUT - Request failed - Unable to update service's image. Service <%s> doesn't exist.", id));
		        	FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
		            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
				}
			}
			else {
				logger.warn("UpdateServiceImage - PUT - Request failed - Received malformed request, returning error to client.");
	        	FieldError error = new FieldError("Service","foto",messageSource.getMessage("format.mismatch", null, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.BAD_REQUEST);
			}
		}
		catch (IllegalArgumentException e){
			logger.warn("UpdateServiceImage - PUT - Request failed - Received invalid base64 image, returning error to client.");
        	FieldError error =new FieldError("Service","foto",messageSource.getMessage("invalid.base64.image", null, Locale.getDefault()));
        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("UpdateServiceImage - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	/*
	 * Elimina la foto y el thumbnail del usuario.
	 */
	@RequestMapping(
			value = { "/services/{id}/photo" },
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteServiceImage(@PathVariable("id") Integer id) {
		logger.info(String.format("DeleteServiceImage - DELETE - Processing request for service's <%s> image.", id));
		try {
			if (serviceManager.getServiceById(id) != null) {
				serviceManager.updateServiceImageById(id, null);
				
				logger.info("DeleteServiceImage - DELETE - Exiting method, providing response resource to client.");
				return new ResponseEntity<String>(HttpStatus.OK);
	        }
			else {
				logger.warn(String.format("DeleteServiceImage - DELETE - Request failed - Unable to delete service's image. Service <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("Service","error",messageSource.getMessage("service.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			logger.error("DeleteServiceImage - DELETE - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Service","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
