package com.yotereparo.service;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.UserDaoImpl;
import com.yotereparo.model.User;
import com.yotereparo.util.SecurityUtils;

@Service("userService")
@Transactional 
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDaoImpl dao;
	@Autowired
	private Environment environment;

	public void createUser(User user) {
		user.setSalt(SecurityUtils.saltGenerator());
		user.hashContrasena();
		user.setFechaExpiracionContrasena(new DateTime().plusDays(Integer.parseInt(environment.getProperty("password.expiration.timeoffset.days"))));
		user.setFechaCreacion(new DateTime());
		// Unstable
		user.setEstado("TEST");
		user.setIntentosIngreso(0);
		// Unstable
		user.setMembresia("GRATUITA");
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
		if (user.getFoto() != entity.getFoto()) {
			entity.setFoto(user.getFoto());
			entity.setThumbnail(user.getThumbnail());
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
			
		/* El estado lo calculamos con reglas un poco más complejas que aun no definimos.
		entity.setEstado(user.getEstado()); */
		/* La membresía la vamos a calcular de acuerdo a los roles que tenga el usuario.
		entity.setMembresia(user.getMembresia()); */
		/* Gestionado por componentes de sesión
		entity.setIntentosIngreso(user.getIntentosIngreso());
		entity.setFechaUltimoIngreso(user.getFechaUltimoIngreso()); */
		/* Nunca les hacemos update, solo setteo inicial en tiempo de creación.
		entity.setSalt(user.getSalt()); 
		entity.setFechaCreacion(user.getFechaCreacion()); */
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
}
