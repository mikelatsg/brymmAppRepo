package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingrediente implements Parcelable {

	private int idIngrediente;
	private String nombre;
	private String descripcion;
	private float precio;

	public Ingrediente(int idIngrediente, String nombre, String descripcion,
			float precio) {
		super();
		this.idIngrediente = idIngrediente;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
	}
	
	public Ingrediente() {
		super();
		this.idIngrediente = 0;
		this.nombre = null;
		this.descripcion = null;
		this.precio = 0;
	}

	public int getIdIngrediente() {
		return idIngrediente;
	}

	public void setIdIngrediente(int idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idIngrediente);
		dest.writeString(this.nombre);
		dest.writeString(this.descripcion);
		dest.writeFloat(this.precio);
	}

	protected Ingrediente(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idIngrediente = in.readInt();
		this.nombre = in.readString();
		this.descripcion = in.readString();
		this.precio = in.readFloat();
	}

	public static final Parcelable.Creator<Ingrediente> CREATOR = new Parcelable.Creator<Ingrediente>() {

		@Override
		public Ingrediente createFromParcel(Parcel source) {
			return new Ingrediente(source);
		}

		@Override
		public Ingrediente[] newArray(int size) {
			return new Ingrediente[size];
		}

	};

}
