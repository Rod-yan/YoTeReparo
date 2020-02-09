package com.yotereparo.service;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.ServiceDaoImpl;
import com.yotereparo.model.City;
import com.yotereparo.model.District;
import com.yotereparo.model.PaymentMethod;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;

/**
 * Capa de servicio para Servicios.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@org.springframework.stereotype.Service("serviceManager")
@Transactional 
public class ServiceManagerImpl implements ServiceManager {
	
	private static final Logger logger = LogManager.getLogger(ServiceManagerImpl.class);
	
	@Autowired
	private ServiceDaoImpl dao;
	
	@Override
	public void createService(Service service) {
		dao.persist(service);		
	}
	
	@Override
	public void updateService(Service service) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void enableServiceById(Integer id) {
		Service service = getServiceById(id);
		if (service != null && service.getEstado() != "ACTIVO") {
			service.setEstado("ACTIVO");
		}
	}
	
	@Override
	public void disableServiceById(Integer id) {
		Service service = getServiceById(id);
		if (service != null && service.getEstado() != "INACTIVO") {
			service.setEstado("INACTIVO");
		}
	}
	
	@Override
	public void deleteServiceById(Integer id) {
		dao.deleteServiceById(id);
	}
	
	@Override
	public Service getServiceById(Integer id) {
		return dao.getByKey(id);
	}
	
	@Override
	public void updateServicePhotoById(Integer id, byte[] b64photo) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean exist(Service service) {
		logger.debug(String.format("Verifying existence of service with description <%s>", service.getDescripcion()));
		User relatedUser = service.getUsuarioPrestador();
		for (Service s : relatedUser.getServicios())
			if (service.equals(s)) 
				return true;
		return false;
	}
	
	@Override
	public boolean exist(Integer id) {
		logger.debug(String.format("Verifying existence of service with id <%s>", id));
		return (getServiceById(id) != null);
	}
	
	@Override
	public List<Service> getAllServices() {
		return dao.getAllServices(null);
	}
	
	@Override
	public List<Service> getAllServices(User user) {
		return dao.getAllServices(user);
	}
	
	@Override
	public List<Service> getAllServices(District district) {
		return dao.getAllServices(district);
	}
	
	@Override
	public List<Service> getAllServices(City city) {
		return dao.getAllServices(city);
	}
	
	@Override
	public List<Service> getAllServices(PaymentMethod paymentMethod) {
		return dao.getAllServices(paymentMethod);
	}
}
