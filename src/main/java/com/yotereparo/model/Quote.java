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
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="presupuesto")
public class Quote {
	
	// Constantes de estado
	public static final String AWAITING_PROVIDER = "ESPERANDO_USUARIO_PRESTADOR";
	public static final String AWAITING_CUSTOMER = "ESPERANDO_USUARIO_FINAL";
	public static final String ACCEPTED_BY_CUSTOMER = "ACEPTADO_USUARIO_FINAL";
	public static final String REJECTED_BY_CUSTOMER = "RECHAZADO_USUARIO_FINAL";
	public static final String REJECTED_BY_PROVIDER = "RECHAZADO_USUARIO_PRESTADOR";
	public static final String ARCHIVED = "ARCHIVADO";
	
	public static final String STATUS_LIST_REGEXP =
			AWAITING_PROVIDER + "|" +
			AWAITING_CUSTOMER + "|" +
			ACCEPTED_BY_CUSTOMER + "|" +
			REJECTED_BY_CUSTOMER + "|" +
			REJECTED_BY_PROVIDER + "|" +
			ARCHIVED;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_presupuesto", nullable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_servicio", nullable=false)
	private Service servicio;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_usuario_final", nullable=false)
	private User usuarioFinal;
	
	@Column(name = "descripcion_solicitud", nullable = true)
	private String descripcionSolicitud;
	
	@Column(name = "descripcion_respuesta", nullable = true)
	private String descripcionRespuesta;
	
	@Column(name = "precio_presupuestado", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = true)
	private Float precioPresupuestado;
	
	@Transient
	private Float precioTotal;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_inicio_ejecucion_prupuesta", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaInicioEjecucionPropuesta;
	
	@Column(name = "incluye_insumos", nullable = true)
	private boolean incluyeInsumos;
	
	@Column(name = "incluye_adicionales", nullable = true)
	private boolean incluyeAdicionales;
		
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_solicitud", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaSolicitud;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_respuesta", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaRespuesta;
	
	private String estado;
	
	public Quote() { }

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

	public String getDescripcionSolicitud() {
		return descripcionSolicitud;
	}

	public void setDescripcionSolicitud(String descripcionSolicitud) {
		this.descripcionSolicitud = descripcionSolicitud;
	}

	public String getDescripcionRespuesta() {
		return descripcionRespuesta;
	}

	public void setDescripcionRespuesta(String descripcionRespuesta) {
		this.descripcionRespuesta = descripcionRespuesta;
	}

	public Float getPrecioPresupuestado() {
		return precioPresupuestado;
	}

	public void setPrecioPresupuestado(Float precioPresupuestado) {
		this.precioPresupuestado = precioPresupuestado;
	}

	public Float getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal() {
		if (precioPresupuestado == null)
			precioPresupuestado = servicio.getPrecioMinimo();
		this.precioTotal = precioPresupuestado;
		if (incluyeInsumos)
			precioTotal = precioTotal + servicio.getPrecioInsumos();
		if (incluyeAdicionales)
			precioTotal = precioTotal + servicio.getPrecioAdicionales();
	}

	public DateTime getFechaInicioEjecucionPropuesta() {
		return fechaInicioEjecucionPropuesta;
	}

	public void setFechaInicioEjecucionPropuesta(DateTime fechaInicioEjecucionPropuesta) {
		this.fechaInicioEjecucionPropuesta = fechaInicioEjecucionPropuesta;
	}
	
	public boolean isIncluyeInsumos() {
		return incluyeInsumos;
	}

	public void setIncluyeInsumos(boolean incluyeInsumos) {
		this.incluyeInsumos = incluyeInsumos;
	}

	public boolean isIncluyeAdicionales() {
		return incluyeAdicionales;
	}

	public void setIncluyeAdicionales(boolean incluyeAdicionales) {
		this.incluyeAdicionales = incluyeAdicionales;
	}

	public DateTime getFechaSolicitud() {
		return fechaSolicitud;
	}

	public void setFechaSolicitud(DateTime fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	public DateTime getFechaRespuesta() {
		return fechaRespuesta;
	}

	public void setFechaRespuesta(DateTime fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
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
		Quote other = (Quote) obj;
		if (descripcionRespuesta == null) {
			if (other.descripcionRespuesta != null)
				return false;
		} else if (!descripcionRespuesta.equals(other.descripcionRespuesta))
			return false;
		if (descripcionSolicitud == null) {
			if (other.descripcionSolicitud != null)
				return false;
		} else if (!descripcionSolicitud.equals(other.descripcionSolicitud))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (fechaInicioEjecucionPropuesta == null) {
			if (other.fechaInicioEjecucionPropuesta != null)
				return false;
		} else if (!fechaInicioEjecucionPropuesta.equals(other.fechaInicioEjecucionPropuesta))
			return false;
		if (fechaRespuesta == null) {
			if (other.fechaRespuesta != null)
				return false;
		} else if (!fechaRespuesta.equals(other.fechaRespuesta))
			return false;
		if (fechaSolicitud == null) {
			if (other.fechaSolicitud != null)
				return false;
		} else if (!fechaSolicitud.equals(other.fechaSolicitud))
			return false;
		if (incluyeAdicionales != other.incluyeAdicionales)
			return false;
		if (incluyeInsumos != other.incluyeInsumos)
			return false;
		if (precioPresupuestado == null) {
			if (other.precioPresupuestado != null)
				return false;
		} else if (!precioPresupuestado.equals(other.precioPresupuestado))
			return false;
		if (servicio == null) {
			if (other.servicio != null)
				return false;
		} else if (!servicio.equals(other.servicio))
			return false;
		if (usuarioFinal == null) {
			if (other.usuarioFinal != null)
				return false;
		} else if (!usuarioFinal.equals(other.usuarioFinal))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Quote [id=" + id + ", servicio=" + servicio + ", usuarioFinal=" + usuarioFinal
				+ ", descripcionSolicitud=" + descripcionSolicitud + ", descripcionRespuesta=" + descripcionRespuesta
				+ ", precioPresupuestado=" + precioPresupuestado + ", precioTotal=" + precioTotal
				+ ", fechaInicioEjecucionPropuesta=" + fechaInicioEjecucionPropuesta + ", incluyeInsumos="
				+ incluyeInsumos + ", incluyeAdicionales=" + incluyeAdicionales + ", fechaSolicitud=" + fechaSolicitud
				+ ", fechaRespuesta=" + fechaRespuesta + ", estado=" + estado + "]";
	}
}