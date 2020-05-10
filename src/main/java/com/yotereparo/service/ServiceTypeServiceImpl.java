package com.yotereparo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.ServiceTypeDaoImpl;
import com.yotereparo.model.ServiceType;

/**
 * Capa de servicio para Tipos de Servicio.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Service("serviceTypeService")
@Transactional
public class ServiceTypeServiceImpl implements ServiceTypeService {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceTypeServiceImpl.class);
	
	@Autowired
	private ServiceTypeDaoImpl dao;

	public List<ServiceType> getAllServiceTypes() {
		logger.debug("Fetching all service types");
		return dao.getAllServiceTypes();
	}

	public ServiceType getServiceTypeById(Integer id) {
		logger.debug("Fetching service type by id <{}>", id);
		return dao.getServiceTypeById(id);
	}
	
	public ServiceType getServiceTypeByDescription(String description) {
		logger.debug("Fetching service type <{}>", description);
		return dao.getServiceTypeByDescription(description);
	}
}
