package com.yotereparo.service;

import java.util.List;
import java.util.Set;

import com.yotereparo.model.City;
import com.yotereparo.model.District;

public interface CityService {
 
    List<City> getAllCities(); 
     
    City getCityById(String id);
    
    Set<District> getValidDistricts(City city, Set<District> districts);
    
    Set<District> getInvalidDistricts(City city, Set<District> districts);
    
    boolean exist(String id);
 
    boolean hasUniqueId(String id);
    
    boolean cityContainsDistrict(City city, District district);
}
