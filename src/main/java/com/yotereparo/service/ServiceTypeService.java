package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.ServiceType;

public interface ServiceTypeService {
 
    List<ServiceType> getAllServiceTypes(); 
     
    ServiceType getServiceTypeById(Integer id);
    
    ServiceType getServiceTypeByDescription(String description);
    
    boolean exist(Integer id);
    
    boolean exist(String description);
}