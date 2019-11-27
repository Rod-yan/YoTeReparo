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
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name="ciudad") 
public class City {

	@Id
	@NotEmpty(message = "{city.id.not.empty}")
	@Column(name = "id_ciudad", nullable = false)
	private String id;
	
	@NotEmpty(message = "{city.provincia.not.empty}")
	@Column(name = "id_provincia", nullable = false)
	private String provincia;
	
	@NotEmpty(message = "{city.descripcion.not.empty}")
	private String descripcion;	
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
	@JoinColumn(name = "id_ciudad", nullable = false, updatable = false, insertable = false)
	private Set<District> barrios;
	
	public City() { }

	/* Getters & Setters */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Set<District> getBarrios() {
		return barrios;
	}
	public void setBarrios(Set<District> barrios) {
		this.barrios = barrios;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barrios == null) ? 0 : barrios.hashCode());
		result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((provincia == null) ? 0 : provincia.hashCode());
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
		City other = (City) obj;
		if (barrios == null) {
			if (other.barrios != null)
				return false;
		} else if (!barrios.equals(other.barrios))
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
		if (provincia == null) {
			if (other.provincia != null)
				return false;
		} else if (!provincia.equals(other.provincia))
			return false;
		return true;
	}
}
