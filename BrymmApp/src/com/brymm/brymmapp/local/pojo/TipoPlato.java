package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TipoPlato implements Parcelable {
	private int idTipoPlato;
	private String descripcion;

	public TipoPlato(int idTipoPlato, String descripcion) {
		super();
		this.idTipoPlato = idTipoPlato;
		this.descripcion = descripcion;
	}

	public int getIdTipoPlato() {
		return idTipoPlato;
	}

	public void setIdTipoPlato(int idTipoPlato) {
		this.idTipoPlato = idTipoPlato;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idTipoPlato);
		dest.writeString(this.descripcion);
	}

	protected TipoPlato(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idTipoPlato = in.readInt();
		this.descripcion = in.readString();
	}

	public static final Parcelable.Creator<TipoPlato> CREATOR = new Parcelable.Creator<TipoPlato>() {

		@Override
		public TipoPlato createFromParcel(Parcel source) {
			return new TipoPlato(source);
		}

		@Override
		public TipoPlato[] newArray(int size) {
			return new TipoPlato[size];
		}

	};

}
