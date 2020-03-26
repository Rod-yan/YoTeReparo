package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yotereparo.model.City;
import com.yotereparo.model.District;
import com.yotereparo.model.District_;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.model.User_;

/**
 * Capa de acceso a datos para Servicios. Su objetivo es abstraer la interacción con la API de hibernate.
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
 
	public List<Service> getAllServices(Object filter) {
		Query<Service> query = null;
		CriteriaBuilder cb = getSession().getCriteriaBuilder();
		CriteriaQuery<Service> cq = cb.createQuery(Service.class);
		Root<Service> service = cq.from(Service.class);
		if (filter != null)
			switch (filter.getClass().getSimpleName()) {
				case ("User"):
					cq.select(service).where(cb.equal(service.get("usuarioPrestador"), (User) filter));
					break;
				case ("District"):
					{
						District district = (District) filter;
						Subquery<User> sub = cq.subquery(User.class);
						Root<User> subRoot = sub.from(User.class);
						SetJoin<User, District> subDistricts = subRoot.join(User_.barrios);
						sub.select(subRoot);
						sub.where(cb.equal(subDistricts.get(District_.id), district.getId()));
						
						cq.select(service).where(service.get("usuarioPrestador").in(sub));
					}
					break;
				case ("City"):
					Join<Service, User> p = service.join("usuarioPrestador", JoinType.INNER);
					cq.select(service).where(cb.equal(p.get("ciudad"), (City) filter));
					break;
			}
		
		query = getSession().createQuery(cq);
		return query.getResultList();
	}
}
