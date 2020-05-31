package com.yotereparo.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="usuario") 
public class User {
	// Constantes de estado
	public static final String ACTIVE = "ACTIVO";
	public static final String INACTIVE = "INACTIVO";
	public static final String BLOCKED = "BLOQUEADO";
	public static final String ARCHIVED = "ARCHIVADO";
	
	public static final String STATUS_LIST_REGEXP =
			ACTIVE + "|" +
			INACTIVE + "|" +
			BLOCKED + "|" +
			ARCHIVED;
	
	@Id
	@Column(name = "id_usuario", nullable = false)
	private String id;
	
	private String nombre;
	
	private String apellido;
	
	@NaturalId(mutable=true)
	private String email;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    @Column(name = "fecha_nacimiento", nullable = true)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	private LocalDate fechaNacimiento;
	
	@Column(name = "telefono_principal", nullable = true)
	private String telefonoPrincipal;
	
	@Column(name = "telefono_alternativo", nullable = true)
	private String telefonoAlternativo;
	
	@ManyToOne
    @JoinColumn(name="id_ciudad", nullable=false, updatable = true, insertable = true)
	private City ciudad;
	
	private byte[] foto;
	
	private byte[] thumbnail;
	
	private String contrasena;
	
	private String salt;
	
	private String descripcion;
	
	private String estado;
	
	@Column(name = "intentos_ingreso", nullable = false)
	private Integer intentosIngreso;
	
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
	@Column(name = "fecha_creacion", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime fechaCreacion;
	
	private String membresia;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id_usuario", nullable = false, updatable = false, insertable = true)
	private Set<Address> direcciones = new HashSet<Address>(0);
	
	@ManyToMany(cascade=CascadeType.MERGE,fetch=FetchType.EAGER)
    @JoinTable(name="usuario_rol",
        joinColumns = {@JoinColumn(name="id_usuario")},
        inverseJoinColumns = {@JoinColumn(name="id_rol")}    
    )
	@Where(clause = "estado <> '"+Role.INACTIVE+"'")
	private Set<Role> roles = new HashSet<Role>(0);
	
	@ManyToMany(cascade=CascadeType.MERGE,fetch=FetchType.EAGER)
    @JoinTable(name="usuario_barrio",
        joinColumns = {@JoinColumn(name="id_usuario")},
        inverseJoinColumns = {@JoinColumn(name="id_barrio")}    
    )
	private Set<District> barrios = new HashSet<District>(0);
	
	@OneToMany(mappedBy = "usuarioPrestador", fetch = FetchType.EAGER, cascade=CascadeType.MERGE, orphanRemoval = true)
	@Where(clause = "estado <> '"+Service.ARCHIVED+"' OR estado <> '"+Service.BLOCKED+"'")
	private Set<Service> servicios  = new HashSet<Service>(0);
	
	@OneToMany(mappedBy = "usuarioFinal", fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
	@Where(clause = "estado <> '"+Quote.ARCHIVED+"'")
	private Set<Quote> presupuestos = new HashSet<Quote>(0);
	
	@OneToMany(mappedBy = "usuarioFinal", fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
	@Where(clause = "estado <> '"+Message.ARCHIVED+"'")
	private Set<Message> mensajes = new HashSet<Message>(0);

	public User() {	}
	
	/* Getters & Setters */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = (id != null && !id.isEmpty()) ? id.toLowerCase() : null;
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
	
	public City getCiudad() {
		return ciudad;
	}
	public void setCiudad(City ciudad) {
		this.ciudad = ciudad;
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
	
	public Integer getIntentosIngreso()	{
		return intentosIngreso;
	}
	public void setIntentosIngreso(Integer intentosIngreso)	{
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
		this.membresia = (membresia != null && !membresia.isEmpty()) ? membresia.toUpperCase() : null;
	}

	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Address> getDirecciones() {
		return direcciones;
	}
	public void setDirecciones(Set<Address> direcciones) {
		this.direcciones = direcciones;
	}

	public Set<District> getBarrios() {
		return barrios;
	}
	public void setBarrios(Set<District> barrios) {
		this.barrios = barrios;
	}

	public Set<Service> getServicios() {
		return servicios;
	}

	public void setServicios(Set<Service> servicios) {
		this.servicios = servicios;
	}

	public void addRole(Role role) {
		roles.add(role);
	}
	public void removeRole(Role role) {
		roles.remove(role);
	}
	
	public void addDireccion(Address direccion) {
        direcciones.add(direccion);
    }
    public void removeDireccion(Address direccion) {
    	direcciones.remove(direccion);
    }
    
    public void addBarrio(District barrio) {
    	barrios.add(barrio);
    }
    public void removeBarrio(District barrio) {
    	barrios.remove(barrio);
    }
    
    public void addServicio(Service servicio) {
    	servicios.add(servicio);
    }
    public void removeServicio(Service servicio) {
    	servicios.remove(servicio);
    }
    
    public Set<Quote> getPresupuestos() {
		return presupuestos;
	}
	public void setPresupuestos(Set<Quote> presupuestos) {
		this.presupuestos = presupuestos;
	}
	public void addPresupuesto(Quote presupuesto) {
		presupuestos.add(presupuesto);
    }
    public void removePresupuesto(Quote presupuesto) {
    	presupuestos.remove(presupuesto);
    }
    
    public Set<Message> getMensajes() {
		return mensajes;
	}
	public void setMensajes(Set<Message> mensajes) {
		this.mensajes = mensajes;
	}
	public void addMensaje(Message mensaje) {
		mensajes.add(mensaje);
    }
    public void removeMensaje(Message mensaje) {
    	mensajes.remove(mensaje);
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
		if (barrios == null) {
			if (other.barrios != null)
				return false;
		} else if (!barrios.equals(other.barrios))
			return false;
		if (ciudad == null) {
			if (other.ciudad != null)
				return false;
		} else if (!ciudad.equals(other.ciudad))
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
		if (direcciones == null) {
			if (other.direcciones != null)
				return false;
		} else if (!direcciones.equals(other.direcciones))
			return false;
		if (presupuestos == null) {
			if (other.presupuestos != null)
				return false;
		} else if (!presupuestos.equals(other.presupuestos))
			return false;
		if (mensajes == null) {
			if (other.mensajes != null)
				return false;
		} else if (!mensajes.equals(other.mensajes))
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
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		if (servicios == null) {
			if (other.servicios != null)
				return false;
		} else if (!servicios.equals(other.servicios))
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

	@Override
	public String toString() {
		return "User [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", email=" + email
				+ ", fechaNacimiento=" + fechaNacimiento + ", telefonoPrincipal=" + telefonoPrincipal
				+ ", telefonoAlternativo=" + telefonoAlternativo + ", ciudad=" + ciudad + ", foto="
				+ Arrays.toString(foto) + ", thumbnail=" + Arrays.toString(thumbnail) + ", contrasena=" + contrasena
				+ ", salt=" + salt + ", descripcion=" + descripcion + ", estado=" + estado + ", intentosIngreso="
				+ intentosIngreso + ", fechaUltimoCambioContrasena=" + fechaUltimoCambioContrasena
				+ ", fechaUltimoIngreso=" + fechaUltimoIngreso + ", fechaExpiracionContrasena="
				+ fechaExpiracionContrasena + ", fechaCreacion=" + fechaCreacion + ", membresia=" + membresia
				+ ", direcciones=" + direcciones + ", roles=" + roles + ", barrios=" + barrios + ", servicios="
				+ servicios + ", presupuestos=" + presupuestos + ", mensajes=" + mensajes + "]";
	}
}
