package com.yotereparo.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
	@Column(name = "fecha_inicio_ejecucion_prupuesta", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaInicioEjecucionPropuesta;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_fin_ejecucion_prupuesta", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaFinEjecucionPropuesta;
	
	@Column(name = "incluye_insumos", nullable = true)
	private boolean incluyeInsumos;
	
	@Column(name = "incluye_adicionales", nullable = true)
	private boolean incluyeAdicionales;
		
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_solicitud", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaSolicitud;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_respuesta", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaRespuesta;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_creacion", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaCreacion;
	
	private String estado;
	
	@ManyToOne(cascade=CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name="id_direccion_usuario_final", nullable=true)
	private Address direccionUsuarioFinal;
	
	@OneToOne(mappedBy = "presupuesto")
	private Contract contrato;
	
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
		this.setPrecioTotal();
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
	
	public DateTime getFechaFinEjecucionPropuesta() {
		return fechaFinEjecucionPropuesta;
	}

	public void setFechaFinEjecucionPropuesta(DateTime fechaFinEjecucionPropuesta) {
		this.fechaFinEjecucionPropuesta = fechaFinEjecucionPropuesta;
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

	public Address getDireccionUsuarioFinal() {
		return direccionUsuarioFinal;
	}

	public void setDireccionUsuarioFinal(Address direccionUsuarioFinal) {
		this.direccionUsuarioFinal = direccionUsuarioFinal;
	}

	public Contract getContrato() {
		return contrato;
	}

	public void setContrato(Contract contrato) {
		this.contrato = contrato;
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
		if (direccionUsuarioFinal == null) {
			if (other.direccionUsuarioFinal != null)
				return false;
		} else if (!direccionUsuarioFinal.equals(other.direccionUsuarioFinal))
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
		if (fechaFinEjecucionPropuesta == null) {
			if (other.fechaFinEjecucionPropuesta != null)
				return false;
		} else if (!fechaFinEjecucionPropuesta.equals(other.fechaFinEjecucionPropuesta))
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
		if (fechaCreacion == null) {
			if (other.fechaCreacion != null)
				return false;
		} else if (!fechaCreacion.equals(other.fechaCreacion))
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
		if (contrato == null) {
			if (other.contrato != null)
				return false;
		} else if (!contrato.equals(other.contrato))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Integer contratoAsString = (contrato != null) ? contrato.getId() : null;
		return "Quote [id=" + id + ", servicio=" + servicio.getId() + ", usuarioFinal=" + usuarioFinal.getId()
				+ ", descripcionSolicitud=" + descripcionSolicitud + ", descripcionRespuesta=" + descripcionRespuesta
				+ ", precioPresupuestado=" + precioPresupuestado + ", precioTotal=" + precioTotal
				+ ", fechaInicioEjecucionPropuesta=" + fechaInicioEjecucionPropuesta + ", fechaFinEjecucionPropuesta="
				+ fechaFinEjecucionPropuesta + ", incluyeInsumos=" + incluyeInsumos + ", incluyeAdicionales="
				+ incluyeAdicionales + ", fechaSolicitud=" + fechaSolicitud + ", fechaRespuesta=" + fechaRespuesta
				+ ", fechaCreacion=" + fechaCreacion + ", estado=" + estado + ", direccionUsuarioFinal="
				+ direccionUsuarioFinal + ", contrato=" + contratoAsString + "]";
	}
}