package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import com.yotereparo.model.Role;

/**
 * Capa de acceso a datos para Roles. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class RoleDaoImpl extends AbstractDao<String, Role> implements RoleDao {

	public Role getRoleById(String id) {
		return getByKey(id);
	}
 
	public List<Role> getAllRoles() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Role> criteriaQuery = builder.createQuery(Role.class);
		criteriaQuery.from(Role.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}