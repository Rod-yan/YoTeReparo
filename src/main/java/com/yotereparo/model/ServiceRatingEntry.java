package com.yotereparo.model;

public class ServiceRatingEntry {
	
	private Integer valoracion;
	
	private String descripcionValoracion;
	
	public ServiceRatingEntry(Integer valoracion, String descripcionValoracion) {
		this.valoracion = valoracion;
		this.descripcionValoracion = descripcionValoracion;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descripcionValoracion == null) ? 0 : descripcionValoracion.hashCode());
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
		ServiceRatingEntry other = (ServiceRatingEntry) obj;
		if (descripcionValoracion == null) {
			if (other.descripcionValoracion != null)
				return false;
		} else if (!descripcionValoracion.equals(other.descripcionValoracion))
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
		return "ServiceRatingEntryDto [valoracion=" + valoracion + ", descripcionValoracion=" + descripcionValoracion
				+ "]";
	}
}
