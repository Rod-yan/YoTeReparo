package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.City;
import com.yotereparo.model.District;
import com.yotereparo.model.PaymentMethod;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;

public interface ServiceManager {
    
    void createService(Service service);
     
    void updateService(Service service);
    
    void enableServiceById(Integer id);
    
    void disableServiceById(Integer id);
    
    void deleteServiceById(Integer id);
     
    Service getServiceById(Integer id);
    
    void updateServicePhotoById(Integer id, byte[] b64photo);
    
    boolean exist(Integer id);
    
    List<Service> getAllServices(); 
    
    List<Service> getAllServices(User user);
    
    List<Service> getAllServices(District district);
    
    List<Service> getAllServices(City city);
    
    List<Service> getAllServices(PaymentMethod paymentMethod);
    
    //List<Service> getAllServices(ServiceType serviceType);
    
    //List<Service> getAllServices(Requirement requirement);
    
    //List<Service> getAllServices(CompositeFilter filter);
}
