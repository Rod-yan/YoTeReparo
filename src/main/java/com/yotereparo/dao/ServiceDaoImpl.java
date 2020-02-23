package com.yotereparo.dao;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yotereparo.model.City;
import com.yotereparo.model.District;
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
	
	private static final Logger logger = LogManager.getLogger(ServiceDaoImpl.class);

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
		Query<Service> query = null;
		CriteriaBuilder cb = getSession().getCriteriaBuilder();
		CriteriaQuery<Service> cq = cb.createQuery(Service.class);
		Root<Service> service = cq.from(Service.class);
		
		if (obj != null)
			switch (obj.getClass().getSimpleName()) {
				case ("User"):
					cq.select(service).where(cb.equal(service.get("usuarioPrestador"), (User) obj));
					break;
				case ("District"):/*
					Subquery sub = cq.subquery(Long.class);
					Root<User> subRoot = sub.from(User.class);
					SetJoin<User, District> subDistricts = subRoot.join(Book_.authors);
					sub.select(cb.count(subRoot.get(Book_.id)));
					sub.where(cb.equal(root.get(Author_.id), subAuthors.get(Author_.id)));*/
					break;
				case ("City"):
					//cq.select(service).where(cb.equal(service.get("usuarioPrestador"), (City) obj));
					break;
			}
		
		query = getSession().createQuery(cq);
		return query.getResultList();
	}
}
