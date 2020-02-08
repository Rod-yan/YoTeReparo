package com.yotereparo.dao;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yotereparo.model.Service;
import com.yotereparo.model.User;

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

	public Service getServiceById(Integer id) {
		return getByKey(id);
	}
	
	public void createService(Service service) {
		persist(service);
	}

	public void deleteServiceById(Integer id) {
		Query<?> query = getSession().createSQLQuery("DELETE FROM servicio WHERE id_servicio = :id");
		query.setParameter("id", id);
        query.executeUpdate();
	}
 
	public List<Service> getAllServices(Object obj) {
		Expression<Boolean> filter = null;
		TypedQuery<Service> query = null;
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Service> criteriaQuery = builder.createQuery(Service.class);
		Root<Service> s = criteriaQuery.from(Service.class);
		if (obj != null)
			switch (obj.getClass().getSimpleName()) {
				case ("User"):
					User user = (User) obj;
					ParameterExpression<String> userId = builder.parameter(String.class);
					filter = builder.equal(s.get("id_usuario_prestador"), userId);
					query = getSession().createQuery(criteriaQuery.where(filter));
					query.setParameter(userId, user.getId());
					break;
				case ("District"):
					break;
				case ("City"):
					break;
				case ("PaymentMethod"):
					break;
			}
		else
			query = getSession().createQuery(criteriaQuery);
		
		return query.getResultList();
	}
}
