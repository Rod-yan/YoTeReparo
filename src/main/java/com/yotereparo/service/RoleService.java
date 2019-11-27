package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.Role;

public interface RoleService {
 
    List<Role> getAllRoles(); 
     
    Role getRoleById(String id);
    
    List<Role> getAllPrestadorRoles();
    
    List<Role> getAllFinalRoles();
    
    boolean exist(String id);
 
    boolean hasUniqueId(String id);
}
