package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Direccion implements Parcelable {

	private int idDireccion;
	private String nombre;
	private String direccion;
	private String localidad;
	private String provincia;

	public Direccion(int idDireccion, String nombre, String direccion,
			String localidad, String provincia) {
		super();
		this.idDireccion = idDireccion;
		this.nombre = nombre;
		this.direccion = direccion;
		this.localidad = localidad;
		this.provincia = provincia;
	}

	public int getIdDireccion() {
		return idDireccion;
	}

	public void setIdDireccion(int idDireccion) {
		this.idDireccion = idDireccion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idDireccion);
		dest.writeString(this.nombre);
		dest.writeString(this.direccion);
		dest.writeString(this.localidad);
		dest.writeString(this.provincia);
	}

	protected Direccion(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idDireccion = in.readInt();
		this.nombre = in.readString();
		this.direccion = in.readString();
		this.localidad = in.readString();
		this.provincia = in.readString();
	}

	public static final Parcelable.Creator<Direccion> CREATOR = new Parcelable.Creator<Direccion>() {

		@Override
		public Direccion createFromParcel(Parcel source) {
			return new Direccion(source);
		}

		@Override
		public Direccion[] newArray(int size) {
			return new Direccion[size];
		}

	};

}
