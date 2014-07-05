package com.brymm.brymmapp.usuario.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class IngredienteLocal implements Parcelable {

	private int idIngrediente;
	private int idLocal;
	private String nombre;
	private String descripcion;
	private float precio;

	public IngredienteLocal(int idIngrediente, int idLocal, String nombre,
			String descripcion, float precio) {
		super();
		this.idIngrediente = idIngrediente;
		this.idLocal = idLocal;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
	}

	public int getIdIngrediente() {
		return idIngrediente;
	}

	public void setIdIngrediente(int idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

	public int getIdLocal() {
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
		dest.writeInt(this.idLocal);
		dest.writeString(this.nombre);
		dest.writeString(this.descripcion);
		dest.writeFloat(this.precio);
	}

	protected IngredienteLocal(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idIngrediente = in.readInt();
		this.idLocal = in.readInt();
		this.nombre = in.readString();
		this.descripcion = in.readString();
		this.precio = in.readFloat();
	}

	public static final Parcelable.Creator<IngredienteLocal> CREATOR = new Parcelable.Creator<IngredienteLocal>() {

		@Override
		public IngredienteLocal createFromParcel(Parcel source) {
			return new IngredienteLocal(source);
		}

		@Override
		public IngredienteLocal[] newArray(int size) {
			return new IngredienteLocal[size];
		}

	};

}
