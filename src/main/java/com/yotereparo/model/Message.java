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
@Table(name="mensaje")
public class Message {
	// Constantes de estado
	public static final String AWAITING_RESPONSE = "ESPERANDO_RESPUESTA";
	public static final String CLOSED = "CERRADO";
	public static final String EXPIRED = "EXPIRADO";
	public static final String ARCHIVED = "ARCHIVADO";
	
	public static final String STATUS_LIST_REGEXP =
			AWAITING_RESPONSE + "|" +
			CLOSED + "|" +
			EXPIRED + "|" +
			ARCHIVED;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_mensaje", nullable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_servicio", nullable=false)
	private Service servicio;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_usuario_final", nullable=false)
	private User usuarioFinal;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_consulta", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaConsulta;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_respuesta", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaRespuesta;
	
	private String consulta;
	
	private String respuesta;
	
	private String estado;
	
	public Message() { }

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

	public DateTime getFechaConsulta() {
		return fechaConsulta;
	}

	public void setFechaConsulta(DateTime fechaConsulta) {
		this.fechaConsulta = fechaConsulta;
	}

	public DateTime getFechaRespuesta() {
		return fechaRespuesta;
	}

	public void setFechaRespuesta(DateTime fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
	}

	public String getConsulta() {
		return consulta;
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
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
		Message other = (Message) obj;
		if (consulta == null) {
			if (other.consulta != null)
				return false;
		} else if (!consulta.equals(other.consulta))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (fechaConsulta == null) {
			if (other.fechaConsulta != null)
				return false;
		} else if (!fechaConsulta.equals(other.fechaConsulta))
			return false;
		if (fechaRespuesta == null) {
			if (other.fechaRespuesta != null)
				return false;
		} else if (!fechaRespuesta.equals(other.fechaRespuesta))
			return false;
		if (respuesta == null) {
			if (other.respuesta != null)
				return false;
		} else if (!respuesta.equals(other.respuesta))
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
		return "Message [id=" + id + ", servicio=" + servicio.getId() + ", usuarioFinal=" + usuarioFinal.getId() + ", fechaConsulta="
				+ fechaConsulta + ", fechaRespuesta=" + fechaRespuesta + ", consulta=" + consulta + ", respuesta="
				+ respuesta + ", estado=" + estado + "]";
	}
}
