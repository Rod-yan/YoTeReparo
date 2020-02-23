package com.yotereparo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="mensaje")
public class Message {
	

	@Id
	@NotNull(message = "{Message.id.not.null}")
	@Column(name = "id_mensaje", nullable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="id_servicio", nullable=false, updatable = false, insertable = true)
	private Service servicio;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="id_usuario_final", nullable=false, updatable = false, insertable = true)
	private User usuarioFinal;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_consulta", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaConsulta;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_respuesta", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaRespuesta;
	
	@Column(name = "consulta", nullable = false)
	private String consulta;
	
	@Column(name = "respuesta", nullable = false)
	private String respuesta;
	
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
	
	public String getRespuestan() {
		return respuesta;
	}
	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
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
		if (consulta == null) {
			if (other.consulta != null)
				return false;
		} else if (!consulta.equals(other.consulta))
			return false;
		if (respuesta == null) {
			if (other.respuesta != null)
				return false;
		} else if (!respuesta.equals(other.respuesta))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", servicio=" + servicio.getId()
				+ ", usuarioFinal=" + usuarioFinal.getId() + ", fechaConsulta=" + fechaConsulta + ", fechaRespuesta="
				+ fechaRespuesta + ", consulta=" + consulta 
				+ ", respuesta=" + respuesta + "]";
	}

	
}
