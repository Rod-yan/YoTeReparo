package com.yotereparo.model;

import java.util.Arrays;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

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
	@Column(name = "membresia", nullable = true)
	private String membresia;
	
	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinTable(name="usuario_rol",
        joinColumns = {@JoinColumn(name="id_usuario", referencedColumnName="id_usuario")},
        inverseJoinColumns = {@JoinColumn(name="id_rol", referencedColumnName="id_rol")}    
    )
	@Where(clause = "estado <> 'INACTIVO'")
	private Set<Role> roles;

	public User() {	}
	
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

	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addRole(Role role) {
		roles.add(role);
	}
	public void removeRole(Role role) {
		roles.remove(role);
	}
}
