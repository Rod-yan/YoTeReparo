package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import com.yotereparo.model.City;

/**
 * Capa de acceso a datos para Ciudades. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class CityDaoImpl extends AbstractDao<String, City> implements CityDao {

	public City getCityById(String id) {
		return getByKey(id);
	}
 
	public List<City> getAllCities() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<City> criteriaQuery = builder.createQuery(City.class);
		criteriaQuery.from(City.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}