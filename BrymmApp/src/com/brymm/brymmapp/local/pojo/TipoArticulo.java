package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TipoArticulo implements Parcelable {

	private int idTipoArticulo;
	private String tipoArticulo;
	private String descripcion;

	protected TipoArticulo() {

	}

	public TipoArticulo(int idTipoArticulo, String tipoArticulo,
			String descripcion) {
		super();
		this.idTipoArticulo = idTipoArticulo;
		this.tipoArticulo = tipoArticulo;
		this.descripcion = descripcion;
	}

	public int getIdTipoArticulo() {
		return idTipoArticulo;
	}

	public void setIdTipoArticulo(int idTipoArticulo) {
		this.idTipoArticulo = idTipoArticulo;
	}

	public String getTipoArticulo() {
		return tipoArticulo;
	}

	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
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
		dest.writeInt(this.idTipoArticulo);
		dest.writeString(this.tipoArticulo);
		dest.writeString(this.descripcion);
	}

	protected TipoArticulo(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idTipoArticulo = in.readInt();
		this.tipoArticulo = in.readString();
		this.descripcion = in.readString();
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
