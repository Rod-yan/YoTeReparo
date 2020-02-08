package com.yotereparo.controller.dto.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public abstract class Converter {
	@Autowired
    ModelMapper modelMapper;
	@Autowired
    MessageSource messageSource;
}
