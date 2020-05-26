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

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
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
		if (getUserByEmail(user.getEmail()) != null) {
			// Illegal
			logger.debug("User email: email <{}> is already registered to a different user.", user.getEmail());
			throw new CustomResponseError("User","email",
					messageSource.getMessage("user.email.already.exist", new String[]{user.getEmail()}, Locale.getDefault()));
		}
		
		user.setSalt(SecurityUtils.saltGenerator());
		user.setContrasena(SecurityUtils.encryptPassword(user.getContrasena().concat(user.getSalt())));
		user.setFechaExpiracionContrasena(new DateTime().plusDays(
				Integer.parseInt(environment.getProperty("password.expiration.timeoffset.days"))));
		user.setFechaCreacion(new DateTime());
		user.setEstado(User.ACTIVE);
		user.setIntentosIngreso(0);
		
		/*
		 *  Definimos los roles del usuario de acuerdo con la membresia del mismo. Todo usuario es usuario final.
		 *  Si el usuario no es prestador, no lo vinculamos con barrios. Si lo es, se valida que los barrios pertenezcan
		 *  a la ciudad elegida.
		 */
		Role role = roleService.getRoleById(environment.getProperty("role.id.usuariofinal"));
		logger.debug("Adding role <{}> to user <{}>", role.toString(), user.getId());
		user.addRole(role);
		if (user.getMembresia() != null) {
			role = roleService.getRoleById(environment.getProperty("role.id.usuarioprestador."+user.getMembresia().toLowerCase()));
			logger.debug("Adding role <{}> to user <{}>", role.toString(), user.getId());
			user.addRole(role);
			
			// Filtramos los barrios elegidos que no sean de la ciudad elegida. 
			// Si no quedan barrios válidos, levantamos excepción.
			user.getBarrios().removeAll(cityService.getInvalidDistricts(user.getCiudad(), user.getBarrios()));
			if (user.getBarrios().size() == 0) {
				// Illegal
				logger.debug("User districts: all entered districts were invalid for user's city.");
				throw new CustomResponseError("User","barrios",
						messageSource.getMessage("user.barrios.not.empty", null, Locale.getDefault()));
			}
		}
		else {
			logger.debug("Clearing all districts from user <{}>", user.getId());
			if (user.getBarrios() != null)
				user.getBarrios().clear();
		}
		
		logger.info("Commiting creation of user <{}>", user.getId());
		dao.createUser(user);
	}

	public void updateUser(User user) {
		User entity = getUserById(user.getId());
		
		if (!SecurityUtils.encryptPassword(user.getContrasena().concat(entity.getSalt())).equals(entity.getContrasena())) {
			// Si la contraseña ingresada es incorrecta, no procedemos con el update.
			logger.debug("User password: password does not match.");
			throw new CustomResponseError("User","contrasena",
					messageSource.getMessage("user.contrasena.not.equals.current", null, Locale.getDefault()));
		}
		
		if (!user.getNombre().equals(entity.getNombre())) {
			logger.debug("Updating attribute 'Nombre' from user <{}>", user.getId());
			entity.setNombre(user.getNombre());
		}
		
		if (!user.getApellido().equals(entity.getApellido())) {
			logger.debug("Updating attribute 'Apellido' from user <{}>", user.getId());
			entity.setApellido(user.getApellido());
		}
		
		if (!user.getEmail().equals(entity.getEmail())) {
			if (getUserByEmail(user.getEmail()) != null) {
				// Illegal
				logger.debug("User email: email <{}> is already registered to a different user.", user.getEmail());
				throw new CustomResponseError("User","email",
						messageSource.getMessage("user.email.already.exist", new String[]{user.getEmail()}, Locale.getDefault()));
			}
			
			logger.debug("Updating attribute 'Email' from user <{}>", user.getId());
			entity.setEmail(user.getEmail());
		}
		
		if (user.getFechaNacimiento() != null) {
			if (!user.getFechaNacimiento().equals(entity.getFechaNacimiento())) {
				logger.debug("Updating attribute 'FechaNacimiento' from user <{}>", user.getId());
				entity.setFechaNacimiento(user.getFechaNacimiento());
			}
		}
		else {
			if (entity.getFechaNacimiento() != null) {
				logger.debug("Updating attribute 'FechaNacimiento' from user <{}>", user.getId());
				entity.setFechaNacimiento(null);
			}
		}
		
		if (user.getTelefonoPrincipal() != null) {
			if (!user.getTelefonoPrincipal().equals(entity.getTelefonoPrincipal())) {
				logger.debug("Updating attribute 'TelefonoPrincipal' from user <{}>", user.getId());
				entity.setTelefonoPrincipal(user.getTelefonoPrincipal());
			}
		}
		else {
			if (entity.getTelefonoPrincipal() != null) {
				logger.debug("Updating attribute 'TelefonoPrincipal' from user <{}>", user.getId());
				entity.setTelefonoPrincipal(null);
			}
		}
		
		if (user.getTelefonoAlternativo() != null) {
			if (!user.getTelefonoAlternativo().equals(entity.getTelefonoAlternativo())) {
				logger.debug("Updating attribute 'TelefonoAlternativo' from user <{}>", user.getId());
				entity.setTelefonoAlternativo(user.getTelefonoAlternativo());
			}
		}
		else {
			if (entity.getTelefonoAlternativo() != null) {
				logger.debug("Updating attribute 'TelefonoAlternativo' from user <{}>", user.getId());
				entity.setTelefonoAlternativo(null);
			}
		}
		
		if (user.getDescripcion() != null) {
			if (!user.getDescripcion().equals(entity.getDescripcion())) {
				logger.debug("Updating attribute 'Descripcion' from user <{}>", user.getId());
				entity.setDescripcion(user.getDescripcion());
			}
		}
		else {
			if (entity.getDescripcion() != null) {
				logger.debug("Updating attribute 'Descripcion' from user <{}>", user.getId());
				entity.setDescripcion(null);
			}
		}
		
		// Agregamos y quitamos roles al usuario de acuerdo con su definicion de membresia actual. 
		// Todo usuario es usuario final.
		if (!Objects.equals(user.getMembresia(), entity.getMembresia())) {
			if (entity.getMembresia() != null) {
				Role role = roleService.getRoleById(
						environment.getProperty("role.id.usuarioprestador."+entity.getMembresia().toLowerCase()));
				logger.debug("Removing role <{}> from user <{}>", role.toString(), user.getId());
				entity.removeRole(role);
			}
				
			if (user.getMembresia() != null) {
				Role role = roleService.getRoleById(
						environment.getProperty("role.id.usuarioprestador."+user.getMembresia().toLowerCase()));
				logger.debug("Adding role <{}> to user <{}>", role.toString(), user.getId());
				entity.addRole(role);
			}
				
			logger.debug("Updating attribute 'Membresia' from user <{}>", user.getId());
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
		// Si el usuario tiene servicios registrados no puede vaciar sus direcciones
		if (entity.getDirecciones().size() == 0 
		 && user.getMembresia() != null 
		 && (user.getServicios() != null 
		 && user.getServicios().size() != 0))
			throw new CustomResponseError("User","direcciones",
					messageSource.getMessage("user.direcciones.not.empty", null, Locale.getDefault()));
		
		/* Si el usuario es prestador (su membresía no es nula), validamos y procesamos los barrios, de lo contrario
		 * descartamos los barrios del usuario.
		 * Si la ciudad cambió, o si cambiaron los barrios, descartamos los barrios anteriores por los nuevos.
		 */
		if (user.getMembresia() != null) {
			user.getBarrios().removeAll(cityService.getInvalidDistricts(user.getCiudad(), user.getBarrios()));
			if (user.getBarrios().size() == 0) {
				throw new CustomResponseError("User","barrios",
						messageSource.getMessage("user.barrios.not.empty", null, Locale.getDefault()));
			}
			if (!user.getCiudad().equals(entity.getCiudad()) || !user.getBarrios().equals(entity.getBarrios())) {
				logger.debug("Updating all districts from user <{}>", user.getId());
				entity.getBarrios().clear();
				entity.getBarrios().addAll(user.getBarrios());
			}
		}
		else {
			if (entity.getBarrios().size() != 0) {
				logger.debug("Clearing all districts from user <{}>", user.getId());
				entity.getBarrios().clear();
			}
		}
			
		if (!user.getCiudad().equals(entity.getCiudad())) {
			logger.debug("Updating attribute 'Ciudad' from user <{}>", user.getId());
			entity.setCiudad(user.getCiudad());
		}
		
		logger.info("Commiting update for user <{}>", user.getId());
	}
	
	public void changeUserPasswordById(String id, String currentPassword, String newPassword) {
		User user = getUserById(id);
		
		currentPassword = SecurityUtils.encryptPassword(currentPassword.concat(user.getSalt()));
		newPassword = SecurityUtils.encryptPassword(newPassword.concat(user.getSalt()));
		String trueCurrentPassword = user.getContrasena();
		
		// Si el usuario está autenticado y es administrador o cuenta de servicio,
		// ignoramos la validación de la contraseña actual
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isServiceAccountOrAdministrator = false;
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String authenticatedUsername = ((UserDetails)authentication.getPrincipal()).getUsername();
			isServiceAccountOrAdministrator = isServiceAccountOrAdministrator(getUserById(authenticatedUsername));
		}
		
		// La verdadera contraseña actual debe ser igual a la contraseña actual ingresada por el usuario
		if (currentPassword.equals(trueCurrentPassword) || isServiceAccountOrAdministrator) {
			// La nueva contraseña no puede ser igual a la anterior
			if (!newPassword.equals(trueCurrentPassword) || isServiceAccountOrAdministrator) {
				logger.debug("Updating attribute 'Contrasena' (and derivates) from user <{}>", user.getId());
				user.setContrasena(newPassword);
				user.setFechaUltimoCambioContrasena(new DateTime());
				user.setFechaExpiracionContrasena(
						new DateTime().plusDays(
								Integer.parseInt(environment.getProperty("password.expiration.timeoffset.days")))
						);
				// Limpiamos estado de sesión del usuario, desbloqueamos si se encuentra bloqueado
				if (user.getIntentosIngreso() != 0) {
					logger.debug("Updating attribute 'IntentosIngreso' from user <{}>", user.getId());
					user.setIntentosIngreso(0);
				}
				if (user.getEstado().equals(User.BLOCKED)) {
					logger.warn("Enabling previously blocked user <{}>", user.getId());
					user.setEstado(User.ACTIVE);
				}
			}
			else
				throw new CustomResponseError("User","contrasena",
						messageSource.getMessage("user.contrasena.must.be.different.from.current",
								null, Locale.getDefault()));
		}
		else
			throw new CustomResponseError("User","contrasena",
					messageSource.getMessage("user.contrasena.not.equals.current", null, Locale.getDefault()));
	}
	
	public void registerSuccessfulLoginAttempt(User user) {
		User entity = getUserById(user.getId());
		
		logger.debug("Updating attribute 'FechaUltimoIngreso' from user <{}>", user.getId());
		entity.setFechaUltimoIngreso(new DateTime());
		if (user.getIntentosIngreso() != 0) {
			logger.debug("Updating attribute 'IntentosIngreso' from user <{}>", user.getId());
			entity.setIntentosIngreso(0);
		}
		
		logger.info("Successful login attempt registered for user <{}>", user.getId());
	}
	
	public void registerFailedLoginAttempt(User user) {
		User entity = getUserById(user.getId());
		
		logger.debug("Updating attribute 'IntentosIngreso' from user <{}>", user.getId());
		entity.setIntentosIngreso(user.getIntentosIngreso()+1);
		// Si el usuario alcanza o excede el límite de intentos de ingreso, se bloquea
		if (entity.getIntentosIngreso() >= Integer.parseInt(environment.getProperty("login.attempts.limit")))
			if (!entity.getEstado().equals(User.BLOCKED)) {
				logger.warn("Disabling user <{}>: Too many failed login attempts", user.getId());
				entity.setEstado(User.BLOCKED);
			}
		
		logger.info("Failed login attempt registered for user <{}>", user.getId());
	}
	
	public void deleteUserById(String id) {
		logger.info("Commiting deletion of user <{}>", id);
		dao.deleteUserById(id);
	}

	public List<User> getAllUsers() {
		logger.debug("Fetching all users");
		return dao.getAllUsers();
	}

	public User getUserById(String id) {
		logger.debug("Fetching user <{}>", id);
		return dao.getUserById(id);
	}
	
	public User getUserByEmail(String email) {
		logger.debug("Fetching user by email <{}>", email);
		return dao.getUserByEmail(email);
	}
	
	/*
	 *  Actualiza la foto y el thumbnail del Usuario haciendo un resize de la foto suscripta,
	 *  Si el parámetro <foto> es nulo, eliminamos la foto y thumbnail actual del usuario.
	 */
	public void updateUserPhotoById(String id, byte[] photo) {
		User entity = dao.getUserById(id);
		if (photo != null) {
	        try {
	        	// Reformateamos la imagen suscripta para normalizar archivos muy grandes, y generamos el thumbnail
	        	InputStream is = new ByteArrayInputStream(photo);
		        BufferedImage img = ImageIO.read(is);
		        logger.debug("Resizing input image");
		        BufferedImage userPhoto = Scalr.resize(img, Method.ULTRA_QUALITY,
	                    Mode.AUTOMATIC, 300, 300);
		        logger.debug("Building thumbnail from input image");
		        BufferedImage userThumbnail = Scalr.resize(img, Method.ULTRA_QUALITY,
	                    Mode.AUTOMATIC, 100, 100);
		        
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ImageIO.write(userPhoto, "png", baos);
		        logger.debug("Updating attribute 'Foto' from user <{}>",id);
	        	entity.setFoto(baos.toByteArray());
	        	
	        	baos.reset();
	        	ImageIO.write(userThumbnail, "png", baos);
	        	logger.debug("Updating attribute 'Thumbnail' from user <{}>",id);
		        entity.setThumbnail(baos.toByteArray());
		        
		        img.flush();
		        userPhoto.flush();
		        userThumbnail.flush();
		        baos.close();		        
	        }
	        catch (IOException e) {
	        	logger.error("IOException: "+e.getMessage());
		        throw new RuntimeException(e.getMessage());
	        }
	        finally {
	        	logger.info("Commiting update for user <{}>", id);
	        }
		}
		else {
			if (entity.getFoto() != null || entity.getThumbnail() != null) {
				logger.debug("Deleting attribute 'Foto' and 'Thumbnail' from user <{}>",id);
				
				entity.setFoto(null);
				entity.setThumbnail(null);
			}
			
			logger.debug("No 'Foto' nor 'Thumbnail' registered for user <{}>, discarding transaction.",id);
		}
	}
	
	public boolean isProvider(User user) {
		logger.debug("Verifying if user's <{}> is of type PRESTADOR", user.getId());
		for (Role role : roleService.getAllPrestadorRoles()) {
			if (user.getRoles().contains(role))
				return true;
		}
		return false;
	}
	
	public boolean isCustomer(User user) {
		logger.debug("Verifying if user's <{}> is of type FINAL", user.getId());
		for (Role role : roleService.getAllFinalRoles()) {
			if (user.getRoles().contains(role))
				return true;
		}
		return false;
	}
	
	public boolean isServiceAccountOrAdministrator(User user) {
		logger.debug("Verifying if user's <{}> is of type CUENTA SERVICIO or ADMINISTRADOR", user.getId());
		if (user.getRoles().contains(roleService.getRoleById(environment.getProperty("role.id.serviceaccount")))
		 || user.getRoles().contains(roleService.getRoleById(environment.getProperty("role.id.administrator"))))
			return true;
		return false;
	}
	
	public boolean hasMembershipAllowance(User user) {
		logger.debug("Verifying if user's <{}> has sufficient membership allowance", user.getId());
		int membershipServiceCreationAlowance = 
				Integer.parseInt(environment.getProperty("membership.service.creation.allowance."+user.getMembresia().toLowerCase()));
		int currentServiceCount = user.getServicios().size();
		return (currentServiceCount < membershipServiceCreationAlowance);
	}
}
