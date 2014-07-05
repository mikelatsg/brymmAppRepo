package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Plato implements Parcelable {
	
	private int idPlato;
	private TipoPlato tipoPlato;
	private String nombre;
	private float precio;

	

	public Plato(int idPlato, TipoPlato tipoPlato, String nombre, float precio) {
		super();
		this.idPlato = idPlato;
		this.tipoPlato = tipoPlato;
		this.nombre = nombre;
		this.precio = precio;
	}
	
	

	public int getIdPlato() {
		return idPlato;
	}



	public void setIdPlato(int idPlato) {
		this.idPlato = idPlato;
	}



	public TipoPlato getTipoPlato() {
		return tipoPlato;
	}



	public void setTipoPlato(TipoPlato tipoPlato) {
		this.tipoPlato = tipoPlato;
	}



	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
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
		dest.writeInt(this.idPlato);
		dest.writeParcelable(this.tipoPlato, flags);
		dest.writeString(this.nombre);
		dest.writeFloat(this.precio);
	}

	protected Plato(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idPlato = in.readInt();
		this.tipoPlato = in.readParcelable(TipoPlato.class
				.getClassLoader());
		this.nombre = in.readString();
		this.precio = in.readFloat();
	}

	public static final Parcelable.Creator<Plato> CREATOR = new Parcelable.Creator<Plato>() {

		@Override
		public Plato createFromParcel(Parcel source) {
			return new Plato(source);
		}

		@Override
		public Plato[] newArray(int size) {
			return new Plato[size];
		}

	};

}
