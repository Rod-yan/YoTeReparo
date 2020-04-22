package com.yotereparo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="rol") 
public class Role {
	// Constantes de estado
	public static final String ACTIVE = "ACTIVO";
	public static final String INACTIVE = "INACTIVO";
	public static final String PROTECTED = "PROTEGIDO";
	
	public static final String STATUS_LIST_REGEXP =
			ACTIVE + "|" +
			INACTIVE + "|" +
			PROTECTED;
	
	@Id
	@Column(name = "id_rol", nullable = false)
	private String id;
	
	private String descripcion;
	
	private String estado;
	
	public Role() { }

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
		result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Role other = (Role) obj;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
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
		return "Role [id=" + id + ", descripcion=" + descripcion + ", estado=" + estado + "]";
	}
}
