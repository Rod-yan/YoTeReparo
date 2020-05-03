package com.yotereparo.controller.mapping;

/**
 * Interfaz genérica para conversores entre entidades y sus respectivos DTOs.
 * 
 * @author Rodrigo Yanis
 * 
 */
public interface Mapper<entityClazz, dtoClazz> {
	
	dtoClazz convertToDto(entityClazz entity);
	
	entityClazz convertToEntity(dtoClazz dto);
}
