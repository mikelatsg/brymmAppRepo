package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TipoComanda implements Parcelable {
	private int idTipoComanda;
	private String descripcion;

	
	
	public TipoComanda(int idTipoComanda, String descripcion) {
		super();
		this.idTipoComanda = idTipoComanda;
		this.descripcion = descripcion;
	}

	public int getIdTipoComanda() {
		return idTipoComanda;
	}

	public void setIdTipoComanda(int idTipoComanda) {
		this.idTipoComanda = idTipoComanda;
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
		dest.writeInt(this.idTipoComanda);
		dest.writeString(this.descripcion);
	}

	protected TipoComanda(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idTipoComanda= in.readInt();
		this.descripcion = in.readString();
	}

	public static final Parcelable.Creator<TipoComanda> CREATOR = new Parcelable.Creator<TipoComanda>() {

		@Override
		public TipoComanda createFromParcel(Parcel source) {
			return new TipoComanda(source);
		}

		@Override
		public TipoComanda[] newArray(int size) {
			return new TipoComanda[size];
		}

	};

}
