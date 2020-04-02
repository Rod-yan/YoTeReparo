package com.yotereparo.controller.dto.converter;

/**
 * Interfaz genérica para conversores entre entidades y sus respectivos DTOs.
 * 
 * @author Rodrigo Yanis
 * 
 */
public interface Converter<entityClazz, dtoClazz> {
	
	dtoClazz convertToDto(entityClazz entity);
	
	entityClazz convertToEntity(dtoClazz dto);
}
