package com.brymm.brymmapp.usuario.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Direccion implements Parcelable {

	private int idDireccion;
	private String nombre;
	private String direccion;
	private String poblacion;
	private String provincia;

	public Direccion(int idDireccion, String nombre, String direccion,
			String poblacion, String provincia) {
		super();
		this.idDireccion = idDireccion;
		this.nombre = nombre;
		this.direccion = direccion;
		this.poblacion = poblacion;
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

	public String getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idDireccion);
		dest.writeString(nombre);
		dest.writeString(direccion);
		dest.writeString(poblacion);
		dest.writeString(provincia);
	}

	private Direccion(Parcel in) {
		super();
		this.idDireccion = in.readInt();
		this.nombre = in.readString();
		this.direccion = in.readString();
		this.poblacion = in.readString();
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

	@Override
	public String toString() {
		return this.nombre;
	};

}
