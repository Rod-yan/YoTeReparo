package com.yotereparo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.DistrictDaoImpl;
import com.yotereparo.model.District;

/**
 * Capa de servicio para Barrios.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Service("districtService")
@Transactional 
public class DistrictServiceImpl implements DistrictService {
	
	private static final Logger logger = LoggerFactory.getLogger(DistrictServiceImpl.class);
	
	@Autowired
	private DistrictDaoImpl dao;

	public List<District> getAllDistricts() {
		logger.debug("Fetching all districts");
		return dao.getAllDistricts();
	}

	public District getDistrictById(Integer id) {
		logger.debug("Fetching district <{}>", id);
		return dao.getDistrictById(id);
	}
}
