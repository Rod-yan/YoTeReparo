package com.yotereparo.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="provincia")

public class Province {
	
	@Id
	@NotNull(message = "{province.id.not.null}")
	@Column(name = "id_provincia", nullable = false)
	private String id;
	
	private String descripcion;
		
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
	@JoinColumn(name = "id_provincia", nullable = false, updatable = false, insertable = false)
	private Set<City> ciudades;
	
	public Province() { }

	/* Getters & Setters */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Set<City> getCiudades() {
		return ciudades;
	}
	public void setCiudades(Set<City> ciudades) {
		this.ciudades = ciudades;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ciudades == null) ? 0 : ciudades.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
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
		Province other = (Province) obj;
		if (ciudades == null) {
			if (other.ciudades != null)
				return false;
		} else if (!ciudades.equals(other.ciudades))
			return false;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Province [id=" + id + ", descripcion=" + descripcion + ", ciudades=" + ciudades + "]";
	}
}
