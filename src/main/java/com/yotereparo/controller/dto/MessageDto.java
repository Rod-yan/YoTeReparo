package com.yotereparo.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class MessageDto {
	
	@JsonProperty(access = Access.READ_ONLY)
	private Integer id;
	
	@NotNull(message = "{message.servicio.not.null}")
	private Integer servicio;
	
	@JsonProperty(access = Access.READ_ONLY)
	private String usuarioFinal;
	
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaConsulta;
	
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaRespuesta;

	@Size(max = 255, message = "{message.consulta.too.long}")
	private String consulta;
	
	@Size(max = 255, message = "{message.respuesta.too.long}")
	private String respuesta;
	
	@JsonProperty(access = Access.READ_ONLY)
	private String estado;
	
	public MessageDto() { }
	
	@JsonIgnore
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getServicio() {
		return servicio;
	}

	public void setServicio(Integer servicio) {
		this.servicio = servicio;
	}
	
	@JsonIgnore
	public String getUsuarioFinal() {
		return usuarioFinal;
	}

	public void setUsuarioFinal(String usuarioFinal) {
		this.usuarioFinal = usuarioFinal;
	}
	
	@JsonIgnore
	public DateTime getFechaConsulta() {
		return fechaConsulta;
	}

	public void setFechaConsulta(DateTime fechaConsulta) {
		this.fechaConsulta = fechaConsulta;
	}
	
	@JsonIgnore
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
	
	@JsonIgnore
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((consulta == null) ? 0 : consulta.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + ((fechaConsulta == null) ? 0 : fechaConsulta.hashCode());
		result = prime * result + ((fechaRespuesta == null) ? 0 : fechaRespuesta.hashCode());
		result = prime * result + ((respuesta == null) ? 0 : respuesta.hashCode());
		result = prime * result + ((servicio == null) ? 0 : servicio.hashCode());
		result = prime * result + ((usuarioFinal == null) ? 0 : usuarioFinal.hashCode());
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
		MessageDto other = (MessageDto) obj;
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
		return "MessageDto [id=" + id + ", servicio=" + servicio + ", usuarioFinal=" + usuarioFinal + ", fechaConsulta="
				+ fechaConsulta + ", fechaRespuesta=" + fechaRespuesta + ", consulta=" + consulta + ", respuesta="
				+ respuesta + ", estado=" + estado + "]";
	}
}
