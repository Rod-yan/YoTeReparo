package com.yotereparo.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.CacheControl;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.controller.dto.converter.UserConverter;
import com.yotereparo.model.User;
import com.yotereparo.service.UserService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.ValidationUtils;
import com.yotereparo.util.error.CustomResponseError;

/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Usuarios.
 * 
 * @author Rodrigo Yanis
 * 
 */
@RestController
public class UserController {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);
	
	@Autowired
    UserService userService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	UserConverter userConverter;

	/*
	 * Devuelve todos los usuarios registrados en formato JSON.
	 */
	@RequestMapping(
			value = { "/users/" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<?> listUsers() {
		logger.info("ListUsers - GET - Processing request for a list with all existing users.");
        try {
			List<User> users = userService.getAllUsers();
	        		
			if (!users.isEmpty()) {
				
				List<UserDto> usersDto = users.stream()
		                .map(user -> userConverter.convertToDto(user))
		                .collect(Collectors.toList());
				
	        	logger.info("ListUsers - GET - Exiting method, providing response resource to client.");
	            return new ResponseEntity<List<UserDto>>(usersDto, HttpStatus.OK);
	        }
	        else {
	        	logger.info("ListUsers - GET - Request failed - No users were found.");
	        	return new ResponseEntity<List<UserDto>>(HttpStatus.NO_CONTENT);
	        }
        }
		catch (Exception e) {
			logger.error(String.format("ListUsers - GET - Request failed - Error procesing request: <%s>", e.getMessage()));
			FieldError error = new FieldError("User","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Devuelve el usuario solicitado en formato JSON.
	 * TODO: Definir si queremos incluir foto y thumbnail en esta response (mucho overhead).
	 */
	@RequestMapping(
			value = { "/users/{id}" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("id") String id) {
		logger.info(String.format("GetUser - GET - Processing request for user <%s>.", id));
        try {
        	User user = userService.getUserById(id);
            
    		if (user != null) {
            	logger.info("GetUser - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<UserDto>(userConverter.convertToDto(user), HttpStatus.OK);
            }
            else {
            	logger.info(String.format("GetUser - GET - Request failed - User with id <%s> not found.", id));
                FieldError error = new FieldError("User","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
            } 
        }
        catch (Exception e) {
			logger.error(String.format("GetUser - GET - Request failed - Error procesing request: <%s>", e.getMessage()));
			FieldError error = new FieldError("User","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Crea un usuario con los valores del JSON payload recibido.
	 */
	@RequestMapping(
			value = { "/users/" }, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody UserDto clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {
		logger.info(String.format("CreateUser - POST - Processing request for user <%s>.", clientInput.getId()));
		try {
			if (!ValidationUtils.userInputValidation(clientInput, result).hasErrors()) {
				if (!userService.exist(clientInput.getId())) {
					userService.createUser(userConverter.convertToEntity(clientInput));
					
					HttpHeaders headers = new HttpHeaders();
			        headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(clientInput.getId()).toUri());
			        
			        logger.info("CreateUser - POST - Exiting method, providing response resource to client.");
					return new ResponseEntity<>(headers, HttpStatus.CREATED);
		        }
				else {
					logger.info(String.format("CreateUser - POST - Request failed - Unable to create user. User <%s> already exist.", clientInput.getId()));
		            FieldError error = new FieldError("User","error",messageSource.getMessage("user.already.exist", new String[]{clientInput.getId()}, Locale.getDefault()));
		            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.CONFLICT);
				}
			}
			else {
				logger.info("CreateUser - POST - Request failed - Input validation error(s) detected.");
				return new ResponseEntity<>(MiscUtils.getFormatedResponseErrorList(result).toString(), HttpStatus.BAD_REQUEST);
			}
		}
		catch (CustomResponseError error) {
			logger.error(String.format("CreateUser - POST - Request failed - Error procesing request: <%s>", error.toString()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (Exception e) {
			logger.error(String.format("CreateUser - POST - Request failed - Error procesing request: <%s>", e.getMessage()));
			FieldError error = new FieldError("User","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Actualiza los atributos del usuario con los valores recibidos en el JSON payload. 
	 * Si estos no se incluyen en el request body entonces se considera que se está intentando vaciar su valor. 
	 * Esto es legal solo para atributos no mandatorios en la entidad.
	 * La foto y el thumbnail del usuario serán ignorados por este método (Ignorados, no ilegales).
	 */
	@RequestMapping(
			value = { "/users/{id}" },
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @RequestBody UserDto clientInput, BindingResult result) {
		logger.info(String.format("UpdateUser - PUT - Processing request for user <%s>.", id));
		try {
			clientInput.setId(id);

			if (userService.exist(id)) {
				if (!ValidationUtils.userInputValidation(clientInput, result).hasErrors()) {
					userService.updateUser(userConverter.convertToEntity(clientInput));
					
					logger.info("UpdateUser - PUT - Exiting method, providing response resource to client.");
					return new ResponseEntity<UserDto>(userConverter.convertToDto(userService.getUserById(id)), HttpStatus.OK);
				}
				else {
					logger.info("UpdateUser - PUT - Request failed - Input validation error(s) detected.");
					return new ResponseEntity<>(MiscUtils.getFormatedResponseErrorList(result).toString(), HttpStatus.BAD_REQUEST);
				}
	        }
			else {
				logger.info(String.format("UpdateUser - PUT - Request failed - Unable to update user. User <%s> doesn't exist.", id));
	            FieldError error = new FieldError("User","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
			}
		}
		catch (CustomResponseError error) {
			logger.error(String.format("UpdateUser - PUT - Request failed - Error procesing request: <%s>", error.toString()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (Exception e) {
			logger.error(String.format("UpdateUser - PUT - Request failed - Error procesing request: <%s>", e.getMessage()));
			FieldError error = new FieldError("User","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }

	/*
	 * Elimina físicamente un usuario.
	 */
	@RequestMapping(
			value = { "/users/{id}" }, 
			produces = MediaType.APPLICATION_JSON_VALUE,			
			method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
		logger.info(String.format("DeleteUser - DELETE - Processing request for user <%s>.", id));
		try {
			if (userService.exist(id)) {
	        	userService.deleteUserById(id);
	        	
	        	logger.info("DeleteUser - DELETE - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.info(String.format("DeleteUser - DELETE - Request failed - Unable to delete user. User <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("User","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
	        }
		}
		catch (Exception e) {
			logger.error(String.format("DeleteUser - DELETE - Request failed - Error procesing request: <%s>", e.getMessage()));
			FieldError error = new FieldError("User","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Obtiene la foto del usuario, o el thumbnail de la misma, 
	 * de acuerdo con el URI path al que se suscriba el request.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo", "/users/{id}/photo/thumbnail" }, 
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
    public ResponseEntity<?> getUserPhoto(@PathVariable("id") String id) {
		logger.info(String.format("GetUserPhoto - GET - Processing request for user's <%s> photo.", id));
		try {
			if (userService.exist(id)) {			
				// evaluamos el uri path del request para determinar si vamos a estar trabajando con la foto o con el thumbnail
				String requestUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
				byte[] userPhoto;
	 			if (requestUri.contains("thumbnail")) {
	 				userPhoto = userService.getUserById(id).getThumbnail();
	 			}
				else {
					userPhoto = userService.getUserById(id).getFoto();
				}
	 			// procesamos el request
	 			if (userPhoto != null) {
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
			        InputStream is = new ByteArrayInputStream(userPhoto);
			        BufferedImage img = ImageIO.read(is);
			        ImageIO.write(img, "png", bao);
			        
			        HttpHeaders headers = new HttpHeaders();
					headers.setCacheControl(CacheControl.noCache().getHeaderValue());
					headers.setContentType(MediaType.IMAGE_PNG);
					
					userPhoto = bao.toByteArray();
					bao.close();
					img.flush();
					
					logger.info("GetUserPhoto - GET - Exiting method, providing response resource to client.");
					return new ResponseEntity<byte[]>(userPhoto, headers, HttpStatus.OK);
				}
				else {
					logger.info(String.format("GetUserPhoto - GET - Request failed - Unable to fetch user's photo. No photo was found for user <%s>.", id));
		        	FieldError error = new FieldError("User","foto",messageSource.getMessage("user.doesnt.have.photo", new String[]{id}, Locale.getDefault()));
		        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
				}
	        }
			else {
				logger.info(String.format("GetUserPhoto - GET - Request failed - Unable to fetch user's photo. User <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("User","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			logger.error(String.format("GetUserPhoto - GET - Request failed - Error procesing request: <%s>", e.getMessage()));
			FieldError error = new FieldError("User","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
	}
	
	/*
	 * Actualiza la foto (y su thumbnail) del usuario. 
	 * El JSON payload es la foto codificada en un string base64.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo" }, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserPhoto(@PathVariable("id") String id, @RequestBody String photoPayload) {
		logger.info(String.format("UpdateUserPhoto - PUT - Processing request for user's <%s> photo.", id));
		
		// parseamos el json object recibido y generamos el byte array validando la estructura del request al mismo tiempo.
		try { 
			JSONObject jsonPhotoPayload = new JSONObject(photoPayload);
			byte[] b64photo = jsonPhotoPayload.getString("foto").getBytes();
			if (userService.exist(id)) {
				userService.updateUserPhotoById(id, Base64.getDecoder().decode(b64photo));
				
				logger.info("UpdateUserPhoto - PUT - Exiting method, providing response resource to client.");
				return new ResponseEntity<String>(HttpStatus.OK);
	        }
			else {
				logger.info(String.format("UpdateUserPhoto - PUT - Request failed - Unable to update user's photo. User <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("User","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
			}
		}
		catch (IllegalArgumentException e){
			logger.error("UpdateUserPhoto - PUT - Request failed - Received invalid base64 image, returning error to client.");
        	FieldError error =new FieldError("User","error",messageSource.getMessage("invalid.base64.image", null, Locale.getDefault()));
        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.BAD_REQUEST);
		}
		catch (JSONException e) {
			logger.error("UpdateUserPhoto - PUT - Request failed - Received malformed request, returning error to client.");
        	FieldError error = new FieldError("User","error",messageSource.getMessage("format.mismatch", null, Locale.getDefault()));
        	return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error(String.format("UpdateUserPhoto - PUT - Request failed - Error procesing request: <%s>", e.getMessage()));
			FieldError error = new FieldError("User","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	/*
	 * Elimina la foto y el thumbnail del usuario.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo" },
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserPhoto(@PathVariable("id") String id) {
		logger.info(String.format("DeleteUserPhoto - DELETE - Processing request for user's <%s> photo.", id));
		try {
			if (userService.exist(id)) {
				userService.updateUserPhotoById(id, null);
				
				logger.info("DeleteUserPhoto - DELETE - Exiting method, providing response resource to client.");
				return new ResponseEntity<String>(HttpStatus.OK);
	        }
			else {
				logger.info(String.format("DeleteUserPhoto - DELETE - Request failed - Unable to delete user's photo. User <%s> doesn't exist.", id));
	        	FieldError error = new FieldError("User","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			logger.error(String.format("DeleteUserPhoto - DELETE - Request failed - Error procesing request: <%s>", e.getMessage()));
			FieldError error = new FieldError("User","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(MiscUtils.getFormatedResponseError(error).toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
