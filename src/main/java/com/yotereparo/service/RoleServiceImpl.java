package com.yotereparo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.RoleDaoImpl;
import com.yotereparo.model.Role;

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
	
	@Autowired
	private RoleDaoImpl dao;

	public List<Role> getAllRoles() {
		return dao.getAllRoles();
	}

	public Role getRoleById(String id) {
		return dao.getRoleById(id);
	}
	
	public boolean exist(String id) {
		return (dao.getRoleById(id) != null);
	}

	public boolean hasUniqueId(String id) {
		Role Role = getRoleById(id);
		return (Role == null);
	}
}
