package com.yotereparo.controller.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.yotereparo.model.Quote;

public class QuoteDto {
	
	@JsonProperty(access = Access.READ_ONLY)
	private Integer id;
	
	@NotNull(message = "{quote.servicio.not.null}")
	private Integer servicio;
	
	@NotEmpty(message = "{quote.usuarioFinal.not.null}")
	private String usuarioFinal;
	
	@Size(max = 255, message = "{quote.descripcionSolicitud.too.long}")
	private String descripcionSolicitud;
	
	@Size(max = 255, message = "{quote.descripcionRespuesta.too.long}")
	private String descripcionRespuesta;
	
	@Digits(integer = 11, fraction = 2, message = "{quote.precioPresupuestado.out.of.boundaries}")
	@Min(value = 0, message="{quote.precioPresupuestado.less.than.min}")
	private Float precioPresupuestado;

	@JsonProperty(access = Access.READ_ONLY)
	private Float precioTotal;
	
	@Future(message = "{quote.fechaInicioEjecucionPropuesta.future}")
	private DateTime fechaInicioEjecucionPropuesta;

	@NotNull(message = "{quote.incluyeInsumos.not.null}")
	private boolean incluyeInsumos;
	
	@NotNull(message = "{quote.incluyeAdicionales.not.null}")
	private boolean incluyeAdicionales;
	
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaSolicitud;
	
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaRespuesta;

	@NotEmpty(message = "{quote.estado.not.empty}")
	@Pattern(regexp = Quote.STATUS_LIST_REGEXP, message = "{quote.estado.pattern}")
	private String estado;
	
	public QuoteDto() { }

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

	public String getUsuarioFinal() {
		return usuarioFinal;
	}

	public void setUsuarioFinal(String usuarioFinal) {
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
	
	@JsonIgnore
	public Float getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(Float precioMinimo, Float precioInsumos, Float precioAdicionales) {
		if (precioPresupuestado == null)
			precioPresupuestado = precioMinimo;
		this.precioTotal = precioPresupuestado;
		if (incluyeInsumos)
			precioTotal = precioTotal + precioInsumos;
		if (incluyeAdicionales)
			precioTotal = precioTotal + precioAdicionales;
	}

	public DateTime getFechaInicioEjecucionPropuesta() {
		return fechaInicioEjecucionPropuesta;
	}

	public void setFechaInicioEjecucionPropuesta(DateTime fechaInicioEjecucionPropuesta) {
		this.fechaInicioEjecucionPropuesta = fechaInicioEjecucionPropuesta;
	}
	
	public void setFechaInicioEjecucionPropuesta(String fechaInicioEjecucionPropuesta) {
		this.fechaInicioEjecucionPropuesta = new DateTime(fechaInicioEjecucionPropuesta);
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

	@JsonIgnore
	public DateTime getFechaSolicitud() {
		return fechaSolicitud;
	}

	public void setFechaSolicitud(DateTime fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	@JsonIgnore
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descripcionRespuesta == null) ? 0 : descripcionRespuesta.hashCode());
		result = prime * result + ((descripcionSolicitud == null) ? 0 : descripcionSolicitud.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result
				+ ((fechaInicioEjecucionPropuesta == null) ? 0 : fechaInicioEjecucionPropuesta.hashCode());
		result = prime * result + ((fechaRespuesta == null) ? 0 : fechaRespuesta.hashCode());
		result = prime * result + ((fechaSolicitud == null) ? 0 : fechaSolicitud.hashCode());
		result = prime * result + (incluyeAdicionales ? 1231 : 1237);
		result = prime * result + (incluyeInsumos ? 1231 : 1237);
		result = prime * result + ((precioPresupuestado == null) ? 0 : precioPresupuestado.hashCode());
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
		QuoteDto other = (QuoteDto) obj;
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
		return "QuoteDto [id=" + id + ", servicio=" + servicio + ", usuarioFinal=" + usuarioFinal
				+ ", descripcionSolicitud=" + descripcionSolicitud + ", descripcionRespuesta=" + descripcionRespuesta
				+ ", precioPresupuestado=" + precioPresupuestado + ", precioTotal=" + precioTotal
				+ ", fechaInicioEjecucionPropuesta=" + fechaInicioEjecucionPropuesta + ", incluyeInsumos="
				+ incluyeInsumos + ", incluyeAdicionales=" + incluyeAdicionales + ", fechaSolicitud=" + fechaSolicitud
				+ ", fechaRespuesta=" + fechaRespuesta + ", estado=" + estado + "]";
	}
}
