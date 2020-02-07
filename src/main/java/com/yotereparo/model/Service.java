package com.yotereparo.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import com.yotereparo.util.customvalidator.GreaterThan;

@Entity
@Table(name="servicio")
public class Service {
	
	@Id
	@Column(name = "id_servicio", nullable = false)
	private Integer id;
	
	@ManyToOne(optional = false)
    @JoinColumn(name="id_usuario_prestador", nullable=false, updatable = false, insertable = true)
	@NotEmpty(message = "{service.usuarioPrestador.not.empty}")
	private User usuarioPrestador;
	
	@NotEmpty(message = "{service.descripcion.not.empty}")
	private String descripcion;
	
	private String disponibilidad;
	
	@Column(name = "precio_maximo", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = false)
	@NotEmpty(message = "{service.precioMaximo.not.empty}")
	@Digits(integer = 9, fraction = 2, message = "{service.precioMaximo.out.of.boundaries}")
	@GreaterThan(field = "precioMinimo", message = "{service.precioMaximo.less.than.min}")
	private float precioMaximo;
	
	@Column(name = "precio_minimo", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = false)
	@NotEmpty(message = "{service.precioMinimo.not.empty}")
	@Digits(integer = 9, fraction = 2, message = "{service.precioMinimo.out.of.boundaries}")
	private float precioMinimo;
	
	@Transient
	@Digits(integer = 9, fraction = 2, message = "{service.precioPromedio.out.of.boundaries}")
	private float precioPromedio;
	
	@Column(name = "precio_insumos", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = true)
	@Digits(integer = 9, fraction = 2, message = "{service.precioInsumos.out.of.boundaries}")
	private float precioInsumos;
	
	@Column(name = "precio_adicionales", columnDefinition = "NUMERIC", precision = 11, scale = 2, nullable = true)
	@Digits(integer = 9, fraction = 2, message = "{service.precioAdicionales.out.of.boundaries}")
	private float precioAdicionales;
	
	@Column(name = "horas_estimadas_ejecucion", columnDefinition = "NUMERIC", precision = 7, scale = 2, nullable = false)
	@NotNull(message = "{service.horasEstimadasEjecucion.not.empty}")
	@Digits(integer = 5, fraction = 2, message = "{service.horasEstimadasEjecucion.out.of.boundaries}")
	private float horasEstimadasEjecucion;
	
	@Column(name = "cantidad_trabajadores", nullable = false)
	@NotEmpty(message = "{service.cantidadTrabajadores.not.empty}")
	@Min(value = 1, message = "{service.cantidadTrabajadores.less.than.min}")
	private Integer cantidadTrabajadores;
	
	@Column(name = "factura_emitida", nullable = false)
	@NotEmpty(message = "{service.facturaEmitida.not.empty}")
	private boolean facturaEmitida;
	
	private byte[] imagen;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_creacion", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaCreacion;
	
	@NotEmpty(message = "{service.estado.not.empty}")
	private String estado;
	
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE},fetch=FetchType.EAGER)
    @JoinTable(name="servicio_mediodepago",
        joinColumns = {@JoinColumn(name="id_servicio")},
        inverseJoinColumns = {@JoinColumn(name="id_mediodepago")}    
    )
	@NotEmpty(message = "{service.mediosDePago.not.empty}")
	private Set<PaymentMethod> mediosDePago = new HashSet<PaymentMethod>(0);
	
	//TODO: Tipo servicio
	
	//TODO: Requerimiento
	
	//TODO: Contrato ?
	
	public Service() { }
	
	/* Getters & Setters */
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUsuarioPrestador() {
		return usuarioPrestador;
	}

	public void setUsuarioPrestador(User usuarioPrestador) {
		this.usuarioPrestador = usuarioPrestador;
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

	public float getPrecioMaximo() {
		return precioMaximo;
	}

	public void setPrecioMaximo(float precioMaximo) {
		this.precioMaximo = precioMaximo;
	}

	public float getPrecioMinimo() {
		return precioMinimo;
	}

	public void setPrecioMinimo(float precioMinimo) {
		this.precioMinimo = precioMinimo;
	}

	public float getPrecioPromedio() {
		return precioPromedio;
	}

	public void setPrecioPromedio(float precioPromedio) {
		this.precioPromedio = (precioMaximo+precioMinimo)/2;
	}

	public float getPrecioInsumos() {
		return precioInsumos;
	}

	public void setPrecioInsumos(float precioInsumos) {
		this.precioInsumos = precioInsumos;
	}

	public float getPrecioAdicionales() {
		return precioAdicionales;
	}

	public void setPrecioAdicionales(float precioAdicionales) {
		this.precioAdicionales = precioAdicionales;
	}

	public float getHorasEstimadasEjecucion() {
		return horasEstimadasEjecucion;
	}

	public void setHorasEstimadasEjecucion(float horasEstimadasEjecucion) {
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

	public byte[] getImagen() {
		return imagen;
	}

	public void setImagen(byte[] imagen) {
		this.imagen = imagen;
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

	public Set<PaymentMethod> getMediosDePago() {
		return mediosDePago;
	}

	public void setMediosDePago(Set<PaymentMethod> mediosDePago) {
		this.mediosDePago = mediosDePago;
	}
	
	public void addMedioDePago(PaymentMethod medioDePago) {
		mediosDePago.add(medioDePago);
    }
    public void removeDireccion(PaymentMethod medioDePago) {
    	mediosDePago.remove(medioDePago);
    }

	@Override
	public String toString() {
		return "Service [id=" + id + ", usuarioPrestador=" + usuarioPrestador + ", descripcion=" + descripcion
				+ ", disponibilidad=" + disponibilidad + ", precioMaximo=" + precioMaximo + ", precioMinimo="
				+ precioMinimo + ", precioPromedio=" + precioPromedio + ", precioInsumos=" + precioInsumos
				+ ", precioAdicionales=" + precioAdicionales + ", horasEstimadasEjecucion=" + horasEstimadasEjecucion
				+ ", cantidadTrabajadores=" + cantidadTrabajadores + ", facturaEmitida=" + facturaEmitida + ", imagen="
				+ Arrays.toString(imagen) + ", fechaCreacion=" + fechaCreacion + ", estado=" + estado
				+ ", mediosDePago=" + mediosDePago + "]";
	}
}
