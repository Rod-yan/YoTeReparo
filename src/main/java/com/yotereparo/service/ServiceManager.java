package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.Service;

public interface ServiceManager {
    
    void createService(Service service);
     
    void updateService(Service service);
    
    void enableServiceById(Integer id);
    
    void disableServiceById(Integer id);
    
    void deleteServiceById(Integer id);
     
    Service getServiceById(Integer id);
    
    void updateServiceImageById(Integer id, byte[] image);
    
    boolean similarExist(Service service);
    
    boolean exist(Integer id);
    
    List<Service> getAllServices(); 
    
    List<Service> getAllServices(Object filter);
}
