package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yotereparo.model.Service;

/**
 * Capa de acceso a datos para Servicios. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * No debe implementar lógica de negocio.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class ServiceDaoImpl extends AbstractDao<Integer, Service> implements ServiceDao {

	public Service getServiceById(int id) {
		return getByKey(id);
	}
	
	public void createService(Service service) {
		persist(service);
	}

	public void deleteServiceById(int id) {
		Query<?> query = getSession().createSQLQuery("DELETE FROM servicio WHERE id_servicio = :id");
		query.setParameter("id", id);
        query.executeUpdate();
	}
 
	public List<Service> getAllServices() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Service> criteriaQuery = builder.createQuery(Service.class);
		criteriaQuery.from(Service.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}
