package com.yotereparo.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.CityDaoImpl;
import com.yotereparo.model.City;
import com.yotereparo.model.District;

/**
 * Capa de servicio para Ciudades.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Service("cityService")
@Transactional 
public class CityServiceImpl implements CityService {
	
	private static final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);
	
	@Autowired
	private CityDaoImpl dao;

	public List<City> getAllCities() {
		logger.debug("Fetching all cities");
		return dao.getAllCities();
	}

	public City getCityById(String id) {
		logger.debug("Fetching city <{}>", id);
		return dao.getCityById(id);
	}
	
	public Set<District> getValidDistricts(City city, Set<District> districts){
		Set<District> validDistricts = new HashSet<District>();
		for (District district : districts)
			if (cityContainsDistrict(city, district)) {
				logger.debug("Allowing district <{}>", district.getId());
				validDistricts.add(district);
			}
		
		return validDistricts;
	}
	
	public Set<District> getInvalidDistricts(City city, Set<District> districts){
		Set<District> invalidDistricts = new HashSet<District>();
		for (District district : districts)
			if (!cityContainsDistrict(city, district)) {
				logger.debug("Discarding district <{}>", district.getId());
				invalidDistricts.add(district);
			}
		
		return invalidDistricts;
	}
	
	public boolean cityContainsDistrict(City city, District district) {
		logger.debug("Validating that district <{}> belongs in city with ID <{}>", district.getId(), city.getId());
		for (District entry : city.getBarrios())
			if (entry.getId() == district.getId())
				return true;
		return false;
	}
}
