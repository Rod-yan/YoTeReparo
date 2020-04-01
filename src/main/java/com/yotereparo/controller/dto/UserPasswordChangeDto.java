package com.yotereparo.controller.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UserPasswordChangeDto {
	
	@NotEmpty(message = "{user.id.not.empty}")
	@Size(min=3, max=15, message = "{user.id.size}")
	private String id;
	
	@NotEmpty(message = "{user.contrasenaActual.not.empty}")
	private String contrasenaActual;
	
	@NotEmpty(message = "{user.contrasenaNueva.not.empty}")
	private String contrasenaNueva;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContrasenaActual() {
		return contrasenaActual;
	}

	public void setContrasenaActual(String contrasenaActual) {
		this.contrasenaActual = contrasenaActual;
	}

	public String getContrasenaNueva() {
		return contrasenaNueva;
	}

	public void setContrasenaNueva(String contrasenaNueva) {
		this.contrasenaNueva = contrasenaNueva;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contrasenaActual == null) ? 0 : contrasenaActual.hashCode());
		result = prime * result + ((contrasenaNueva == null) ? 0 : contrasenaNueva.hashCode());
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
		UserPasswordChangeDto other = (UserPasswordChangeDto) obj;
		if (contrasenaActual == null) {
			if (other.contrasenaActual != null)
				return false;
		} else if (!contrasenaActual.equals(other.contrasenaActual))
			return false;
		if (contrasenaNueva == null) {
			if (other.contrasenaNueva != null)
				return false;
		} else if (!contrasenaNueva.equals(other.contrasenaNueva))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
