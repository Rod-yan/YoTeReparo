package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yotereparo.model.User;

/**
 * Capa de acceso a datos para Usuarios. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class UserDaoImpl extends AbstractDao<String, User> implements UserDao {

	public User getUserById(String id) {
		return getByKey(id);
	}
	
	public void createUser(User user) {
		persist(user);
	}

	public void deleteUserById(String id) {
		Query<?> query = getSession().createSQLQuery("DELETE FROM usuario WHERE id_usuario = :id");
		query.setParameter("id", id);
        query.executeUpdate();
	}
 
	public List<User> getAllUsers() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
		criteriaQuery.from(User.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}
