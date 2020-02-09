package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.ServiceType;

public interface ServiceTypeDao {
	
	ServiceType getServiceTypeById(Integer id);
	
	ServiceType getServiceTypeByDescription(String description);
	 
	List<ServiceType> getAllServiceTypes();
}
