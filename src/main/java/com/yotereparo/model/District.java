package com.yotereparo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="barrio") 
public class District {

	@Id
	@NotNull(message = "{district.id.not.null}")
	@Column(name = "id_barrio", nullable = false)
	private int id;
	
	@NotEmpty(message = "{district.descripcion.not.empty}")
	private String descripcion;
	
	@Min(value=1000, message = "{district.codigopostal.min.value}")
	@Max(value=9999, message = "{district.codigopostal.max.value}")
	@Column(name = "codigo_postal", nullable = false)
	private int codigoPostal;
	
	public District() { }

	/* Getters & Setters */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getCodigoPostal() {
		return codigoPostal;
	}
	public void setCodigoPostal(int codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codigoPostal;
		result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
		result = prime * result + id;
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
		District other = (District) obj;
		if (codigoPostal != other.codigoPostal)
			return false;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		/*if (id != other.id)
			return false;*/
		return true;
	}

	@Override
	public String toString() {
		return "District [id=" + id + ", descripcion=" + descripcion + ", codigoPostal=" + codigoPostal + "]";
	}
}
