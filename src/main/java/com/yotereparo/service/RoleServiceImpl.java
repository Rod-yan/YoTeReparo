package com.yotereparo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.RoleDaoImpl;
import com.yotereparo.model.Role;
import com.yotereparo.util.SpringEnvironmentUtils;

/**
 * Capa de servicio para Roles.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Service("roleService")
@Transactional 
public class RoleServiceImpl implements RoleService {
	
	private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
	
	@Autowired
	private RoleDaoImpl dao;
	@Autowired
	private ConfigurableEnvironment environment;

	public List<Role> getAllRoles() {
		logger.debug("Fetching all roles");
		return dao.getAllRoles();
	}

	public Role getRoleById(String id) {
		logger.debug(String.format("Fetching role <%s>", id));
		return dao.getRoleById(id);
	}
	
	public List<Role> getAllPrestadorRoles() {
		Map<String, Object> filteredRolesInProperties = SpringEnvironmentUtils.getPropertiesStartingWith(environment, "role.id.usuarioprestador");
		List<Role> prestadorRoles = new ArrayList<Role>();
		for (Object roleId : filteredRolesInProperties.values()) {
			prestadorRoles.add(getRoleById(roleId.toString())); 
		}
		
		return prestadorRoles;
	}
	
	public List<Role> getAllFinalRoles() {
		Map<String, Object> filteredRolesInProperties = SpringEnvironmentUtils.getPropertiesStartingWith(environment, "role.id.usuariofinal");
		List<Role> finalRoles = new ArrayList<Role>();
		for (Object roleId : filteredRolesInProperties.values()) {
			finalRoles.add(getRoleById(roleId.toString())); 
		}
		
		return finalRoles;
	}
}
