package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.Service;

public interface ServiceDao {
	
	Service getServiceById(int id);
	 
    void createService(Service service);
     
    void deleteServiceById(int id);
     
    List<Service> getAllServices();
}
