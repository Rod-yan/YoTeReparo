package com.yotereparo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.RequirementDaoImpl;
import com.yotereparo.model.Requirement;

/**
 * Capa de servicio para Requerimientos.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Gabriel Capitanelli
 * 
 */

@Service("requirementService")
@Transactional 
public class RequirementServiceImpl implements RequirementService {
	
	private static final Logger logger = LoggerFactory.getLogger(RequirementServiceImpl.class);
	
	@Autowired
	private RequirementDaoImpl dao;

	public List<Requirement> getAllRequirements() {
		logger.debug("Fetching all requirements");
		return dao.getAllRequirements();
	}

	public Requirement getRequirementById(Integer id) {
		logger.debug("Fetching requirement by id <{}>", id);
		return dao.getRequirementById(id);
	}
	
	public Requirement getRequirementByDescription(String description) {
		logger.debug("Fetching requirement by description <{}>", description);
		return dao.getRequirementByDescription(description);
	}
}
