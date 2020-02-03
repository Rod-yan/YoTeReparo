package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.City;
import com.yotereparo.model.District;
import com.yotereparo.model.PaymentMethod;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;

public interface ServiceManager {
    
    void createService(Service Service);
     
    void updateService(Service Service);
    
    void enableServiceById(int id);
    
    void disableServiceById(int id);
    
    void deleteServiceById(int id);
     
    Service getServiceById(int id);
    
    void updateServicePhotoById(int id, byte[] b64photo);
    
    boolean exist(int id);
    
    List<Service> getAllServices(); 
    
    List<Service> getAllServices(User user);
    
    List<Service> getAllServices(District district);
    
    List<Service> getAllServices(City city);
    
    List<Service> getAllServices(PaymentMethod paymentMethod);
    
    //List<Service> getAllServices(ServiceType serviceType);
    
    //List<Service> getAllServices(Requirement requirement);
    
    //List<Service> getAllServices(CompositeFilter filter);
}
