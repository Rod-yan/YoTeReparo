package com.yotereparo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
		if (userService.isProvider(service.getUsuarioPrestador()))
			if (service.getUsuarioPrestador().getDirecciones() != null && service.getUsuarioPrestador().getDirecciones().size() != 0) {
				service.setFechaCreacion(new DateTime());
				// No cargamos imagenes en tiempo de creacion, siempre usar el metodo dedicado
				service.setImagen(null);
				service.setThumbnail(null);
				service.setEstado("ACTIVO");
				
				logger.info(String.format("Commiting creation of service <%s>", service.getTitulo()));
				dao.persist(service);
			}
			else {
				logger.debug(String.format("Service <%s> can't be created. User <%s> has no registered address", service.getTitulo(), service.getUsuarioPrestador().getId()));
				throw new CustomResponseError("Service","usuarioPrestador",messageSource.getMessage("service.usuarioPrestador.addresses.is.empty", new String[]{service.getUsuarioPrestador().getId()}, Locale.getDefault()));
			}
		else {
			logger.debug(String.format("Service <%s> can't be created. User <%s> is not of type Prestador", service.getTitulo(), service.getUsuarioPrestador().getId()));
			throw new CustomResponseError("Service","usuarioPrestador",messageSource.getMessage("service.usuarioPrestador.unauthorized", new String[]{service.getUsuarioPrestador().getId()}, Locale.getDefault()));
		}
			
	}
	
	@Override
	public void updateService(Service service) {
		Service entity = getServiceById(service.getId());
		
		if (!service.getUsuarioPrestador().getId().equals(entity.getUsuarioPrestador().getId())) {
			// Illegal
			logger.debug(String.format("Service <%s> owner: <%s> can't be modified!", service.getId(), entity.getUsuarioPrestador().getId()));
			throw new CustomResponseError("Service","usuarioPrestador",messageSource.getMessage("service.usuarioPrestador.cant.change", null, Locale.getDefault()));
		}
		
		if (!service.getTitulo().equals(entity.getTitulo())) {
			logger.debug(String.format("Updating attribute 'Titulo' from service <%s>", service.getId()));
			entity.setTitulo(service.getTitulo());
		}
		
		if (!service.getDescripcion().equals(entity.getDescripcion())) {
			logger.debug(String.format("Updating attribute 'Descripcion' from service <%s>", service.getId()));
			entity.setDescripcion(service.getDescripcion());
		}
		
		if (service.getDisponibilidad() != null) {
			if (!service.getDisponibilidad().equals(entity.getDisponibilidad())) {
				logger.debug(String.format("Updating attribute 'Disponibilidad' from service <%s>", service.getId()));
				entity.setDisponibilidad(service.getDisponibilidad());
			}
		}
		else {
			if (entity.getDisponibilidad() != null) {
				logger.debug(String.format("Updating attribute 'Disponibilidad' from service <%s>", service.getId()));
				entity.setDisponibilidad(null);
			}
		}
		
		Boolean precioHasChanged = false;
		if (!service.getPrecioMaximo().equals(entity.getPrecioMaximo())) {
			logger.debug(String.format("Updating attribute 'PrecioMaximo' from service <%s>", service.getId()));
			entity.setPrecioMaximo(service.getPrecioMaximo());
			precioHasChanged = true;
		}
		
		if (!service.getPrecioMinimo().equals(entity.getPrecioMinimo())) {
			logger.debug(String.format("Updating attribute 'PrecioMinimo' from service <%s>", service.getId()));
			entity.setPrecioMinimo(service.getPrecioMinimo());
			precioHasChanged = true;
		}
		
		if (precioHasChanged) {
			logger.debug(String.format("Updating attribute 'PrecioPromedio' from service <%s>", service.getId()));
			entity.setPrecioPromedio(null);
		}
		
		if (service.getPrecioInsumos() != null) {
			if (!service.getPrecioInsumos().equals(entity.getPrecioInsumos())) {
				logger.debug(String.format("Updating attribute 'PrecioInsumos' from service <%s>", service.getId()));
				entity.setPrecioInsumos(service.getPrecioInsumos());
			}
		}
		else {
			if (entity.getPrecioInsumos() != null) {
				logger.debug(String.format("Updating attribute 'PrecioInsumos' from service <%s>", service.getId()));
				entity.setPrecioInsumos(null);
			}
		}
		
		if (service.getPrecioAdicionales() != null) {
			if (!service.getPrecioAdicionales().equals(entity.getPrecioAdicionales())) {
				logger.debug(String.format("Updating attribute 'PrecioAdicionales' from service <%s>", service.getId()));
				entity.setPrecioAdicionales(service.getPrecioAdicionales());
			}
		}
		else {
			if (entity.getPrecioAdicionales() != null) {
				logger.debug(String.format("Updating attribute 'PrecioAdicionales' from service <%s>", service.getId()));
				entity.setPrecioAdicionales(null);
			}
		}
		
		if (!service.getHorasEstimadasEjecucion().equals(entity.getHorasEstimadasEjecucion())) {
			logger.debug(String.format("Updating attribute 'HorasEstimadasEjecucion' from service <%s>", service.getId()));
			entity.setHorasEstimadasEjecucion(service.getHorasEstimadasEjecucion());
		}
		
		if (!service.getCantidadTrabajadores().equals(entity.getCantidadTrabajadores())) {
			logger.debug(String.format("Updating attribute 'CantidadTrabajadores' from service <%s>", service.getId()));
			entity.setCantidadTrabajadores(service.getCantidadTrabajadores());
		}
		
		if (!service.isFacturaEmitida() == entity.isFacturaEmitida()) {
			logger.debug(String.format("Updating attribute 'FacturaEmitida' from service <%s>", service.getId()));
			entity.setFacturaEmitida(service.isFacturaEmitida());
		}
		
		if (!service.getTipoServicio().equals(entity.getTipoServicio())) {
			logger.debug(String.format("Updating attribute 'TipoServicio' from service <%s>", service.getId()));
			entity.setTipoServicio(service.getTipoServicio());
		}
		
		if (!service.getMediosDePago().equals(entity.getMediosDePago())) {
			logger.debug(String.format("Updating attribute 'MediosDePago' from service <%s>", service.getId()));
			entity.getMediosDePago().clear();
			entity.setMediosDePago(service.getMediosDePago());
		}
		
		if (service.getRequerimientos() != null && !service.getRequerimientos().isEmpty()) {
			if (!service.getRequerimientos().equals(entity.getRequerimientos())) {
				logger.debug(String.format("Updating attribute 'Requerimientos' from service <%s>", service.getId()));
				entity.getRequerimientos().clear();
				entity.setRequerimientos(service.getRequerimientos());
			}
		}
		else {
			if (entity.getRequerimientos() != null && !entity.getRequerimientos().isEmpty()) {
				logger.debug(String.format("Updating attribute 'Requerimientos' from service <%s>", service.getId()));
				entity.getRequerimientos().clear();
			}
		}
		
		logger.info(String.format("Commiting update for service <%s>", service.getId()));
	}
	
	@Override
	public void enableServiceById(Integer id) {
		Service service = getServiceById(id);
		if (service != null && service.getEstado() != "ACTIVO") {
			logger.info(String.format("Enabling service <%s>", service.getId()));
			service.setEstado("ACTIVO");
		}
	}
	
	@Override
	public void disableServiceById(Integer id) {
		Service service = getServiceById(id);
		if (service != null && service.getEstado() != "INACTIVO") {
			logger.info(String.format("Disabling service <%s>", service.getId()));
			service.setEstado("INACTIVO");
		}
	}
	
	@Override
	public void deleteServiceById(Integer id) {
		logger.info(String.format("Commiting deletion of service <%s>", id));
		dao.deleteServiceById(id);
	}
	
	@Override
	public Service getServiceById(Integer id) {
		logger.debug(String.format("Fetching service <%s>", id));
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
		        logger.debug(String.format("Updating attribute 'Imagen' from service <%s>",id));
		        entity.setImagen(baos.toByteArray());
		        
		        baos.reset();
	        	ImageIO.write(serviceThumbnail, "png", baos);
	        	logger.debug(String.format("Updating attribute 'Thumbnail' from service <%s>",id));
		        entity.setThumbnail(baos.toByteArray());
		        
		        img.flush();
		        serviceImage.flush();
		        serviceThumbnail.flush();
		        baos.close();
	        }
	        catch (IOException e) {
	        	logger.error("IOException: "+e.getMessage());
		        throw new RuntimeException(e.getMessage());
	        }
	        finally {
	        	logger.info(String.format("Commiting update for service <%s>", id));
	        }
		}
		else {
			if (entity.getImagen() != null || entity.getThumbnail() != null) {
				logger.debug(String.format("Deleting attribute 'Imagen' and 'Thumbnail' from service <%s>",id));
				
				entity.setImagen(null);
				entity.setThumbnail(null);
			}
			
			logger.debug(String.format("No 'Imagen' nor 'Thumbnail' registered for service <%s>, discarding transaction.",id));
		}
	}
	
	@Override
	public boolean similarExist(Service service) {
		logger.debug(String.format("Verifying that service with title <%s> is not similar to already registered services", service.getTitulo()));
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
		List<Service> services = null;
		Entry <String, String> filter = filters.entrySet().iterator().next();
		String filterKey = filter.getKey().toLowerCase();
		String filterValue = filter.getValue().toLowerCase();
		switch (filterKey) {
			case "user":
				User user = userService.getUserById(filterValue);
				logger.debug("Fetching all services by user: <"+filterValue+">");
				if (user != null)
					services = dao.getAllServices(user);
				break;
			case "district":
				try {
					District district = districtService.getDistrictById(Integer.parseInt(filterValue));
					logger.debug("Fetching all services by district: <"+filterValue+">");
					if (district != null)
						services = dao.getAllServices(district);
				}
				catch (NumberFormatException e) { }
				break;
			case "city":
				City city = cityService.getCityById(filterValue);
				logger.debug("Fetching all services by city: <"+filterValue+">");
				if (city != null)
					services = dao.getAllServices(city);
				break;
			// TODO: más filtros y filtro compuesto
		}
		return services;
	}
}
