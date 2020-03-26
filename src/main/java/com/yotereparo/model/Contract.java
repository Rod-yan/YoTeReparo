package com.yotereparo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="contrato")
public class Contract {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_contrato", nullable = false)
	private Integer id;
		
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="id_servicio", nullable=false, updatable = false, insertable = true)
	private Service servicio;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="id_usuario_final", nullable=false, updatable = false, insertable = true)
	private User usuarioFinal;
		
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_contratacion", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaContratacion;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_inicio_ejecucion", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaInicioEjecucion;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_fin_ejecucion", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaFinEjecucion;
	
	private String descripcion;
	
	@Column(name = "precio_final", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = false)
	private Float precioFinal;
	
	@Column(name = "incluye_insumos", nullable = false)
	private Boolean incluyeInsumos;
	
	@Column(name = "incluye_adicionales", nullable = false)
	private Boolean incluyeAdicionales;
	
	private Integer valoracion;
	
	@Column(name = "descripcion_valoracion", nullable = false)
	private String descripcionValoracion;
	
	private String estado;
	
	public Contract() { }

	/* Getters & Setters */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Service getServicio() {
		return servicio;
	}

	public void setServicio(Service servicio) {
		this.servicio = servicio;
	}

	public User getUsuarioFinal() {
		return usuarioFinal;
	}

	public void setUsuarioFinal(User usuarioFinal) {
		this.usuarioFinal = usuarioFinal;
	}

	public DateTime getFechaContratacion() {
		return fechaContratacion;
	}

	public void setFechaContratacion(DateTime fechaContratacion) {
		this.fechaContratacion = fechaContratacion;
	}

	public DateTime getFechaInicioEjecucion() {
		return fechaInicioEjecucion;
	}

	public void setFechaInicioEjecucion(DateTime fechaInicioEjecucion) {
		this.fechaInicioEjecucion = fechaInicioEjecucion;
	}

	public DateTime getFechaFinEjecucion() {
		return fechaFinEjecucion;
	}

	public void setFechaFinEjecucion(DateTime fechaFinEjecucion) {
		this.fechaFinEjecucion = fechaFinEjecucion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Float getPrecioFinal() {
		return precioFinal;
	}

	public void setPrecioFinal(Float precioFinal) {
		this.precioFinal = precioFinal;
	}

	public Boolean getIncluyeInsumos() {
		return incluyeInsumos;
	}

	public void setIncluyeInsumos(Boolean incluyeInsumos) {
		this.incluyeInsumos = incluyeInsumos;
	}

	public Boolean getIncluyeAdicionales() {
		return incluyeAdicionales;
	}

	public void setIncluyeAdicionales(Boolean incluyeAdicionales) {
		this.incluyeAdicionales = incluyeAdicionales;
	}

	public Integer getValoracion() {
		return valoracion;
	}

	public void setValoracion(Integer valoracion) {
		this.valoracion = valoracion;
	}

	public String getDescripcionValoracion() {
		return descripcionValoracion;
	}

	public void setDescripcionValoracion(String descripcionValoracion) {
		this.descripcionValoracion = descripcionValoracion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contract other = (Contract) obj;
		if (servicio == null) {
			if (other.servicio != null)
				return false;
		} else if (!servicio.equals(other.servicio))
			return false;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (usuarioFinal == null) {
			if (other.usuarioFinal != null)
				return false;
		} else if (!usuarioFinal.equals(other.usuarioFinal))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (valoracion != other.valoracion)
			return false;
		if (fechaContratacion == null) {
			if (other.fechaContratacion != null)
				return false;
		} else if (!fechaContratacion.equals(other.fechaContratacion))
			return false;
		if (fechaInicioEjecucion == null) {
			if (other.fechaInicioEjecucion != null)
				return false;
		} else if (!fechaInicioEjecucion.equals(other.fechaInicioEjecucion))
			return false;
		if (fechaFinEjecucion == null) {
			if (other.fechaFinEjecucion != null)
				return false;
		} else if (!fechaFinEjecucion.equals(other.fechaFinEjecucion))
			return false;
		if (precioFinal == null) {
			if (other.precioFinal != null)
				return false;
		} else if (!precioFinal.equals(other.precioFinal))
			return false;
		if (incluyeAdicionales != other.incluyeAdicionales)
			return false;
		if (incluyeInsumos != other.incluyeInsumos)
			return false;
		if (descripcionValoracion == null) {
			if (other.descripcionValoracion != null)
				return false;
		} else if (!descripcionValoracion.equals(other.descripcionValoracion))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contract [id=" + id + ", servicio=" + servicio.getId() + ", usuarioFinal=" + usuarioFinal.getId()
				+ ", fechaContratacion=" + fechaContratacion + ", fechaInicioEjecucion=" + fechaInicioEjecucion
				+ ", fechaFinEjecucion=" + fechaFinEjecucion + ", descripcion=" + descripcion + ", precioFinal="
				+ precioFinal + ", incluyeInsumos=" + incluyeInsumos + ", incluyeAdicionales=" + incluyeAdicionales
				+ ", valoracion=" + valoracion + ", descripcionValoracion=" + descripcionValoracion + ", estado="
				+ estado + "]";
	}
}