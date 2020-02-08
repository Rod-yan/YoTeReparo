package com.yotereparo.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="servicio")
public class Service {
	
	@Id
	@Column(name = "id_servicio", nullable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="id_usuario_prestador", nullable=false, updatable = false, insertable = true)
	private User usuarioPrestador;
	
	private String descripcion;
	
	private String disponibilidad;
	
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
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_creacion", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaCreacion;
	
	private String estado;
	
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE},fetch=FetchType.EAGER)
    @JoinTable(name="servicio_mediodepago",
        joinColumns = {@JoinColumn(name="id_servicio")},
        inverseJoinColumns = {@JoinColumn(name="id_mediodepago")}    
    )
	private Set<PaymentMethod> mediosDePago = new HashSet<PaymentMethod>(0);
	
	//TODO: Tipo servicio
	
	//TODO: Requerimiento
	
	//TODO: Contrato ?
	
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

	public void setPrecioPromedio() {
		this.precioPromedio = (precioMaximo+precioMinimo)/2;
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
    public void removeDireccion(PaymentMethod medioDePago) {
    	mediosDePago.remove(medioDePago);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cantidadTrabajadores == null) ? 0 : cantidadTrabajadores.hashCode());
		result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
		result = prime * result + ((disponibilidad == null) ? 0 : disponibilidad.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + (facturaEmitida ? 1231 : 1237);
		result = prime * result + ((fechaCreacion == null) ? 0 : fechaCreacion.hashCode());
		result = prime * result + ((horasEstimadasEjecucion == null) ? 0 : horasEstimadasEjecucion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + Arrays.hashCode(imagen);
		result = prime * result + ((mediosDePago == null) ? 0 : mediosDePago.hashCode());
		result = prime * result + ((precioAdicionales == null) ? 0 : precioAdicionales.hashCode());
		result = prime * result + ((precioInsumos == null) ? 0 : precioInsumos.hashCode());
		result = prime * result + ((precioMaximo == null) ? 0 : precioMaximo.hashCode());
		result = prime * result + ((precioMinimo == null) ? 0 : precioMinimo.hashCode());
		result = prime * result + ((precioPromedio == null) ? 0 : precioPromedio.hashCode());
		result = prime * result + ((usuarioPrestador == null) ? 0 : usuarioPrestador.hashCode());
		return result;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (precioPromedio == null) {
			if (other.precioPromedio != null)
				return false;
		} else if (!precioPromedio.equals(other.precioPromedio))
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
		return "Service [id=" + id + ", usuarioPrestador=" + usuarioPrestador + ", descripcion=" + descripcion
				+ ", disponibilidad=" + disponibilidad + ", precioMaximo=" + precioMaximo + ", precioMinimo="
				+ precioMinimo + ", precioPromedio=" + precioPromedio + ", precioInsumos=" + precioInsumos
				+ ", precioAdicionales=" + precioAdicionales + ", horasEstimadasEjecucion=" + horasEstimadasEjecucion
				+ ", cantidadTrabajadores=" + cantidadTrabajadores + ", facturaEmitida=" + facturaEmitida + ", imagen="
				+ Arrays.toString(imagen) + ", fechaCreacion=" + fechaCreacion + ", estado=" + estado
				+ ", mediosDePago=" + mediosDePago + "]";
	}
}
