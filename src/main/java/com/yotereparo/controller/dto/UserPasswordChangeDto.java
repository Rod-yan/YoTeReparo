package com.yotereparo.controller.dto;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class UserPasswordChangeDto {
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@NotEmpty(message = "{user.contrasenaActual.not.empty}")
	private String contrasenaActual;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@NotEmpty(message = "{user.contrasenaNueva.not.empty}")
	private String contrasenaNueva;

	@JsonIgnore
	public String getContrasenaActual() {
		return contrasenaActual;
	}

	public void setContrasenaActual(String contrasenaActual) {
		this.contrasenaActual = contrasenaActual;
	}

	@JsonIgnore
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
		return true;
	}
}
