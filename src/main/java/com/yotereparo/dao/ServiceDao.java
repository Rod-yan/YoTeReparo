package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.Service;

public interface ServiceDao {
	
	Service getServiceById(Integer id);
	 
    void createService(Service service);
     
    void deleteServiceById(Integer id);
    
    List<Service> getAllServices(String attributeKey, String attributeValue);
     
    List<Service> getAllServices(Object filter);
}
