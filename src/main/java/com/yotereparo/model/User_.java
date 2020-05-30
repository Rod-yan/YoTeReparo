package com.yotereparo.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@StaticMetamodel(User.class)
public class User_ {
	public static volatile SingularAttribute<User, String> id;
	public static volatile SingularAttribute<User, String> nombre;
	public static volatile SingularAttribute<User, String> apellido;
	public static volatile SingularAttribute<User, String> email;
	public static volatile SingularAttribute<User, LocalDate> fechaNacimiento;
	public static volatile SingularAttribute<User, String> telefonoPrincipal;
	public static volatile SingularAttribute<User, String> telefonoAlternativo;
	public static volatile SingularAttribute<User, City> ciudad;
	public static volatile SingularAttribute<User, byte[]> foto;
	public static volatile SingularAttribute<User, byte[]> thumbnail;
	public static volatile SingularAttribute<User, String> contrasena;
	public static volatile SingularAttribute<User, String> salt;
	public static volatile SingularAttribute<User, String> descripcion;
	public static volatile SingularAttribute<User, String> estado;
	public static volatile SingularAttribute<User, Integer> intentosIngreso;
	public static volatile SingularAttribute<User, DateTime> fechaUltimoCambioContrasena;
	public static volatile SingularAttribute<User, DateTime> fechaUltimoIngreso;
	public static volatile SingularAttribute<User, DateTime> fechaExpiracionContrasena;
	public static volatile SingularAttribute<User, DateTime> fechaCreacion;
	public static volatile SingularAttribute<User, String> membresia;
	public static volatile SetAttribute<User, Address> direcciones;
	public static volatile SetAttribute<User, Role> roles;
	public static volatile SetAttribute<User, District> barrios;
	public static volatile SetAttribute<User, Service> servicios;
	public static volatile SetAttribute<User, Quote> presupuestos;
	public static volatile SetAttribute<User, Message> mensajes;
}
