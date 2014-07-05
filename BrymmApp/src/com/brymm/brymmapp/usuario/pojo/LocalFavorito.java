package com.brymm.brymmapp.usuario.pojo;

public class LocalFavorito extends Local{

	private int idLocalFavorito;

	public LocalFavorito(int idLocalFavorito, int idLocal, String nombre,
			String localidad, String provincia, String direccion,
			String codigoPostal, String telefono, String email,
			String tipoComida) {
		super(idLocal,nombre,localidad,provincia,direccion,codigoPostal,email,telefono,tipoComida);
		this.idLocalFavorito = idLocalFavorito;		
	}

	public int getIdLocalFavorito() {
		return idLocalFavorito;
	}

	public void setIdLocalFavorito(int idLocalFavorito) {
		this.idLocalFavorito = idLocalFavorito;
	}

	/*public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTipoComida() {
		return tipoComida;
	}

	public void setTipoComida(String tipoComida) {
		this.tipoComida = tipoComida;
	}*/

}
