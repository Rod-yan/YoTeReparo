package com.yotereparo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.ServiceDaoImpl;
import com.yotereparo.model.City;
import com.yotereparo.model.District;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.util.error.CustomResponseError;

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
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceManagerImpl.class);
	
	@Autowired
	private ServiceDaoImpl dao;
	@Autowired
    private MessageSource messageSource;
	@Autowired
    private UserService userService;
	@Autowired
	private DistrictService districtService;
	@Autowired
	private CityService cityService;
	
	@Override
	public void createService(Service service) {
		User user = service.getUsuarioPrestador();
		if (userService.isProvider(user))
			if (user.getDirecciones() != null && user.getDirecciones().size() != 0) {
				service.setFechaCreacion(new DateTime());
				service.setEstado(Service.ACTIVE);
				
				logger.info("Commiting creation of service <{}>", service.getTitulo());
				dao.persist(service);
			}
			else {
				logger.debug("Service <{}> can't be created. User <{}> has no registered address", 
						service.getTitulo(), user.getId());
				throw new CustomResponseError("Service","usuarioPrestador",
						messageSource.getMessage("service.usuarioPrestador.addresses.is.empty", 
								new String[]{user.getId()}, Locale.getDefault()));
			}
		else {
			logger.debug("Service <{}> can't be created. User <{}> is not of type Prestador", 
					service.getTitulo(), user.getId());
			throw new CustomResponseError("Service","usuarioPrestador",
					messageSource.getMessage("service.usuarioPrestador.unauthorized", 
							new String[]{user.getId()}, Locale.getDefault()));
		}
			
	}
	
	@Override
	public void updateService(Service service) {
		Service entity = getServiceById(service.getId());
		
		if (!service.getUsuarioPrestador().getId().equals(entity.getUsuarioPrestador().getId())) {
			// Illegal
			logger.debug("Service <{}> owner: <{}> can't be modified!", 
					service.getId(), entity.getUsuarioPrestador().getId());
			throw new CustomResponseError("Service","usuarioPrestador",
					messageSource.getMessage("service.usuarioPrestador.cant.change", 
							null, Locale.getDefault()));
		}
		
		if (!service.getTitulo().equals(entity.getTitulo())) {
			logger.debug("Updating attribute 'Titulo' from service <{}>", service.getId());
			entity.setTitulo(service.getTitulo());
		}
		
		if (!service.getDescripcion().equals(entity.getDescripcion())) {
			logger.debug("Updating attribute 'Descripcion' from service <{}>", service.getId());
			entity.setDescripcion(service.getDescripcion());
		}
		
		if (service.getDisponibilidad() != null) {
			if (!service.getDisponibilidad().equals(entity.getDisponibilidad())) {
				logger.debug("Updating attribute 'Disponibilidad' from service <{}>", service.getId());
				entity.setDisponibilidad(service.getDisponibilidad());
			}
		}
		else {
			if (entity.getDisponibilidad() != null) {
				logger.debug("Updating attribute 'Disponibilidad' from service <{}>", service.getId());
				entity.setDisponibilidad(null);
			}
		}
		
		if (!service.isInsitu() == entity.isInsitu()) {
			logger.debug("Updating attribute 'Insitu' from service <{}>", service.getId());
			entity.setInsitu(service.isInsitu());
		}
		
		Boolean precioHasChanged = false;
		if (!service.getPrecioMaximo().equals(entity.getPrecioMaximo())) {
			logger.debug("Updating attribute 'PrecioMaximo' from service <{}>", service.getId());
			entity.setPrecioMaximo(service.getPrecioMaximo());
			precioHasChanged = true;
		}
		
		if (!service.getPrecioMinimo().equals(entity.getPrecioMinimo())) {
			logger.debug("Updating attribute 'PrecioMinimo' from service <{}>", service.getId());
			entity.setPrecioMinimo(service.getPrecioMinimo());
			precioHasChanged = true;
		}
		
		if (precioHasChanged) {
			logger.debug("Updating attribute 'PrecioPromedio' from service <{}>", service.getId());
			entity.setPrecioPromedio(null);
		}
		
		if (service.getPrecioInsumos() != null) {
			if (!service.getPrecioInsumos().equals(entity.getPrecioInsumos())) {
				logger.debug("Updating attribute 'PrecioInsumos' from service <{}>", service.getId());
				entity.setPrecioInsumos(service.getPrecioInsumos());
			}
		}
		else {
			if (entity.getPrecioInsumos() != null) {
				logger.debug("Updating attribute 'PrecioInsumos' from service <{}>", service.getId());
				entity.setPrecioInsumos(null);
			}
		}
		
		if (service.getPrecioAdicionales() != null) {
			if (!service.getPrecioAdicionales().equals(entity.getPrecioAdicionales())) {
				logger.debug("Updating attribute 'PrecioAdicionales' from service <{}>", service.getId());
				entity.setPrecioAdicionales(service.getPrecioAdicionales());
			}
		}
		else {
			if (entity.getPrecioAdicionales() != null) {
				logger.debug("Updating attribute 'PrecioAdicionales' from service <{}>", service.getId());
				entity.setPrecioAdicionales(null);
			}
		}
		
		if (service.getHorasEstimadasEjecucion() != null) {
			if (service.getHorasEstimadasEjecucion() != entity.getHorasEstimadasEjecucion()) {
				logger.debug("Updating attribute 'HorasEstimadasEjecucion' from service <{}>", service.getId());
				entity.setHorasEstimadasEjecucion(service.getHorasEstimadasEjecucion());
			}
		}
		else {
			if (entity.getHorasEstimadasEjecucion() != null) {
				logger.debug("Updating attribute 'HorasEstimadasEjecucion' from service <{}>", service.getId());
				entity.setHorasEstimadasEjecucion(null);
			}
		}
		
		if (!service.getCantidadTrabajadores().equals(entity.getCantidadTrabajadores())) {
			logger.debug("Updating attribute 'CantidadTrabajadores' from service <{}>", service.getId());
			entity.setCantidadTrabajadores(service.getCantidadTrabajadores());
		}
		
		if (!service.isFacturaEmitida() == entity.isFacturaEmitida()) {
			logger.debug("Updating attribute 'FacturaEmitida' from service <{}>", service.getId());
			entity.setFacturaEmitida(service.isFacturaEmitida());
		}
		
		if (!service.getTipoServicio().equals(entity.getTipoServicio())) {
			logger.debug("Updating attribute 'TipoServicio' from service <{}>", service.getId());
			entity.setTipoServicio(service.getTipoServicio());
		}
		
		if (!service.getMediosDePago().equals(entity.getMediosDePago())) {
			logger.debug("Updating attribute 'MediosDePago' from service <{}>", service.getId());
			entity.getMediosDePago().clear();
			entity.setMediosDePago(service.getMediosDePago());
		}
		
		if (service.getRequerimientos() != null && !service.getRequerimientos().isEmpty()) {
			if (!service.getRequerimientos().equals(entity.getRequerimientos())) {
				logger.debug("Updating attribute 'Requerimientos' from service <{}>", service.getId());
				entity.getRequerimientos().clear();
				entity.setRequerimientos(service.getRequerimientos());
			}
		}
		else {
			if (entity.getRequerimientos() != null && !entity.getRequerimientos().isEmpty()) {
				logger.debug("Updating attribute 'Requerimientos' from service <{}>", service.getId());
				entity.getRequerimientos().clear();
			}
		}
		
		logger.info("Commiting update for service <{}>", service.getId());
	}
	
	@Override
	public void enableServiceById(Integer id) {
		Service service = getServiceById(id);
		if (service != null && service.getEstado() != Service.ACTIVE) {
			logger.info("Enabling service <{}>", service.getId());
			service.setEstado(Service.ACTIVE);
		}
	}
	
	@Override
	public void disableServiceById(Integer id) {
		Service service = getServiceById(id);
		if (service != null && service.getEstado() != Service.INACTIVE) {
			logger.info("Disabling service <{}>", service.getId());
			service.setEstado(Service.INACTIVE);
		}
	}
	
	@Override
	public void deleteServiceById(Integer id) {
		logger.info("Commiting deletion of service <{}>", id);
		dao.deleteServiceById(id);
	}
	
	@Override
	public Service getServiceById(Integer id) {
		logger.debug("Fetching service <{}>", id);
		return dao.getByKey(id);
	}
	
	/*
	 *  Actualiza la imagen y el thumbnail del Servicio haciendo un resize de la imagen suscripta,
	 *  Si el parámetro <image> es nulo, eliminamos la imagen y thumbnail actual del servicio.
	 */
	@Override
	public void updateServiceImageById(Integer id, byte[] image) {
		Service entity = dao.getServiceById(id);
		if (image != null) {
	        try {
	        	// Reformateamos la imagen suscripta para normalizar archivos muy grandes, y generamos el thumbnail
	        	InputStream is = new ByteArrayInputStream(image);
		        BufferedImage img = ImageIO.read(is);
		        logger.debug("Resizing input image");
		        BufferedImage serviceImage = Scalr.resize(img, Method.ULTRA_QUALITY,
	                    Mode.AUTOMATIC, 1000, 1000);
		        logger.debug("Building thumbnail from input image");
		        BufferedImage serviceThumbnail = Scalr.resize(img, Method.ULTRA_QUALITY,
	                    Mode.AUTOMATIC, 100, 100);
		        
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        ImageIO.write(serviceImage, "png", baos);
		        logger.debug("Updating attribute 'Imagen' from service <{}>",id);
		        entity.setImagen(baos.toByteArray());
		        
		        baos.reset();
	        	ImageIO.write(serviceThumbnail, "png", baos);
	        	logger.debug("Updating attribute 'Thumbnail' from service <{}>",id);
		        entity.setThumbnail(baos.toByteArray());
		        
		        img.flush();
		        serviceImage.flush();
		        serviceThumbnail.flush();
		        baos.close();
	        }
	        catch (IOException e) {
	        	logger.error("IOException: {}", e.getMessage());
		        throw new RuntimeException(e.getMessage());
	        }
	        finally {
	        	logger.info("Commiting update for service <{}>", id);
	        }
		}
		else {
			if (entity.getImagen() != null || entity.getThumbnail() != null) {
				logger.debug("Deleting attribute 'Imagen' and 'Thumbnail' from service <{}>",id);
				
				entity.setImagen(null);
				entity.setThumbnail(null);
			}
			
			logger.debug("No 'Imagen' nor 'Thumbnail' registered for service <{}>, discarding transaction.",id);
		}
	}
	
	@Override
	public boolean similarExist(Service service) {
		logger.debug("Verifying that service with title <{}> is not similar to already registered services", 
				service.getTitulo());
		User relatedUser = service.getUsuarioPrestador();
		for (Service s : relatedUser.getServicios())
			if (service.equals(s) || service.similarTo(s)) 
				return true;
		return false;
	}
	
	@Override
	public List<Service> getAllServices() {
		logger.debug("Fetching all services");
		return dao.getAllServices(null);
	}
	
	@Override
	public List<Service> getAllServices(Map<String,String> filters) {
		List<Service> filteredServices = new ArrayList<Service>();
		List<Service> serviceListAfterFilters = new ArrayList<Service>();
		if (filters != null && !filters.isEmpty()) {
			String filterKey = null;
			String filterValue = null;
			int entryCount = 0;
			for (Entry <String, String> filter : filters.entrySet()) {
				entryCount++;
				filterKey = filter.getKey().toLowerCase();
				filterValue = filter.getValue().toLowerCase();
				switch (filterKey) {
					case "user":
						User user = userService.getUserById(filterValue);
						logger.debug("Fetching all services - filtering by user: <{}>", filterValue);
						if (user != null)
							filteredServices = dao.getAllServices(user);
						break;
					case "district":
						try {
							District district = districtService.getDistrictById(Integer.parseInt(filterValue));
							logger.debug("Fetching all services - filtering by district: <{}>", filterValue);
							if (district != null)
								filteredServices = dao.getAllServices(district);
						}
						catch (NumberFormatException e) { }
						break;
					case "city":
						City city = cityService.getCityById(filterValue);
						logger.debug("Fetching all services - filtering by city: <{}>", filterValue);
						if (city != null)
							filteredServices = dao.getAllServices(city);
						break;
					case "title":
						logger.debug("Fetching all services - filtering by title: <{}>", filterValue);
						if (filterValue != null && !filterValue.isEmpty())
							filteredServices = dao.getAllServices(filterKey, filterValue);
						break;
					case "description":
						logger.debug("Fetching all services - filtering by description: <{}>", filterValue);
						if (filterValue != null && !filterValue.isEmpty())
							filteredServices = dao.getAllServices(filterKey, filterValue);
						break;
				}
				
				if (entryCount == 1)
					serviceListAfterFilters.addAll(filteredServices);
				else
					serviceListAfterFilters.retainAll(filteredServices);
				
				filteredServices.clear();
				
				if (serviceListAfterFilters.isEmpty())
					break;
			}
		}
		return serviceListAfterFilters;
	}
}
