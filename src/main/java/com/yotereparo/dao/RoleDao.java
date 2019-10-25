package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.Role;

public interface RoleDao {
	
	Role getRoleById(String id);
	 
	List<Role> getAllRoles();
}
