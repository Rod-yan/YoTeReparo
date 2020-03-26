package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import com.yotereparo.model.Requirement;

/**
 * Capa de acceso a datos para Requerimientos. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Gabriel Capitanelli
 * 
 */
@Repository
public class RequirementDaoImpl extends AbstractDao<Integer, Requirement> implements RequirementDao {

	public Requirement getRequirementById(Integer id) {
		return getByKey(id);
	}
	
	public Requirement getRequirementByDescription(String description) {
		Requirement Requirement = getSession().byNaturalId(Requirement.class)
				.using("descripcion", description)
                .load();
		
		return Requirement;
	}
 
	public List<Requirement> getAllRequirements() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Requirement> criteriaQuery = builder.createQuery(Requirement.class);
		criteriaQuery.from(Requirement.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}
