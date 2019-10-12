package com.yotereparo.models;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class Usuario
{
	private String id;
	private String nombre;
	private String apellido;
	private String email;
	private LocalDate fechaNacimiento;
	private String telefonoPrincipal;
	private String telefonoAlternativo;
	private byte[] thumbnail;
	private String contrasena;
	private String descripcion;
	private String estado;
	private Integer intentosIngreso;
	private DateTime fechaUltimoCambioContrasena;
	private DateTime fechaUltimoIngreso;
	private DateTime fechaExpiracionContrasena;
	private DateTime fechaCreacion;
	private String membresia;

	public Usuario() {	}
	
	// Start of Getters & Setters
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
	// End of Getters & Setters
}
