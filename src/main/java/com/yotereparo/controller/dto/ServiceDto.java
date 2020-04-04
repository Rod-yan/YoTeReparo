package com.yotereparo.controller.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.yotereparo.model.PaymentMethod;
import com.yotereparo.model.Requirement;
import com.yotereparo.util.customvalidator.GreaterThan;

@GreaterThan(valueOf = "precioMaximo", greaterThanValueOf = "precioMinimo", message = "{service.precioMaximo.less.than.precioMinimo}")
public class ServiceDto {
	
	@JsonProperty(access = Access.READ_ONLY)
	private Integer id;
	
	@NotEmpty(message = "{service.usuarioPrestador.not.empty}")
	private String usuarioPrestador;
	
	@NotEmpty(message = "{service.titulo.not.empty}")
	@Size.List({ 
		@Size(min = 5, message = "{service.titulo.too.short}"), 
		@Size(max = 127, message = "{service.titulo.too.long}")
	})
	private String titulo;
	
	@NotEmpty(message = "{service.descripcion.not.empty}")
	@Size(max = 255, message = "{service.descripcion.too.long}")
	private String descripcion;
	
	@Size(max = 255, message = "{service.disponibilidad.too.long}")
	private String disponibilidad;
	
	@NotNull(message = "{service.precioMaximo.not.null}")
	@Digits(integer = 9, fraction = 2, message = "{service.precioMaximo.out.of.boundaries}")
	@Min(value = 0, message="{service.precioMaximo.less.than.min}")
	private Float precioMaximo;
	
	@NotNull(message = "{service.precioMinimo.not.null}")
	@Digits(integer = 9, fraction = 2, message = "{service.precioMinimo.out.of.boundaries}")
	@Min(value = 0, message="{service.precioMinimo.less.than.min}")
	private Float precioMinimo;

	@JsonProperty(access = Access.READ_ONLY)
	private Float precioPromedio;

	@Digits(integer = 9, fraction = 2, message = "{service.precioInsumos.out.of.boundaries}")
	@Min(value = 0, message="{service.precioInsumos.less.than.min}")
	private Float precioInsumos;

	@Digits(integer = 9, fraction = 2, message = "{service.precioAdicionales.out.of.boundaries}")
	@Min(value = 0, message="{service.precioAdicionales.less.than.min}")
	private Float precioAdicionales;

	@NotNull(message = "{service.horasEstimadasEjecucion.not.null}")
	@Digits(integer = 5, fraction = 2, message = "{service.horasEstimadasEjecucion.out.of.boundaries}")
	@Min(value = 0, message="{service.horasEstimadasEjecucion.less.than.min}")
	private Float horasEstimadasEjecucion;

	@NotNull(message = "{service.cantidadTrabajadores.not.null}")
	@Min(value = 1, message = "{service.cantidadTrabajadores.less.than.min}")
	private Integer cantidadTrabajadores;

	@NotNull(message = "{service.facturaEmitida.not.null}")
	private boolean facturaEmitida;
	
	@NotEmpty(message = "{service.tipoServicio.not.empty}")
	private String tipoServicio;
	
	@JsonProperty(access = Access.READ_ONLY)
	private DateTime fechaCreacion;

	@JsonProperty(access = Access.READ_ONLY)
	private String estado;

	@NotEmpty(message = "{service.mediosDePago.not.empty}")
	private Set<PaymentMethod> mediosDePago = new HashSet<PaymentMethod>(0);
	
	private Set<Requirement> requerimientos = new HashSet<Requirement>(0);
	
	@JsonProperty(access = Access.READ_ONLY)
	private Set<QuoteDto> presupuestos = new HashSet<QuoteDto>(0);
	
	public ServiceDto() { }

	@JsonIgnore
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsuarioPrestador() {
		return usuarioPrestador;
	}

	public void setUsuarioPrestador(String usuarioPrestador) {
		this.usuarioPrestador = usuarioPrestador;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
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

	@JsonIgnore
	public Float getPrecioPromedio() {
		return precioPromedio;
	}

	public void setPrecioPromedio(Float precioPromedio) {
		this.precioPromedio = (float) Math.ceil((precioMaximo+precioMinimo)/2);
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

	public String getTipoServicio() {
		return tipoServicio;
	}

	public void setTipoServicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
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

	public Set<PaymentMethod> getMediosDePago() {
		return mediosDePago;
	}

	public void setMediosDePago(Set<PaymentMethod> mediosDePago) {
		this.mediosDePago = mediosDePago;
	}
	
	public Set<Requirement> getRequerimientos() {
		return requerimientos;
	}

	public void setRequerimientos(Set<Requirement> requerimientos) {
		this.requerimientos = requerimientos;
	}
	
	@JsonIgnore
	public Set<QuoteDto> getPresupuestos() {
		return presupuestos;
	}

	public void setPresupuestos(Set<QuoteDto> presupuestos) {
		this.presupuestos = presupuestos;
	}
	
	public void addPresupuesto(QuoteDto presupuesto) {
		this.presupuestos.add(presupuesto);
	}
	
	public void removePresupuesto(QuoteDto presupuesto) {
		this.presupuestos.remove(presupuesto);
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
		result = prime * result + ((mediosDePago == null) ? 0 : mediosDePago.hashCode());
		result = prime * result + ((requerimientos == null) ? 0 : requerimientos.hashCode());
		result = prime * result + ((presupuestos == null) ? 0 : presupuestos.hashCode());
		result = prime * result + ((precioAdicionales == null) ? 0 : precioAdicionales.hashCode());
		result = prime * result + ((precioInsumos == null) ? 0 : precioInsumos.hashCode());
		result = prime * result + ((precioMaximo == null) ? 0 : precioMaximo.hashCode());
		result = prime * result + ((precioMinimo == null) ? 0 : precioMinimo.hashCode());
		result = prime * result + ((tipoServicio == null) ? 0 : tipoServicio.hashCode());
		result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
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
		ServiceDto other = (ServiceDto) obj;
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
		if (mediosDePago == null) {
			if (other.mediosDePago != null)
				return false;
		} else if (!mediosDePago.equals(other.mediosDePago))
			return false;
		if (requerimientos == null) {
			if (other.requerimientos != null)
				return false;
		} else if (!requerimientos.equals(other.requerimientos))
			return false;
		if (presupuestos == null) {
			if (other.presupuestos != null)
				return false;
		} else if (!presupuestos.equals(other.presupuestos))
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
		if (tipoServicio == null) {
			if (other.tipoServicio != null)
				return false;
		} else if (!tipoServicio.equals(other.tipoServicio))
			return false;
		if (titulo == null) {
			if (other.titulo != null)
				return false;
		} else if (!titulo.equals(other.titulo))
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
		return "ServiceDto [id=" + id + ", usuarioPrestador=" + usuarioPrestador + ", titulo=" + titulo
				+ ", descripcion=" + descripcion + ", disponibilidad=" + disponibilidad + ", precioMaximo="
				+ precioMaximo + ", precioMinimo=" + precioMinimo + ", precioPromedio=" + precioPromedio
				+ ", precioInsumos=" + precioInsumos + ", precioAdicionales=" + precioAdicionales
				+ ", horasEstimadasEjecucion=" + horasEstimadasEjecucion + ", cantidadTrabajadores="
				+ cantidadTrabajadores + ", facturaEmitida=" + facturaEmitida + ", tipoServicio=" + tipoServicio
				+ ", fechaCreacion=" + fechaCreacion + ", estado=" + estado + ", mediosDePago=" + mediosDePago
				+ ", requerimientos=" + requerimientos + ", presupuestos=" + presupuestos + "]";
	}
}
