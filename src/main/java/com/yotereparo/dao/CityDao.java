package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.City;

public interface CityDao {
	
	City getCityById(String id);
	 
	List<City> getAllCities();
}
