package com.yotereparo.configuration;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.User;
 
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.yotereparo")
public class AppConfig {
	
	@Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("ValidationMessages");
        return messageSource;
    }
	
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	    converters.add(byteArrayHttpMessageConverter());
	}
	 
	@Bean
	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
	    ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
	    arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());
	    return arrayHttpMessageConverter;
	}
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.createTypeMap(UserDto.class, User.class).addMappings(mapper -> mapper.skip(User::setCiudad));
		modelMapper.createTypeMap(User.class, UserDto.class).addMappings(mapper -> {
			mapper.map(src -> src.getCiudad().getId(), UserDto::setCiudad);
		});
	    return modelMapper;
	}
	 
	private List<MediaType> getSupportedMediaTypes() {
	    List<MediaType> list = new ArrayList<MediaType>();
	    list.add(MediaType.IMAGE_JPEG);
	    list.add(MediaType.IMAGE_PNG);
	    list.add(MediaType.APPLICATION_OCTET_STREAM);
	    return list;
	}
}
