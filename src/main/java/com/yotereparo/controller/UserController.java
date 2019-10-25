package com.yotereparo.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

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

import com.yotereparo.model.User;
import com.yotereparo.service.UserService;
import com.yotereparo.util.CustomResponseError;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.ValidationUtils;

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

	/*
	 * Devuelve todos los usuarios registrados en formato JSON.
	 */
	@RequestMapping(
			value = { "/users/" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<List<User>> listUsers() {
		logger.info("ListUsers - GET - Processing request for a list with all existing users.");
        
		List<User> users = userService.getAllUsers();
        
		if (!users.isEmpty()) {
        	logger.debug("ListUsers - GET - Exiting method, providing response resource to client.");
            return new ResponseEntity<List<User>>(users, HttpStatus.OK);
        }
        else {
        	logger.debug("ListUsers - GET - No users were found.");
        	return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
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
        
		User user = userService.getUserById(id);
        
		if (user != null) {
        	logger.debug("GetUser - GET - Exiting method, providing response resource to client.");
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }
        else {
        	logger.debug(String.format("GetUser - GET - User with id <%s> not found.", id));
            FieldError error =new FieldError("user","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
            return new ResponseEntity<>(new CustomResponseError(error.getObjectName(), 
            		error.getField(), 
            		error.getDefaultMessage()), 
            		HttpStatus.NOT_FOUND);
        } 
    }
	
	/*
	 * Crea un usuario con los valores del JSON payload recibido.
	 */
	@RequestMapping(
			value = { "/users/" }, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {
		
		if (!ValidationUtils.userInputValidation(clientInput, result).hasErrors()) {
			logger.info(String.format("CreateUser - POST - Processing request for user <%s>.", clientInput.getId()));
			if (!userService.exist(clientInput.getId())) {
				userService.createUser(clientInput);
				
				HttpHeaders headers = new HttpHeaders();
		        headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(clientInput.getId()).toUri());
		        
		        logger.debug("CreateUser - POST - Exiting method, providing response resource to client.");
				return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	        }
			else {
				logger.debug(String.format("CreateUser - POST - Unable to create user. User <%s> already exist.", clientInput.getId()));
	            FieldError error =new FieldError("user","error",messageSource.getMessage("user.already.exist", new String[]{clientInput.getId()}, Locale.getDefault()));
	            result.addError(error);
	            return new ResponseEntity<>(MiscUtils.getFormatedRequestErrorList(result), HttpStatus.CONFLICT);
			}
		}
		else {
			return new ResponseEntity<>(MiscUtils.getFormatedRequestErrorList(result), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @RequestBody User clientInput, BindingResult result) {
		logger.info(String.format("UpdateUser - PUT - Processing request for user <%s>.", id));
		
		clientInput.setId(id);
		
		if (userService.exist(id)) {
			if (!ValidationUtils.userInputValidation(clientInput, result).hasErrors()) {
				userService.updateUser(clientInput);
				
				logger.debug("UpdateUser - PUT - Exiting method, providing response resource to client.");
				return new ResponseEntity<User>(userService.getUserById(id), HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(MiscUtils.getFormatedRequestErrorList(result), HttpStatus.BAD_REQUEST);
			}
        }
		else {
			logger.debug(String.format("UpdateUser - PUT - Unable to update user. User <%s> doesn't exist.", id));
            FieldError error =new FieldError("user","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
            result.addError(error);
            return new ResponseEntity<>(MiscUtils.getFormatedRequestErrorList(result), HttpStatus.NOT_FOUND);
		}
    }

	/*
	 * Elimina físicamente un usuario.
	 */
	@RequestMapping(
			value = { "/users/{id}" }, 
			method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
		logger.info(String.format("DeleteUser - DELETE - Processing request for user <%s>.", id));
		
        if (userService.exist(id)) {
        	userService.deleteUserById(id);
        	
        	logger.debug("DeleteUser - DELETE - Exiting method, providing response resource to client.");
            return new ResponseEntity<User>(HttpStatus.OK);
        }
        else {
        	FieldError error =new FieldError("user","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
        	logger.debug(String.format("DeleteUser - DELETE - Unable to delete user. User <%s> doesn't exist.", id));
        	return new ResponseEntity<>(new CustomResponseError(error.getObjectName(), 
            		error.getField(), 
            		error.getDefaultMessage()), 
            		HttpStatus.NOT_FOUND);
        }
    }
	
	/*
	 * Obtiene la foto del usuario, o el thumbnail de la misma, 
	 * de acuerdo con el URI path al que se suscriba el request.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo", "/users/{id}/photo/thumbnail" }, 
			method = RequestMethod.GET)
    public ResponseEntity<?> getUserPhoto(@PathVariable("id") String id) {
		logger.info(String.format("GetUserPhoto - GET - Processing request for user's <%s> photo.", id));

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
				try {
			        InputStream is = new ByteArrayInputStream(userPhoto);
			        BufferedImage img = ImageIO.read(is);
			        ImageIO.write(img, "png", bao);
			        
			        HttpHeaders headers = new HttpHeaders();
					headers.setCacheControl(CacheControl.noCache().getHeaderValue());
					headers.setContentType(MediaType.IMAGE_PNG);
					
					userPhoto = bao.toByteArray();
					bao.close();
					img.flush();
					
					logger.debug("GetUserPhoto - GET - Exiting method, providing response resource to client.");
					return new ResponseEntity<byte[]>(userPhoto, headers, HttpStatus.OK);
			    } catch (IOException e) {
			        logger.error("GetUserPhoto - GET - IOException: "+e.getMessage());
			        throw new RuntimeException(e.getMessage());
			    }
			}
			else {
				logger.debug(String.format("GetUserPhoto - GET - Unable to fetch user's photo. No photo was found for user <%s>.", id));
	        	FieldError error =new FieldError("user","error",messageSource.getMessage("user.doesnt.have.photo", new String[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(new CustomResponseError(error.getObjectName(), 
	            		error.getField(), 
	            		error.getDefaultMessage()), 
	            		HttpStatus.NOT_FOUND);
			}
        }
		else {
			logger.debug(String.format("GetUserPhoto - GET - Unable to fetch user's photo. User <%s> doesn't exist.", id));
        	FieldError error =new FieldError("user","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
            return new ResponseEntity<>(new CustomResponseError(error.getObjectName(), 
            		error.getField(), 
            		error.getDefaultMessage()), 
            		HttpStatus.NOT_FOUND);
		}
	}
	
	/*
	 * Actualiza la foto (y su thumbnail) del usuario. 
	 * El JSON payload es la foto codificada en un string base64.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo" }, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserPhoto(@PathVariable("id") String id, @RequestBody String photoPayload) {
		logger.info(String.format("UpdateUserPhoto - PUT - Processing request for user's <%s> photo.", id));
		
		// parseamos el json object recibido y generamos el byte array validando la estructura del request al mismo tiempo.
		try { 
			JSONObject jsonPhotoPayload = new JSONObject(photoPayload);
			byte[] b64photo = jsonPhotoPayload.getString("foto").getBytes();
			if (userService.exist(id)) {
				try {
					userService.updateUserPhotoById(id, Base64.getDecoder().decode(b64photo));
					
					logger.debug("UpdateUserPhoto - PUT - Exiting method, providing response resource to client.");
					return new ResponseEntity<String>(HttpStatus.OK);
				}
				catch (IllegalArgumentException e){
					logger.error("UpdateUserPhoto - PUT - Received invalid base64 image, returning error to client.");
		        	FieldError error =new FieldError("user","error",messageSource.getMessage("invalid.base64.image", null, Locale.getDefault()));
		        	return new ResponseEntity<>(new CustomResponseError(error.getObjectName(), 
		            		error.getField(), 
		            		error.getDefaultMessage()), 
		            		HttpStatus.BAD_REQUEST);
				}
	        }
			else {
				logger.debug(String.format("UpdateUserPhoto - PUT - Unable to update user's photo. User <%s> doesn't exist.", id));
	        	FieldError error =new FieldError("user","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(new CustomResponseError(error.getObjectName(), 
	            		error.getField(), 
	            		error.getDefaultMessage()), 
	            		HttpStatus.NOT_FOUND);
			}
		}
		catch (JSONException e) {
			logger.error("UpdateUserPhoto - PUT - Received malformed request, returning error to client.");
        	FieldError error =new FieldError("user","error",messageSource.getMessage("format.mismatch", null, Locale.getDefault()));
        	return new ResponseEntity<>(new CustomResponseError(error.getObjectName(), 
            		error.getField(), 
            		error.getDefaultMessage()), 
            		HttpStatus.BAD_REQUEST);
		}
	}
	
	/*
	 * Elimina la foto y el thumbnail del usuario.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo" }, 
			method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserPhoto(@PathVariable("id") String id) {
		logger.info(String.format("DeleteUserPhoto - DELETE - Processing request for user's <%s> photo.", id));
		
		if (userService.exist(id)) {
			userService.updateUserPhotoById(id, null);
			
			logger.debug("DeleteUserPhoto - DELETE - Exiting method, providing response resource to client.");
			return new ResponseEntity<String>(HttpStatus.OK);
        }
		else {
			logger.debug(String.format("DeleteUserPhoto - DELETE - Unable to delete user's photo. User <%s> doesn't exist.", id));
        	FieldError error =new FieldError("user","error",messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
            return new ResponseEntity<>(new CustomResponseError(error.getObjectName(), 
            		error.getField(), 
            		error.getDefaultMessage()), 
            		HttpStatus.NOT_FOUND);
		}
	}
}
