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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.controller.dto.UserPasswordChangeDto;
import com.yotereparo.controller.dto.validation.UserValidation;
import com.yotereparo.controller.mapping.UserMapper;
import com.yotereparo.model.User;
import com.yotereparo.service.UserService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.error.CustomResponseError;

/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Usuarios.
 * 
 * @author Rodrigo Yanis
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
    UserService userService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	UserValidation userValidation;
	@Autowired
	UserMapper userMapper;
	@Autowired
	MiscUtils miscUtils;

	/*
	 * Devuelve todos los usuarios registrados en formato JSON.
	 */
	@RequestMapping(
			value = { "/users" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMINISTRATOR')")
	public ResponseEntity<?> listUsers() {
		logger.info("ListUsers - GET - Processing request for a list with all existing users.");
        try {
			List<User> users = userService.getAllUsers();
	        		
			if (users != null && !users.isEmpty()) {
				
				List<UserDto> usersDto = users.stream()
		                .map(user -> userMapper.convertToDto(user))
		                .collect(Collectors.toList());
				
	        	logger.info("ListUsers - GET - Exiting method, providing response resource to client.");
	            return new ResponseEntity<List<UserDto>>(usersDto, HttpStatus.OK);
	        }
	        else {
	        	logger.warn("ListUsers - GET - Request failed - No users were found.");
	        	return new ResponseEntity<List<UserDto>>(HttpStatus.NO_CONTENT);
	        }
        }
		catch (Exception e) {
			logger.error("ListUsers - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Devuelve el usuario solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/users/{id}" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	@PreAuthorize("authentication.principal.username.equalsIgnoreCase(#id) or hasAuthority('ADMINISTRATOR')")
	public ResponseEntity<?> getUser(@PathVariable("id") String id) {
		logger.info("GetUser - GET - Processing request for user <{}>.", id);
        try {
        	User user = userService.getUserById(id.toLowerCase());
            
    		if (user != null) {
            	logger.info("GetUser - GET - Exiting method, providing response resource to client.");
                return new ResponseEntity<UserDto>(userMapper.convertToDto(user), HttpStatus.OK);
            }
            else {
            	logger.warn("GetUser - GET - Request failed - User with id <{}> not found.", id);
                FieldError error = new FieldError("User","error",
                		messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
            } 
        }
        catch (Exception e) {
			logger.error("GetUser - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Crea un usuario con los valores del JSON payload recibido.
	 */
	@RequestMapping(
			value = { "/users", "/auth/signup" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody UserDto clientInput, UriComponentsBuilder ucBuilder, BindingResult result) {
		logger.info("CreateUser - POST - Processing request for user <{}>.", clientInput.getId().toLowerCase());
		try {
			if (!userValidation.validateRequest(clientInput, result).hasErrors()) {
				if (userService.getUserById(clientInput.getId()) == null) {
					userService.createUser(userMapper.convertToEntity(clientInput));
					
					HttpHeaders headers = new HttpHeaders();
			        headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(clientInput.getId()).toUri());
			        
			        logger.info("CreateUser - POST - Exiting method, providing response resource to client.");
					return new ResponseEntity<>(headers, HttpStatus.CREATED);
		        }
				else {
					logger.warn("CreateUser - POST - Request failed - Unable to create user. User <{}> already exist.", 
							clientInput.getId());
		            FieldError error = new FieldError("User","error",
		            		messageSource.getMessage("user.already.exist", new String[]{clientInput.getId()}, Locale.getDefault()));
		            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.CONFLICT);
				}
			}
			else {
				logger.warn("CreateUser - POST - Request failed - Input validation error(s) detected.");
				return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
			}
		}
		catch (CustomResponseError e) {
			logger.warn("CreateUser - POST - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("CreateUser - POST - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Actualiza los atributos del usuario con los valores recibidos en el JSON payload. 
	 * Si estos no se incluyen en el request body entonces se considera que se está intentando vaciar su valor. 
	 * Esto es legal solo para atributos no mandatorios en la entidad.
	 */
	@RequestMapping(
			value = { "/users/{id}" },
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.PUT)
	@PreAuthorize("authentication.principal.username.equalsIgnoreCase(#id) or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @RequestBody UserDto clientInput, BindingResult result) {
		logger.info("UpdateUser - PUT - Processing request for user <{}>.", id);
		try {
			clientInput.setId(id);
			if (userService.getUserById(id) != null) {
				if (!userValidation.validateRequest(clientInput, result).hasErrors()) {
					userService.updateUser(userMapper.convertToEntity(clientInput));
					
					logger.info("UpdateUser - PUT - Exiting method, providing response resource to client.");
					return new ResponseEntity<UserDto>(userMapper.convertToDto(userService.getUserById(id)), HttpStatus.OK);
				}
				else {
					logger.warn("UpdateUser - PUT - Request failed - Input validation error(s) detected.");
					return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
				}
	        }
			else {
				logger.warn("UpdateUser - PUT - Request failed - Unable to update user. User <{}> doesn't exist.", id);
	            FieldError error = new FieldError("User","error",
	            		messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (CustomResponseError e) {
			logger.warn("UpdateUser - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("UpdateUser - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Actualiza la contraseña del usuario.
	 */
	@RequestMapping(
			value = { "/users/{id}/password" },
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.PUT)
    public ResponseEntity<?> changeUserPassword(@PathVariable("id") String id, @RequestBody UserPasswordChangeDto clientInput, BindingResult result) {
		logger.info("ChangeUserPassword - PUT - Processing request for user <{}>.", id);
		try {
			if (userService.getUserById(id) != null) {
				if (!userValidation.validateRequest(clientInput, result).hasErrors()) {
					userService.changeUserPasswordById(id, clientInput.getContrasenaActual(), clientInput.getContrasenaNueva());
					
					logger.info("ChangeUserPassword - PUT - Exiting method, providing response resource to client.");
					return new ResponseEntity<UserPasswordChangeDto>(HttpStatus.OK);
				}
				else {
					logger.warn("ChangeUserPassword - PUT - Request failed - Input validation error(s) detected.");
					return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
				}
	        }
			else {
				logger.warn("ChangeUserPassword - PUT - Request failed - Unable to update user. User <{}> doesn't exist.", id);
	            FieldError error = new FieldError("User","error",
	            		messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (CustomResponseError e) {
			logger.warn("ChangeUserPassword - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("ChangeUserPassword - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }

	/*
	 * Elimina físicamente un usuario.
	 */
	@RequestMapping(
			value = { "/users/{id}" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
		id = id.toLowerCase();
		logger.info("DeleteUser - DELETE - Processing request for user <{}>.", id);
		try {
			if (userService.getUserById(id) != null) {
	        	userService.deleteUserById(id);
	        	
	        	logger.info("DeleteUser - DELETE - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("DeleteUser - DELETE - Request failed - Unable to delete user. User <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("User","error",
	        			messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (Exception e) {
			logger.error("DeleteUser - DELETE - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Obtiene la foto del usuario, o el thumbnail de la misma, 
	 * de acuerdo con el URI path al que se suscriba el request.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo", "/users/{id}/photo/thumbnail" }, 
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.GET)
	@PreAuthorize("authentication.principal.username.equalsIgnoreCase(#id) or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getUserPhoto(@PathVariable("id") String id) {
		id = id.toLowerCase();
		logger.info("GetUserPhoto - GET - Processing request for user's <{}> photo.", id);
		try {
			User user = userService.getUserById(id);
			if (user != null) {			
				// Evaluamos el uri path del request para determinar si vamos a estar trabajando 
				// con la foto o con el thumbnail
				String requestUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
				byte[] userPhoto;
	 			if (requestUri.contains("thumbnail")) {
	 				userPhoto = user.getThumbnail();
	 			}
				else {
					userPhoto = user.getFoto();
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
					logger.warn("GetUserPhoto - GET - Request failed - Unable to fetch user's photo. "
							+ "No photo was found for user <{}>.", id);
		        	FieldError error = new FieldError("User","foto",
		        			messageSource.getMessage("user.doesnt.have.photo", new String[]{id}, Locale.getDefault()));
		        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
				}
	        }
			else {
				logger.warn("GetUserPhoto - GET - Request failed - Unable to fetch user's photo. User <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("User","error",
	        			messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			logger.error("GetUserPhoto - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
	}
	
	/*
	 * Actualiza la foto (y su thumbnail) del usuario. 
	 * El JSON payload es la foto codificada en un string base64.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.PUT)
	@PreAuthorize("authentication.principal.username.equalsIgnoreCase(#id) or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> updateUserPhoto(@PathVariable("id") String id, @RequestBody ObjectNode photoPayload) {
		id = id.toLowerCase();
		logger.info("UpdateUserPhoto - PUT - Processing request for user's <{}> photo.", id);
		// parseamos el json object recibido y generamos el byte array validando la estructura del request al mismo tiempo.
		try { 
			JsonNode jsonPhotoPayload = photoPayload.get("foto");
			if (jsonPhotoPayload != null) {
				byte[] b64photo = jsonPhotoPayload.asText().getBytes();
				if (userService.getUserById(id) != null) {
					userService.updateUserPhotoById(id, Base64.getDecoder().decode(b64photo));
					
					logger.info("UpdateUserPhoto - PUT - Exiting method, providing response resource to client.");
					return new ResponseEntity<String>(HttpStatus.OK);
		        }
				else {
					logger.warn("UpdateUserPhoto - PUT - Request failed - Unable to update user's photo. "
							+ "User <{}> doesn't exist.", id);
		        	FieldError error = new FieldError("User","error",
		        			messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
		            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
				}
			}
			else {
				logger.warn("UpdateUserPhoto - PUT - Request failed - Received malformed request, returning error to client.");
	        	FieldError error = new FieldError("User","foto",
	        			messageSource.getMessage("format.mismatch", null, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.BAD_REQUEST);
			}
		}
		catch (IllegalArgumentException e){
			logger.warn("UpdateUserPhoto - PUT - Request failed - Received invalid base64 image, returning error to client.");
        	FieldError error =new FieldError("User","foto",
        			messageSource.getMessage("invalid.base64.image", null, Locale.getDefault()));
        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("UpdateUserPhoto - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	/*
	 * Elimina la foto y el thumbnail del usuario.
	 */
	@RequestMapping(
			value = { "/users/{id}/photo" },
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.DELETE)
	@PreAuthorize("authentication.principal.username.equalsIgnoreCase(#id) or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> deleteUserPhoto(@PathVariable("id") String id) {
		id = id.toLowerCase();
		logger.info("DeleteUserPhoto - DELETE - Processing request for user's <{}> photo.", id);
		try {
			if (userService.getUserById(id) != null) {
				userService.updateUserPhotoById(id, null);
				
				logger.info("DeleteUserPhoto - DELETE - Exiting method, providing response resource to client.");
				return new ResponseEntity<String>(HttpStatus.OK);
	        }
			else {
				logger.warn("DeleteUserPhoto - DELETE - Request failed - Unable to delete user's photo. "
						+ "User <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("User","error",
	        			messageSource.getMessage("user.doesnt.exist", new String[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (Exception e) {
			logger.error("DeleteUserPhoto - DELETE - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("User","error",
					messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
