package com.yotereparo.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.yotereparo.util.SecurityUtils;

@Entity
@Table(name="usuario") 
public class User
{
	@Id
	@NotEmpty(message = "{user.id.not.empty}")
	@Size(min=3, max=16, message = "{user.id.size}")
	@Column(name = "id_usuario", nullable = false)
	private String id;
	
	@NotEmpty(message = "{user.nombre.not.empty}")
	@Column(name = "nombre", nullable = false)
	private String nombre;
	
	@NotEmpty(message = "{user.apellido.not.empty}")
	@Column(name = "apellido", nullable = false)
	private String apellido;
	
	@NotEmpty(message = "{user.email.not.empty}")
	@Email(message = "{user.email.not.valid}")
	@Column(name = "email", nullable = false)
	private String email;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    @Column(name = "fecha_nacimiento", nullable = true)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	private LocalDate fechaNacimiento;
	
	@Column(name = "telefono_principal", nullable = true)
	private String telefonoPrincipal;
	
	@Column(name = "telefono_alternativo", nullable = true)
	private String telefonoAlternativo;
	
	@Column(name = "foto", nullable = true)
	private byte[] foto;
	
	@Column(name = "thumbnail", nullable = true)
	private byte[] thumbnail;
	
	@NotEmpty(message = "{user.contrasena.not.empty}")
	@Column(name = "contrasena", nullable = false)
	private String contrasena;
	
	@Column(name = "salt", nullable = false)
	private String salt;
	
	@Column(name = "descripcion", nullable = true)
	private String descripcion;
	
	@Size(max=20)
	@Column(name = "estado", nullable = false)
	private String estado;
	
	@DecimalMin("0")
	@Column(name = "intentos_ingreso", nullable = false)
	private int intentosIngreso;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_ultimo_cambio_contrasena", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaUltimoCambioContrasena;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_ultimo_ingreso", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaUltimoIngreso;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_expiracion_contrasena", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaExpiracionContrasena;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Column(name = "fecha_creacion", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaCreacion;
	
	@Size(max=10)
	@Column(name = "membresia", nullable = false)
	private String membresia;

	public User() {	}
	
	public void hashContrasena() {
		this.contrasena = SecurityUtils.encryptPassword(this.contrasena.concat(this.salt));
	}
	
	/* Getters & Setters */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	
	public String getTelefonoPrincipal() {
		return telefonoPrincipal;
	}
	public void setTelefonoPrincipal(String telefonoPrincipal) {
		this.telefonoPrincipal = telefonoPrincipal;
	}
	
	public String getTelefonoAlternativo() {
		return telefonoAlternativo;
	}
	public void setTelefonoAlternativo(String telefonoAlternativo) {
		this.telefonoAlternativo = telefonoAlternativo;
	}
	
	public byte[] getFoto() {
		return foto;
	}
	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	public byte[] getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
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
	
	public int getIntentosIngreso()	{
		return intentosIngreso;
	}
	public void setIntentosIngreso(int intentosIngreso)	{
		this.intentosIngreso = intentosIngreso;
	}
	
	public DateTime getFechaUltimoCambioContrasena() {
		return fechaUltimoCambioContrasena;
	}
	public void setFechaUltimoCambioContrasena(DateTime fechaUltimoCambioContrasena) {
		this.fechaUltimoCambioContrasena = fechaUltimoCambioContrasena;
	}
	
	public DateTime getFechaUltimoIngreso() {
		return fechaUltimoIngreso;
	}
	public void setFechaUltimoIngreso(DateTime fechaUltimoIngreso) {
		this.fechaUltimoIngreso = fechaUltimoIngreso;
	}
	
	public DateTime getFechaExpiracionContrasena() {
		return fechaExpiracionContrasena;
	}
	public void setFechaExpiracionContrasena(DateTime fechaExpiracionContrasena) {
		this.fechaExpiracionContrasena = fechaExpiracionContrasena;
	}
	
	public DateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(DateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	public String getMembresia() {
		return membresia;
	}
	public void setMembresia(String membresia) {
		this.membresia = membresia;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apellido == null) ? 0 : apellido.hashCode());
		result = prime * result + ((contrasena == null) ? 0 : contrasena.hashCode());
		result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + ((fechaCreacion == null) ? 0 : fechaCreacion.hashCode());
		result = prime * result + ((fechaExpiracionContrasena == null) ? 0 : fechaExpiracionContrasena.hashCode());
		result = prime * result + ((fechaNacimiento == null) ? 0 : fechaNacimiento.hashCode());
		result = prime * result + ((fechaUltimoCambioContrasena == null) ? 0 : fechaUltimoCambioContrasena.hashCode());
		result = prime * result + ((fechaUltimoIngreso == null) ? 0 : fechaUltimoIngreso.hashCode());
		result = prime * result + Arrays.hashCode(foto);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + intentosIngreso;
		result = prime * result + ((membresia == null) ? 0 : membresia.hashCode());
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		result = prime * result + ((telefonoAlternativo == null) ? 0 : telefonoAlternativo.hashCode());
		result = prime * result + ((telefonoPrincipal == null) ? 0 : telefonoPrincipal.hashCode());
		result = prime * result + Arrays.hashCode(thumbnail);
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
		User other = (User) obj;
		if (apellido == null) {
			if (other.apellido != null)
				return false;
		} else if (!apellido.equals(other.apellido))
			return false;
		if (contrasena == null) {
			if (other.contrasena != null)
				return false;
		} else if (!contrasena.equals(other.contrasena))
			return false;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
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
		if (fechaExpiracionContrasena == null) {
			if (other.fechaExpiracionContrasena != null)
				return false;
		} else if (!fechaExpiracionContrasena.equals(other.fechaExpiracionContrasena))
			return false;
		if (fechaNacimiento == null) {
			if (other.fechaNacimiento != null)
				return false;
		} else if (!fechaNacimiento.equals(other.fechaNacimiento))
			return false;
		if (fechaUltimoCambioContrasena == null) {
			if (other.fechaUltimoCambioContrasena != null)
				return false;
		} else if (!fechaUltimoCambioContrasena.equals(other.fechaUltimoCambioContrasena))
			return false;
		if (fechaUltimoIngreso == null) {
			if (other.fechaUltimoIngreso != null)
				return false;
		} else if (!fechaUltimoIngreso.equals(other.fechaUltimoIngreso))
			return false;
		if (!Arrays.equals(foto, other.foto))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (intentosIngreso != other.intentosIngreso)
			return false;
		if (membresia == null) {
			if (other.membresia != null)
				return false;
		} else if (!membresia.equals(other.membresia))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		if (telefonoAlternativo == null) {
			if (other.telefonoAlternativo != null)
				return false;
		} else if (!telefonoAlternativo.equals(other.telefonoAlternativo))
			return false;
		if (telefonoPrincipal == null) {
			if (other.telefonoPrincipal != null)
				return false;
		} else if (!telefonoPrincipal.equals(other.telefonoPrincipal))
			return false;
		if (!Arrays.equals(thumbnail, other.thumbnail))
			return false;
		return true;
	}
}
