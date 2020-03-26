package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import com.yotereparo.model.District;

/**
 * Capa de acceso a datos para Distritos. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class DistrictDaoImpl extends AbstractDao<Integer, District> implements DistrictDao {

	public District getDistrictById(Integer id) {
		return getByKey(id);
	}
 
	public List<District> getAllDistricts() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<District> criteriaQuery = builder.createQuery(District.class);
		criteriaQuery.from(District.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}