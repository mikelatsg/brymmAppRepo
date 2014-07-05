package com.brymm.brymmapp.usuario.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TipoArticulo implements Parcelable {

	private int idTipoArticuloLocal;
	private int idTipoArticulo;
	private String nombre;
	private String descripcion;
	private int personalizar;
	private float precio;

	public TipoArticulo(int idTipoArticuloLocal, int idTipoArticulo,
			String nombre, String descripcion, int personalizar, float precio) {
		super();
		this.idTipoArticuloLocal = idTipoArticuloLocal;
		this.idTipoArticulo = idTipoArticulo;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.personalizar = personalizar;
		this.precio = precio;
	}

	public int getIdTipoArticuloLocal() {
		return idTipoArticuloLocal;
	}

	public void setIdTipoArticuloLocal(int idTipoArticuloLocal) {
		this.idTipoArticuloLocal = idTipoArticuloLocal;
	}

	public int getIdTipoArticulo() {
		return idTipoArticulo;
	}

	public void setIdTipoArticulo(int idTipoArticulo) {
		this.idTipoArticulo = idTipoArticulo;
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

	public int getPersonalizar() {
		return personalizar;
	}

	public void setPersonalizar(int personalizar) {
		this.personalizar = personalizar;
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
		dest.writeInt(this.idTipoArticuloLocal);
		dest.writeInt(this.idTipoArticulo);
		dest.writeString(this.nombre);
		dest.writeString(this.descripcion);
		dest.writeInt(this.personalizar);
		dest.writeFloat(this.precio);

	}

	protected TipoArticulo(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idTipoArticuloLocal = in.readInt();
		this.idTipoArticulo = in.readInt();
		this.nombre = in.readString();
		this.descripcion = in.readString();
		this.personalizar = in.readInt();
		this.precio = in.readFloat();
	}

	public static final Parcelable.Creator<TipoArticulo> CREATOR = new Parcelable.Creator<TipoArticulo>() {

		@Override
		public TipoArticulo createFromParcel(Parcel source) {
			return new TipoArticulo(source);
		}

		@Override
		public TipoArticulo[] newArray(int size) {
			return new TipoArticulo[size];
		}

	};

}
