package com.yotereparo.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="servicio")
public class Service {
	// Constantes de estado
	public static final String ACTIVE = "ACTIVO";
	public static final String INACTIVE = "INACTIVO";
	public static final String BLOCKED = "BLOQUEADO";
	public static final String ARCHIVED = "ARCHIVADO";
	
	public static final String STATUS_LIST_REGEXP =
			ACTIVE + "|" +
			INACTIVE + "|" +
			BLOCKED + "|" +
			ARCHIVED;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_servicio", nullable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="id_usuario_prestador", nullable=false, updatable = false, insertable = true)
	private User usuarioPrestador;
	
	private String titulo;
	
	private String descripcion;
	
	private String disponibilidad;
	
	private boolean insitu;
	
	@Column(name = "precio_maximo", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = false)
	private Float precioMaximo;
	
	@Column(name = "precio_minimo", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = false)
	private Float precioMinimo;
	
	@Transient
	private Float precioPromedio;
	
	@Column(name = "precio_insumos", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = true)
	private Float precioInsumos;
	
	@Column(name = "precio_adicionales", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = true)
	private Float precioAdicionales;
	
	@Column(name = "horas_estimadas_ejecucion", columnDefinition = "NUMERIC", precision = 7, scale = 2, nullable = false)
	private Float horasEstimadasEjecucion;
	
	@Column(name = "cantidad_trabajadores", nullable = false)
	private Integer cantidadTrabajadores;
	
	@Column(name = "factura_emitida", nullable = false)
	private boolean facturaEmitida;
	
	private byte[] imagen;
	
	private byte[] thumbnail;
	
	@ManyToOne
    @JoinColumn(name="id_tiposervicio", nullable=false, updatable = true, insertable = true)
	private ServiceType tipoServicio;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_creacion", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaCreacion;
	
	private String estado;
	
	@ManyToMany(cascade=CascadeType.MERGE,fetch=FetchType.EAGER)
    @JoinTable(name="servicio_mediodepago",
        joinColumns = {@JoinColumn(name="id_servicio")},
        inverseJoinColumns = {@JoinColumn(name="id_mediodepago")}    
    )
	private Set<PaymentMethod> mediosDePago = new HashSet<PaymentMethod>(0);
	
	@ManyToMany(cascade=CascadeType.MERGE,fetch=FetchType.EAGER)
    @JoinTable(name="servicio_requerimiento",
        joinColumns = {@JoinColumn(name="id_servicio")},
        inverseJoinColumns = {@JoinColumn(name="id_requerimiento")}    
    )
	private Set<Requirement> requerimientos = new HashSet<Requirement>(0);
	
	@OneToMany(mappedBy = "servicio", fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
	@Where(clause = "estado <> '"+Quote.ARCHIVED+"'")
	private Set<Quote> presupuestos = new HashSet<Quote>(0);
	
	@OneToMany(mappedBy = "servicio", fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
	@Where(clause = "estado <> '"+Message.ARCHIVED+"'")
	private Set<Message> mensajes = new HashSet<Message>(0);
		
	public Service() { }
	
	/* Getters & Setters */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUsuarioPrestador() {
		return usuarioPrestador;
	}

	public void setUsuarioPrestador(User usuarioPrestador) {
		this.usuarioPrestador = usuarioPrestador;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDisponibilidad() {
		return disponibilidad;
	}

	public void setDisponibilidad(String disponibilidad) {
		this.disponibilidad = disponibilidad;
	}

	public boolean isInsitu() {
		return insitu;
	}

	public void setInsitu(boolean insitu) {
		this.insitu = insitu;
	}

	public Float getPrecioMaximo() {
		return precioMaximo;
	}

	public void setPrecioMaximo(Float precioMaximo) {
		this.precioMaximo = precioMaximo;
	}

	public Float getPrecioMinimo() {
		return precioMinimo;
	}
	public void setPrecioMinimo(Float precioMinimo) {
		this.precioMinimo = precioMinimo;
	}

	public Float getPrecioPromedio() {
		return precioPromedio;
	}
	public void setPrecioPromedio(Float precioPromedio) {
		this.precioPromedio = (float) Math.ceil((precioMaximo+precioMinimo)/2);
	}

	public Float getPrecioInsumos() {
		return precioInsumos;
	}
	public void setPrecioInsumos(Float precioInsumos) {
		this.precioInsumos = precioInsumos;
	}

	public Float getPrecioAdicionales() {
		return precioAdicionales;
	}
	public void setPrecioAdicionales(Float precioAdicionales) {
		this.precioAdicionales = precioAdicionales;
	}

	public Float getHorasEstimadasEjecucion() {
		return horasEstimadasEjecucion;
	}
	public void setHorasEstimadasEjecucion(Float horasEstimadasEjecucion) {
		this.horasEstimadasEjecucion = horasEstimadasEjecucion;
	}

	public Integer getCantidadTrabajadores() {
		return cantidadTrabajadores;
	}
	public void setCantidadTrabajadores(Integer cantidadTrabajadores) {
		this.cantidadTrabajadores = cantidadTrabajadores;
	}

	public boolean isFacturaEmitida() {
		return facturaEmitida;
	}
	public void setFacturaEmitida(boolean facturaEmitida) {
		this.facturaEmitida = facturaEmitida;
	}

	public byte[] getImagen() {
		return imagen;
	}
	public void setImagen(byte[] imagen) {
		this.imagen = imagen;
	}

	public byte[] getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public ServiceType getTipoServicio() {
		return tipoServicio;
	}
	public void setTipoServicio(ServiceType tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	public DateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Set<PaymentMethod> getMediosDePago() {
		return mediosDePago;
	}
	public void setMediosDePago(Set<PaymentMethod> mediosDePago) {
		this.mediosDePago = mediosDePago;
	}
	public void addMedioDePago(PaymentMethod medioDePago) {
		mediosDePago.add(medioDePago);
    }
    public void removeMedioDePago(PaymentMethod medioDePago) {
    	mediosDePago.remove(medioDePago);
    }
    
	public Set<Requirement> getRequerimientos() {
		return requerimientos;
	}
	public void setRequerimientos(Set<Requirement> requerimientos) {
		this.requerimientos = requerimientos;
	}
	public void addRequerimiento(Requirement requerimiento) {
		requerimientos.add(requerimiento);
    }
    public void removeRequerimiento(Requirement requerimiento) {
    	requerimientos.remove(requerimiento);
    }
    
    public Set<Quote> getPresupuestos() {
		return presupuestos;
	}
	public void setPresupuestos(Set<Quote> presupuestos) {
		this.presupuestos = presupuestos;
	}
	public void addPresupuesto(Quote presupuesto) {
		presupuestos.add(presupuesto);
    }
    public void removePresupuesto(Quote presupuesto) {
    	presupuestos.remove(presupuesto);
    }

	public Set<Message> getMensajes() {
		return mensajes;
	}
	public void setMensajes(Set<Message> mensajes) {
		this.mensajes = mensajes;
	}
	public void addMensaje(Message mensaje) {
		mensajes.add(mensaje);
    }
    public void removeMensaje(Message mensaje) {
    	mensajes.remove(mensaje);
    }

	public boolean similarTo(Service service) {
		if (service == null)
			return false;
		if (this.id == service.id)
			return false;
		if (this.titulo.equalsIgnoreCase(service.titulo) ||
				( this.descripcion.equalsIgnoreCase(service.descripcion) &&
				  this.disponibilidad.equalsIgnoreCase(service.disponibilidad) &&
				  this.insitu == service.insitu &&
				  this.precioMaximo.equals(service.precioMaximo) &&
				  this.precioMinimo.equals(service.precioMinimo) &&
				  this.precioInsumos.equals(service.precioInsumos) &&
				  this.precioAdicionales.equals(service.precioAdicionales) &&
				  this.horasEstimadasEjecucion.equals(service.horasEstimadasEjecucion) &&
				  this.cantidadTrabajadores.equals(service.cantidadTrabajadores) &&
				  this.facturaEmitida == service.facturaEmitida &&
				  Arrays.equals(imagen, service.imagen) &&
				  Arrays.equals(thumbnail, service.thumbnail) &&
				  this.tipoServicio.equals(service.tipoServicio) &&
				  this.mediosDePago.equals(service.mediosDePago) &&
				  this.requerimientos.equals(service.requerimientos) ))
			return true;
		return false;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Service other = (Service) obj;
		if (cantidadTrabajadores == null) {
			if (other.cantidadTrabajadores != null)
				return false;
		} else if (!cantidadTrabajadores.equals(other.cantidadTrabajadores))
			return false;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (disponibilidad == null) {
			if (other.disponibilidad != null)
				return false;
		} else if (!disponibilidad.equals(other.disponibilidad))
			return false;
		if (insitu != other.insitu)
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (facturaEmitida != other.facturaEmitida)
			return false;
		if (fechaCreacion == null) {
			if (other.fechaCreacion != null)
				return false;
		} else if (!fechaCreacion.equals(other.fechaCreacion))
			return false;
		if (horasEstimadasEjecucion == null) {
			if (other.horasEstimadasEjecucion != null)
				return false;
		} else if (!horasEstimadasEjecucion.equals(other.horasEstimadasEjecucion))
			return false;
		if (!Arrays.equals(imagen, other.imagen))
			return false;
		if (mediosDePago == null) {
			if (other.mediosDePago != null)
				return false;
		} else if (!mediosDePago.equals(other.mediosDePago))
			return false;
		if (precioAdicionales == null) {
			if (other.precioAdicionales != null)
				return false;
		} else if (!precioAdicionales.equals(other.precioAdicionales))
			return false;
		if (precioInsumos == null) {
			if (other.precioInsumos != null)
				return false;
		} else if (!precioInsumos.equals(other.precioInsumos))
			return false;
		if (precioMaximo == null) {
			if (other.precioMaximo != null)
				return false;
		} else if (!precioMaximo.equals(other.precioMaximo))
			return false;
		if (precioMinimo == null) {
			if (other.precioMinimo != null)
				return false;
		} else if (!precioMinimo.equals(other.precioMinimo))
			return false;
		if (requerimientos == null) {
			if (other.requerimientos != null)
				return false;
		} else if (!requerimientos.equals(other.requerimientos))
			return false;
		if (presupuestos == null) {
			if (other.presupuestos != null)
				return false;
		} else if (!presupuestos.equals(other.presupuestos))
			return false;
		if (mensajes == null) {
			if (other.mensajes != null)
				return false;
		} else if (!mensajes.equals(other.mensajes))
			return false;
		if (!Arrays.equals(thumbnail, other.thumbnail))
			return false;
		if (tipoServicio == null) {
			if (other.tipoServicio != null)
				return false;
		} else if (!tipoServicio.equals(other.tipoServicio))
			return false;
		if (titulo == null) {
			if (other.titulo != null)
				return false;
		} else if (!titulo.equals(other.titulo))
			return false;
		if (usuarioPrestador == null) {
			if (other.usuarioPrestador != null)
				return false;
		} else if (!usuarioPrestador.equals(other.usuarioPrestador))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Service [id=" + id + ", usuarioPrestador=" + usuarioPrestador.getId() + ", titulo=" + titulo + ", descripcion="
				+ descripcion + ", disponibilidad=" + disponibilidad + ", insitu=" + insitu + ", precioMaximo="
				+ precioMaximo + ", precioMinimo=" + precioMinimo + ", precioPromedio=" + precioPromedio
				+ ", precioInsumos=" + precioInsumos + ", precioAdicionales=" + precioAdicionales
				+ ", horasEstimadasEjecucion=" + horasEstimadasEjecucion + ", cantidadTrabajadores="
				+ cantidadTrabajadores + ", facturaEmitida=" + facturaEmitida + ", imagen=" + Arrays.toString(imagen)
				+ ", thumbnail=" + Arrays.toString(thumbnail) + ", tipoServicio=" + tipoServicio + ", fechaCreacion="
				+ fechaCreacion + ", estado=" + estado + ", mediosDePago=" + mediosDePago + ", requerimientos="
				+ requerimientos + ", presupuestos=" + presupuestos + ", mensajes=" + mensajes + "]";
	}
}
