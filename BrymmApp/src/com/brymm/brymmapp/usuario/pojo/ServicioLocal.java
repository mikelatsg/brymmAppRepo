package com.brymm.brymmapp.usuario.pojo;

public class ServicioLocal {

	private int idServicioLocal;
	private int idTipoServicio;
	private int idLocal;
	private float importeMinimo;
	private float precio;
	private String nombre;

	public ServicioLocal(int idServicioLocal, int idTipoServicio, int idLocal,
			 float importeMinimo, float precio) {
		super();
		this.idServicioLocal = idServicioLocal;
		this.idTipoServicio = idTipoServicio;
		this.idLocal = idLocal;
		this.importeMinimo = importeMinimo;
		this.precio = precio;
	}

	public ServicioLocal(int idServicioLocal, int idTipoServicio, int idLocal,
			 float importeMinimo, float precio, String nombre) {
		super();
		this.idServicioLocal = idServicioLocal;
		this.idTipoServicio = idTipoServicio;
		this.idLocal = idLocal;
		this.importeMinimo = importeMinimo;
		this.precio = precio;
		this.nombre = nombre;
	}

	public int getIdServicioLocal() {
		return idServicioLocal;
	}

	public void setIdServicioLocal(int idServicioLocal) {
		this.idServicioLocal = idServicioLocal;
	}

	public int getIdTipoServicio() {
		return idTipoServicio;
	}

	public void setIdTipoServicio(int idTipoServicio) {
		this.idTipoServicio = idTipoServicio;
	}

	public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public float getImporteMinimo() {
		return importeMinimo;
	}

	public void setImporteMinimo(float importeMinimo) {
		this.importeMinimo = importeMinimo;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Override
	public String toString() {
		return this.nombre;
	}

}
