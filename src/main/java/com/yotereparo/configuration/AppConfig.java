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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
 
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.yotereparo")
public class AppConfig implements WebMvcConfigurer {
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }
	
	@Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("ValidationMessages");
        return messageSource;
    }
	
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	    converters.add(byteArrayHttpMessageConverter());
	    converters.add(mappingJackson2HttpMessageConverter());
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
	    return jackson2HttpMessageConverter;
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
		// UserDto -> User
		modelMapper.createTypeMap(UserDto.class, User.class).addMappings(mapper -> {
			mapper.skip(User::setCiudad);
			mapper.skip(User::setServicios);
			mapper.skip(User::setPresupuestos);
		});
		// User -> UserDto
		modelMapper.createTypeMap(User.class, UserDto.class).addMappings(mapper -> {
			mapper.map(src -> src.getCiudad().getId(), UserDto::setCiudad);
			mapper.skip(UserDto::setServicios);
			mapper.skip(UserDto::setPresupuestos);
		});
		// ServiceDto -> Service
		modelMapper.createTypeMap(ServiceDto.class, Service.class).addMappings(mapper -> {
			mapper.skip(Service::setTipoServicio);
			mapper.skip(Service::setUsuarioPrestador);
			mapper.skip(Service::setPresupuestos);
		});
		// Service -> ServiceDto
		modelMapper.createTypeMap(Service.class, ServiceDto.class).addMappings(mapper -> {
			mapper.map(src -> src.getTipoServicio().getDescripcion(), ServiceDto::setTipoServicio);
			mapper.map(src -> src.getUsuarioPrestador().getId(), ServiceDto::setUsuarioPrestador);
			mapper.skip(ServiceDto::setPresupuestos);
		});
		// QuoteDto -> Quote
		modelMapper.createTypeMap(QuoteDto.class, Quote.class).addMappings(mapper -> {
			mapper.skip(Quote::setUsuarioFinal);
			mapper.skip(Quote::setServicio);
		});
		// Quote -> QuoteDto
		modelMapper.createTypeMap(Quote.class, QuoteDto.class).addMappings(mapper -> {
			mapper.map(src -> src.getUsuarioFinal().getId(), QuoteDto::setUsuarioFinal);
			mapper.map(src -> src.getServicio().getId(), QuoteDto::setServicio);
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
