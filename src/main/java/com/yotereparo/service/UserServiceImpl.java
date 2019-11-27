package com.yotereparo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.UserDaoImpl;
import com.yotereparo.model.Address;
import com.yotereparo.model.Role;
import com.yotereparo.model.User;
import com.yotereparo.util.SecurityUtils;
import com.yotereparo.util.error.CustomResponseError;

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
    private MessageSource messageSource;
	@Autowired
	private RoleService roleService;
	@Autowired
	private CityService cityService;

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
		
		/*
		 *  Definimos los roles del usuario de acuerdo con la membresia del mismo. Todo usuario es usuario final.
		 *  Si el usuario no es prestador, no lo vinculamos con barrios. Si lo es, se valida que los barrios pertenezcan
		 *  a la ciudad elegida.
		 */
		Role role = roleService.getRoleById(environment.getProperty("role.id.usuariofinal"));
		logger.debug(String.format("Adding role <%s> to user <%s>", role.toString(), user.getId()));
		user.addRole(role);
		if (user.getMembresia() != null) {
			role = roleService.getRoleById(environment.getProperty("role.id.usuarioprestador."+user.getMembresia().toLowerCase()));
			logger.debug(String.format("Adding role <%s> to user <%s>", role.toString(), user.getId()));
			user.addRole(role);
			
			// Filtramos los barrios elegidos que no sean de la ciudad elegida. Si no quedan barrios válidos, levantamos excepción.
			user.getBarrios().removeAll(cityService.filterInvalidDistricts(user.getCiudad(), user.getBarrios()));
			if (user.getBarrios().size() == 0) {
				throw new CustomResponseError("User","barrios",messageSource.getMessage("user.barrios.not.empty", null, Locale.getDefault()));
			}
		}
		else {
			logger.debug(String.format("Clearing all districts from user <%s>", user.getId()));
			if (user.getBarrios() != null)
				user.getBarrios().clear();
		}
		
		logger.info(String.format("Commiting creation of user <%s>", user.getId()));
		dao.createUser(user);
	}

	public void updateUser(User user) {
		User entity = dao.getUserById(user.getId());
		
		if (!user.getNombre().equals(entity.getNombre())) {
			logger.debug(String.format("Updating attribute 'Nombre' from user <%s>", user.getId()));
			entity.setNombre(user.getNombre());
		}	
		
		if (!user.getApellido().equals(entity.getApellido())) {
			logger.debug(String.format("Updating attribute 'Apellido' from user <%s>", user.getId()));
			entity.setApellido(user.getApellido());
		}
		
		if (!user.getEmail().equals(entity.getEmail())) {
			logger.debug(String.format("Updating attribute 'Email' from user <%s>", user.getId()));
			entity.setEmail(user.getEmail());
		}
		
		if (user.getFechaNacimiento() != null) {
			if (!user.getFechaNacimiento().equals(entity.getFechaNacimiento())) {
				logger.debug(String.format("Updating attribute 'FechaNacimiento' from user <%s>", user.getId()));
				entity.setFechaNacimiento(user.getFechaNacimiento());
			}
		}
		else {
			if (entity.getFechaNacimiento() != null) {
				logger.debug(String.format("Updating attribute 'FechaNacimiento' from user <%s>", user.getId()));
				entity.setFechaNacimiento(null);
			}
		}
		
		if (user.getTelefonoPrincipal() != null) {
			if (!user.getTelefonoPrincipal().equals(entity.getTelefonoPrincipal())) {
				logger.debug(String.format("Updating attribute 'TelefonoPrincipal' from user <%s>", user.getId()));
				entity.setTelefonoPrincipal(user.getTelefonoPrincipal());
			}
		}
		else {
			if (entity.getTelefonoPrincipal() != null) {
				logger.debug(String.format("Updating attribute 'TelefonoPrincipal' from user <%s>", user.getId()));
				entity.setTelefonoPrincipal(null);
			}
		}
		
		if (user.getTelefonoAlternativo() != null) {
			if (!user.getTelefonoAlternativo().equals(entity.getTelefonoAlternativo())) {
				logger.debug(String.format("Updating attribute 'TelefonoAlternativo' from user <%s>", user.getId()));
				entity.setTelefonoAlternativo(user.getTelefonoAlternativo());
			}
		}
		else {
			if (entity.getTelefonoAlternativo() != null) {
				logger.debug(String.format("Updating attribute 'TelefonoAlternativo' from user <%s>", user.getId()));
				entity.setTelefonoAlternativo(null);
			}
		}
		
		if (user.getDescripcion() != null) {
			if (!user.getDescripcion().equals(entity.getDescripcion())) {
				logger.debug(String.format("Updating attribute 'Descripcion' from user <%s>", user.getId()));
				entity.setDescripcion(user.getDescripcion());
			}
		}
		else {
			if (entity.getDescripcion() != null) {
				logger.debug(String.format("Updating attribute 'Descripcion' from user <%s>", user.getId()));
				entity.setDescripcion(null);
			}
		}
		
		String newPassword = SecurityUtils.encryptPassword(user.getContrasena().concat(entity.getSalt()));
		if (!entity.getContrasena().equals(newPassword)) {
			// TODO: Mover a método dedicado
			logger.debug(String.format("Updating attribute 'Contrasena' (and derivates) from user <%s>", user.getId()));
			entity.setContrasena(newPassword);
			entity.setFechaUltimoCambioContrasena(new DateTime());
			entity.setFechaExpiracionContrasena(
					new DateTime().plusDays(Integer.parseInt(environment.getProperty("password.expiration.timeoffset.days")))
					);
		}
		
		// Agregamos y quitamos roles al usuario de acuerdo con su definicion de membresia actual. Todo usuario es usuario final.
		if (!Objects.equals(user.getMembresia(), entity.getMembresia())) {
			if (entity.getMembresia() != null) {
				Role role = roleService.getRoleById(environment.getProperty("role.id.usuarioprestador."+entity.getMembresia().toLowerCase()));
				logger.debug(String.format("Removing role <%s> from user <%s>", role.toString(), user.getId()));
				entity.removeRole(role);
			}
				
			if (user.getMembresia() != null) {
				Role role = roleService.getRoleById(environment.getProperty("role.id.usuarioprestador."+user.getMembresia().toLowerCase()));
				logger.debug(String.format("Adding role <%s> to user <%s>", role.toString(), user.getId()));
				entity.addRole(role);
			}
				
			logger.debug(String.format("Updating attribute 'Membresia' from user <%s>", user.getId()));
			entity.setMembresia(user.getMembresia());
		}
		
		/* Comparamos las direcciones del usuario actualizado con las existentes, y calculamos
		 * los deltas para armar el Set final. Esto es necesario debido a que tenemos que comparar
		 * a nivel de entidad <Address>, ya que cada objeto tiene id autogenerado.
		 */
		// TODO: Bloque muy suboptimizado, O(n^2). Poco elegante. Refactorizar.
		Set<Address> addressesToBeRemoved = new HashSet<Address>();
		Boolean addressWasRemoved = true;
		for (Address direccion : entity.getDirecciones()) {
			addressWasRemoved = true;
			for (Address entry : user.getDirecciones()) {
				if (entry.equals(direccion)) {
					addressWasRemoved = false;
					break;
				}
			}			
			if (addressWasRemoved)
				addressesToBeRemoved.add(direccion);
		}
		// TODO: Bloque muy suboptimizado, O(n^2). Poco elegante. Refactorizar.
		Set<Address> addressesToBeAdded = new HashSet<Address>();
		Boolean addressWasAdded = true;
		for (Address entry : user.getDirecciones()) {
			addressWasAdded = true;
			for (Address direccion : entity.getDirecciones()) {
				if (entry.equals(direccion)) {
					addressWasAdded = false;
					break;
				}
			}
			if (addressWasAdded)
				addressesToBeAdded.add(entry);
		}
		entity.getDirecciones().addAll(addressesToBeAdded);
		entity.getDirecciones().removeAll(addressesToBeRemoved);
		
		/* Si el usuario es prestador (su membresía no es nula), validamos y procesamos los barrios, de lo contrario
		 * descartamos los barrios del usuario.
		 * Si la ciudad cambió, o si cambiaron los barrios, descartamos los barrios anteriores por los nuevos.
		 */
		if (user.getMembresia() != null) {
			user.getBarrios().removeAll(cityService.filterInvalidDistricts(user.getCiudad(), user.getBarrios()));
			if (user.getBarrios().size() == 0) {
				throw new CustomResponseError("User","barrios",messageSource.getMessage("user.barrios.not.empty", null, Locale.getDefault()));
			}
			if (!user.getCiudad().equals(entity.getCiudad()) || !user.getBarrios().equals(entity.getBarrios())) {
				logger.debug(String.format("Updating all districts from user <%s>", user.getId()));
				entity.getBarrios().clear();
				entity.getBarrios().addAll(user.getBarrios());
			}
		}
		else {
			if (entity.getBarrios().size() != 0) {
				logger.debug(String.format("Clearing all districts from user <%s>", user.getId()));
				entity.getBarrios().clear();
			}
		}
			
		if (!user.getCiudad().equals(entity.getCiudad())) {
			logger.debug(String.format("Updating attribute 'Ciudad' from user <%s>", user.getId()));
			entity.setCiudad(user.getCiudad());
		}
		
		/* El estado lo calculamos con reglas un poco más complejas que aun no definimos.
		entity.setEstado(user.getEstado()); */
		/* Gestionado por componentes de sesión
		entity.setIntentosIngreso(user.getIntentosIngreso());
		entity.setFechaUltimoIngreso(user.getFechaUltimoIngreso()); */
		logger.info(String.format("Commiting update for user <%s>", user.getId()));
	}

	public void deleteUserById(String id) {
		logger.info(String.format("Commiting deletion of user <%s>", id));
		dao.deleteUserById(id);
	}

	public List<User> getAllUsers() {
		logger.debug("Fetching all users");
		return dao.getAllUsers();
	}

	public User getUserById(String id) {
		logger.debug(String.format("Fetching user <%s>", id));
		return dao.getUserById(id);
	}
	
	public boolean exist(String id) {
		logger.debug(String.format("Verifying existence of user <%s>", id));
		return (getUserById(id) != null);
	}

	public boolean hasUniqueId(String id) {
		logger.debug(String.format("Verifying uniqueness of user's <%s> ID", id));
		return !exist(id);
	}
	
	public boolean userIsPrestador(User user) {
		logger.debug(String.format("Verifying if user's <%s> is of type PRESTADOR", user.getId()));
		for (Role role : roleService.getAllPrestadorRoles()) {
			if (user.getRoles().contains(role))
				return true;
		}
		return false;
	}
	
	public boolean userIsFinal(User user) {
		logger.debug(String.format("Verifying if user's <%s> is of type FINAL", user.getId()));
		for (Role role : roleService.getAllFinalRoles()) {
			if (user.getRoles().contains(role))
				return true;
		}
		return false;
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
	        	logger.debug(String.format("Updating attribute 'Foto' from user <%s>",id));
	        	entity.setFoto(photo);
	        	
				// construye y guarda el thumbnail a partir de la foto suscripta
	        	logger.debug("Building thumbnail from input image");
	        	InputStream is = new ByteArrayInputStream(photo);
		        BufferedImage img = ImageIO.read(is);
		        BufferedImage thumbImg = Scalr.resize(img, Method.ULTRA_QUALITY,
	                    Mode.AUTOMATIC, 100, 100);
		        
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        	ImageIO.write(thumbImg, "png", baos);
	        	
	        	logger.debug(String.format("Updating attribute 'Thumbnail' from user <%s>",id));
		        entity.setThumbnail(baos.toByteArray());
		        
		        img.flush();
		        thumbImg.flush();
		        baos.close();		        
	        }
	        catch (IOException e) {
	        	logger.error("IOException: "+e.getMessage());
		        throw new RuntimeException(e.getMessage());
	        }
	        finally {
	        	logger.info(String.format("Commiting update for user <%s>", id));
	        }
		}
		else {
			if (entity.getFoto() != null || entity.getThumbnail() != null) {
				logger.debug(String.format("Deleting attribute 'Foto' and 'Thumbnail' from user <%s>",id));
				
				entity.setFoto(null);
				entity.setThumbnail(null);
			}
			
			logger.debug(String.format("No 'Foto' nor 'Thumbnail' registered for user <%s>, discarding transaction.",id));
		}
	}
}
