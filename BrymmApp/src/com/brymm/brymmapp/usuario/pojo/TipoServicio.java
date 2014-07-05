package com.brymm.brymmapp.usuario.pojo;

public class TipoServicio {

	private int idServicioLocal;
	private String nombre;

	public TipoServicio(int idServicioLocal, String nombre) {
		super();
		this.idServicioLocal = idServicioLocal;
		this.nombre = nombre;
	}

	public int getIdServicioLocal() {
		return idServicioLocal;
	}

	public void setIdServicioLocal(int idServicioLocal) {
		this.idServicioLocal = idServicioLocal;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
