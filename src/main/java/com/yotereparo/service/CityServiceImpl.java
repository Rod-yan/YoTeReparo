package com.yotereparo.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
	
	private static final Logger logger = LogManager.getLogger(CityServiceImpl.class);
	
	@Autowired
	private CityDaoImpl dao;

	public List<City> getAllCities() {
		logger.debug("Fetching all cities");
		return dao.getAllCities();
	}

	public City getCityById(String id) {
		logger.debug(String.format("Fetching city <%s>", id));
		return dao.getCityById(id);
	}
	
	public Set<District> getValidDistricts(City city, Set<District> districts){
		Set<District> validDistricts = new HashSet<District>();
		for (District district : districts)
			if (cityContainsDistrict(city, district)) {
				logger.debug(String.format("Allowing district <%s>", district.toString()));
				validDistricts.add(district);
			}
		
		return validDistricts;
	}
	
	public Set<District> getInvalidDistricts(City city, Set<District> districts){
		Set<District> invalidDistricts = new HashSet<District>();
		for (District district : districts)
			if (!cityContainsDistrict(city, district)) {
				logger.debug(String.format("Discarding district <%s>", district.toString()));
				invalidDistricts.add(district);
			}
		
		return invalidDistricts;
	}
	
	public boolean cityContainsDistrict(City city, District district) {
		logger.debug(String.format("Validating that district <%s> belongs in city with ID <%s>", district.toString(), city.getId()));
		return city.getBarrios().contains(district);
	}
}
