package com.brymm.brymmapp.usuario.pojo;

public class TipoComida {

	private int idTipoComida;
	private String nombre;

	public TipoComida(int idTipoComida, String nombre) {
		this.idTipoComida = idTipoComida;
		this.nombre = nombre;
	}

	public int getIdTipoComida() {
		return idTipoComida;
	}

	public void setIdTipoComida(int idTipoComida) {
		this.idTipoComida = idTipoComida;
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
