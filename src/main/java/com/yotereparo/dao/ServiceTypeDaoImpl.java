package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import com.yotereparo.model.ServiceType;

/**
 * Capa de acceso a datos para Tipos de Servicio. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class ServiceTypeDaoImpl extends AbstractDao<Integer, ServiceType> implements ServiceTypeDao {

	public ServiceType getServiceTypeById(Integer id) {
		return getByKey(id);
	}
	
	public ServiceType getServiceTypeByDescription(String description) {
		ServiceType serviceType = getSession().byNaturalId(ServiceType.class)
				.using("descripcion", description)
                .load();
		
		return serviceType;
	}
 
	public List<ServiceType> getAllServiceTypes() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<ServiceType> criteriaQuery = builder.createQuery(ServiceType.class);
		criteriaQuery.from(ServiceType.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}