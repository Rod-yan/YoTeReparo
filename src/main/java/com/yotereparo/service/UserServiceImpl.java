package com.yotereparo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.UserDaoImpl;
import com.yotereparo.model.Role;
import com.yotereparo.model.User;
import com.yotereparo.util.SecurityUtils;

/**
 * Capa de servicio para Usuarios. 
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Service("userService")
@Transactional 
public class UserServiceImpl implements UserService {
	
	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserDaoImpl dao;
	@Autowired
	private Environment environment;
	@Autowired
	private RoleService roleService;

	public void createUser(User user) {
		user.setSalt(SecurityUtils.saltGenerator());
		user.setContrasena(SecurityUtils.encryptPassword(user.getContrasena().concat(user.getSalt())));
		user.setFechaExpiracionContrasena(new DateTime().plusDays(Integer.parseInt(environment.getProperty("password.expiration.timeoffset.days"))));
		user.setFechaCreacion(new DateTime());
		// No cargamos imagenes en tiempo de creacion, siempre usar el metodo dedicado
		user.setFoto(null);
		user.setThumbnail(null);
		// Unstable:
		user.setEstado("TEST");
		user.setIntentosIngreso(0);
		
		// Definimos los roles del usuario de acuerdo con la membresia del mismo. Todo usuario es usuario final.
		user.setRoles(new HashSet<Role>());
		user.addRole(roleService.getRoleById(environment.getProperty("role.id.usuariofinal")));
		if (user.getMembresia() != null)
			user.addRole(roleService.getRoleById(environment.getProperty("role.id.usuarioprestador."+user.getMembresia().toLowerCase())));

		dao.createUser(user);
	}

	public void updateUser(User user) {
		User entity = dao.getUserById(user.getId());
		if (user.getNombre() != entity.getNombre()) {
			entity.setNombre(user.getNombre());
		}
		if (user.getApellido() != entity.getApellido()) {
			entity.setApellido(user.getApellido());
		}
		if (user.getEmail() != entity.getEmail()) {
			entity.setEmail(user.getEmail());
		}
		if (user.getFechaNacimiento() != entity.getFechaNacimiento()) {
			entity.setFechaNacimiento(user.getFechaNacimiento());
		}
		if (user.getTelefonoPrincipal() != entity.getTelefonoPrincipal()) {
			entity.setTelefonoPrincipal(user.getTelefonoPrincipal());
		}
		if (user.getTelefonoAlternativo() != entity.getTelefonoAlternativo()) {
			entity.setTelefonoAlternativo(user.getTelefonoAlternativo());
		}
		if (user.getDescripcion() != entity.getDescripcion()) {
			entity.setDescripcion(user.getDescripcion());
		}
		
		String newPassword = SecurityUtils.encryptPassword(user.getContrasena().concat(entity.getSalt()));
		if (!entity.getContrasena().equals(newPassword)) {
			entity.setContrasena(newPassword);
			entity.setFechaUltimoCambioContrasena(new DateTime());
			entity.setFechaExpiracionContrasena(
					new DateTime().plusDays(Integer.parseInt(environment.getProperty("password.expiration.timeoffset.days")))
					);
		}
		
		if (user.getMembresia() != entity.getMembresia()) {
			if (entity.getMembresia() != null)
				entity.removeRole(roleService.getRoleById(environment.getProperty("role.id.usuarioprestador."+entity.getMembresia().toLowerCase())));
			if (user.getMembresia() != null)
				entity.addRole(roleService.getRoleById(environment.getProperty("role.id.usuarioprestador."+user.getMembresia().toLowerCase())));
			
			entity.setMembresia(user.getMembresia());
		}
		
		/* El estado lo calculamos con reglas un poco más complejas que aun no definimos.
		entity.setEstado(user.getEstado()); */
		/* Gestionado por componentes de sesión
		entity.setIntentosIngreso(user.getIntentosIngreso());
		entity.setFechaUltimoIngreso(user.getFechaUltimoIngreso()); */
	}

	public void deleteUserById(String id) {
		dao.deleteUserById(id);
	}

	public List<User> getAllUsers() {
		return dao.getAllUsers();
	}

	public User getUserById(String id) {
		return dao.getUserById(id);
	}
	
	public boolean exist(String id) {
		return (dao.getUserById(id) != null);
	}

	public boolean hasUniqueId(String id) {
		User user = getUserById(id);
		return (user == null);
	}
	
	/*
	 *  Actualiza la foto y el thumbnail del Usuario haciendo un resize de la foto suscripta,
	 *  si el procesamiento del thumbnail levanta excepcion, no suscribe la actualizacion
	 *  de la foto. Si la foto es nula, eliminamos la foto y thumbnail actual del usuario.
	 */
	public void updateUserPhotoById(String id, byte[] photo) {
		User entity = dao.getUserById(id);
		if (photo != null) {
	        try {
	        	logger.info(String.format("UpdateUserPhotoById - Updating user's <%s> photo.",id));
	        	entity.setFoto(photo);
	        	
				// construye y guarda el thumbnail a partir de la foto suscripta
	        	InputStream is = new ByteArrayInputStream(photo);
		        BufferedImage img = ImageIO.read(is);
		        BufferedImage thumbImg = Scalr.resize(img, Method.ULTRA_QUALITY,
	                    Mode.AUTOMATIC, 100, 100);
		        
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        	ImageIO.write(thumbImg, "png", baos);
	        	
	        	logger.info(String.format("UpdateUserPhotoById - Updating user's <%s> thumbnail.",id));
		        entity.setThumbnail(baos.toByteArray());
		        
		        img.flush();
		        thumbImg.flush();
		        baos.close();
	        }
	        catch (IOException e) {
	        	logger.error("UpdateUserPhotoById - IOException: "+e.getMessage());
		        throw new RuntimeException(e.getMessage());
	        }
		}
		else {
			if (entity.getFoto() != null || entity.getThumbnail() != null) {
				logger.info(String.format("UpdateUserPhotoById - Deleting user's <%s> photo and thumbnail.",id));
				
				entity.setFoto(null);
				entity.setThumbnail(null);
			}
			
			logger.debug(String.format("UpdateUserPhotoById - User <%s> doesn't have a photo to delete, discarding transaction.",id));
		}
	}
}
