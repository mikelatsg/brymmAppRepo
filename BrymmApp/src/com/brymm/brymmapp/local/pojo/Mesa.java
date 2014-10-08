package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Mesa implements Parcelable{
	private int idMesa;
	private int capacidad;
	private String nombre;

	public Mesa(int idMesa, int capacidad, String nombre) {
		super();
		this.idMesa = idMesa;
		this.capacidad = capacidad;
		this.nombre = nombre;
	}

	public int getIdMesa() {
		return idMesa;
	}

	public void setIdMesa(int idMesa) {
		this.idMesa = idMesa;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String toString(){
		return this.nombre;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idMesa);
		dest.writeInt(this.capacidad);
		dest.writeString(this.nombre);
	}

	protected Mesa(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idMesa = in.readInt();
		this.capacidad = in.readInt();
		this.nombre = in.readString();
	}

	public static final Parcelable.Creator<Mesa> CREATOR = new Parcelable.Creator<Mesa>() {

		@Override
		public Mesa createFromParcel(Parcel source) {
			return new Mesa(source);
		}

		@Override
		public Mesa[] newArray(int size) {
			return new Mesa[size];
		}

	};

}
