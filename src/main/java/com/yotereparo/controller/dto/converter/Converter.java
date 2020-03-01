package com.yotereparo.controller.dto.converter;

public interface Converter<entityClazz, dtoClazz> {
	
	dtoClazz convertToDto(entityClazz entity);
	
	entityClazz convertToEntity(dtoClazz dto);
}
