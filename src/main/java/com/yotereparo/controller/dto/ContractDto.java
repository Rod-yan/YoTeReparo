package com.yotereparo.controller.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class ContractDto {
	
	@JsonProperty(access = Access.READ_ONLY)
	private Integer id;
	
	@JsonProperty(access = Access.READ_ONLY)
	private Integer presupuesto;
	
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaInicioEjecucion;
	
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaFinEjecucion;
	
	@JsonProperty(access = Access.READ_ONLY)
	private Float precioFinal;
	
	@Min(value = 1, message = "{contract.valoracion.less.than.min}")
	@Max(value = 10, message = "{contract.valoracion.greater.than.max}")
	@NotNull(message = "{contract.valoracion.not.null}")
	private Integer valoracion;
	
	@Size(max = 255, message = "{contract.descripcionValoracion.too.long}")
	private String descripcionValoracion;
	
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaValoracion;

	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaCreacion;

	@JsonProperty(access = Access.READ_ONLY)
	private String estado;
	
	public ContractDto() { }

	@JsonIgnore
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@JsonIgnore
	public Integer getPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(Integer presupuesto) {
		this.presupuesto = presupuesto;
	}
	@JsonIgnore
	public DateTime getFechaInicioEjecucion() {
		return fechaInicioEjecucion;
	}

	public void setFechaInicioEjecucion(DateTime fechaInicioEjecucion) {
		this.fechaInicioEjecucion = fechaInicioEjecucion;
	}
	@JsonIgnore
	public DateTime getFechaFinEjecucion() {
		return fechaFinEjecucion;
	}

	public void setFechaFinEjecucion(DateTime fechaFinEjecucion) {
		this.fechaFinEjecucion = fechaFinEjecucion;
	}
	@JsonIgnore
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
	@JsonIgnore
	public DateTime getFechaValoracion() {
		return fechaValoracion;
	}

	public void setFechaValoracion(DateTime fechaValoracion) {
		this.fechaValoracion = fechaValoracion;
	}
	@JsonIgnore
	public DateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
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
		result = prime * result + ((descripcionValoracion == null) ? 0 : descripcionValoracion.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + ((fechaCreacion == null) ? 0 : fechaCreacion.hashCode());
		result = prime * result + ((fechaFinEjecucion == null) ? 0 : fechaFinEjecucion.hashCode());
		result = prime * result + ((fechaInicioEjecucion == null) ? 0 : fechaInicioEjecucion.hashCode());
		result = prime * result + ((fechaValoracion == null) ? 0 : fechaValoracion.hashCode());
		result = prime * result + ((precioFinal == null) ? 0 : precioFinal.hashCode());
		result = prime * result + ((presupuesto == null) ? 0 : presupuesto.hashCode());
		result = prime * result + ((valoracion == null) ? 0 : valoracion.hashCode());
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
		ContractDto other = (ContractDto) obj;
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
		if (fechaValoracion == null) {
			if (other.fechaValoracion != null)
				return false;
		} else if (!fechaValoracion.equals(other.fechaValoracion))
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
		return "ContractDto [id=" + id + ", presupuesto=" + presupuesto + ", fechaInicioEjecucion="
				+ fechaInicioEjecucion + ", fechaFinEjecucion=" + fechaFinEjecucion + ", precioFinal=" + precioFinal
				+ ", valoracion=" + valoracion + ", descripcionValoracion=" + descripcionValoracion
				+ ", fechaValoracion=" + fechaValoracion + ", fechaCreacion=" + fechaCreacion + ", estado=" + estado
				+ "]";
	}
}
