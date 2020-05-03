package com.yotereparo.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="contrato")
public class Contract {
	// Constantes de estado
	public static final String PENDING_EXECUTION = "PENDIENTE";
	public static final String ONGOING_EXECUTION = "EN_PROCESO";
	public static final String ALREADY_EXECUTED = "FINALIZADO";
	public static final String CANCELED_BY_CUSTOMER = "CANCELADO_USUARIO_FINAL";
	public static final String CANCELED_BY_PROVIDER = "CANCELADO_USUARIO_PRESTADOR";
	public static final String ARCHIVED = "ARCHIVADO";
	
	public static final String STATUS_LIST_REGEXP =
			PENDING_EXECUTION + "|" +
			ONGOING_EXECUTION + "|" +
			ALREADY_EXECUTED + "|" +
			CANCELED_BY_CUSTOMER + "|" +
			CANCELED_BY_PROVIDER + "|" +
			ARCHIVED;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_contrato", nullable = false)
	private Integer id;
		
	@OneToOne(cascade=CascadeType.MERGE, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="id_presupuesto", nullable=false)
	private Quote presupuesto;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_inicio_ejecucion", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaInicioEjecucion;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_fin_ejecucion", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaFinEjecucion;
	
	@Column(name = "precio_final", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = false)
	private Float precioFinal;
	
	private Integer valoracion;
	
	@Column(name = "descripcion_valoracion", nullable = true)
	private String descripcionValoracion;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_valoracion", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaValoracion;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_creacion", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaCreacion;
	
	private String estado;
	
	public Contract() { }

	/* Getters & Setters */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Quote getPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(Quote presupuesto) {
		this.presupuesto = presupuesto;
	}

	public DateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
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

	public Float getPrecioFinal() {
		return precioFinal;
	}

	public void setPrecioFinal(Float precioFinal) {
		this.precioFinal = precioFinal;
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

	public DateTime getFechaValoracion() {
		return fechaValoracion;
	}

	public void setFechaValoracion(DateTime fechaValoracion) {
		this.fechaValoracion = fechaValoracion;
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
		if (descripcionValoracion == null) {
			if (other.descripcionValoracion != null)
				return false;
		} else if (!descripcionValoracion.equals(other.descripcionValoracion))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (fechaValoracion == null) {
			if (other.fechaValoracion != null)
				return false;
		} else if (!fechaValoracion.equals(other.fechaValoracion))
			return false;
		if (fechaCreacion == null) {
			if (other.fechaCreacion != null)
				return false;
		} else if (!fechaCreacion.equals(other.fechaCreacion))
			return false;
		if (fechaFinEjecucion == null) {
			if (other.fechaFinEjecucion != null)
				return false;
		} else if (!fechaFinEjecucion.equals(other.fechaFinEjecucion))
			return false;
		if (fechaInicioEjecucion == null) {
			if (other.fechaInicioEjecucion != null)
				return false;
		} else if (!fechaInicioEjecucion.equals(other.fechaInicioEjecucion))
			return false;
		if (precioFinal == null) {
			if (other.precioFinal != null)
				return false;
		} else if (!precioFinal.equals(other.precioFinal))
			return false;
		if (presupuesto == null) {
			if (other.presupuesto != null)
				return false;
		} else if (!presupuesto.equals(other.presupuesto))
			return false;
		if (valoracion == null) {
			if (other.valoracion != null)
				return false;
		} else if (!valoracion.equals(other.valoracion))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contract [id=" + id + ", presupuesto=" + presupuesto.getId() + ", fechaInicioEjecucion=" + fechaInicioEjecucion
				+ ", fechaFinEjecucion=" + fechaFinEjecucion + ", precioFinal=" + precioFinal + ", valoracion="
				+ valoracion + ", descripcionValoracion=" + descripcionValoracion + ", fechaValoracion="
				+ fechaValoracion + ", fechaCreacion=" + fechaCreacion + ", estado=" + estado + "]";
	}
}