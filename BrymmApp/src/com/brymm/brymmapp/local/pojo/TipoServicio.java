package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TipoServicio implements Parcelable {
	private int idTipoServicio;
	private String servicio;
	private String descripcion;
	private boolean mostrarBuscador;

	public TipoServicio(int idTipoServicio, String servicio,
			String descripcion, boolean mostrarBuscador) {
		super();
		this.idTipoServicio = idTipoServicio;
		this.servicio = servicio;
		this.descripcion = descripcion;
		this.mostrarBuscador = mostrarBuscador;
	}

	public int getIdTipoServicio() {
		return idTipoServicio;
	}

	public void setIdTipoServicio(int idTipoServicio) {
		this.idTipoServicio = idTipoServicio;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isMostrarBuscador() {
		return mostrarBuscador;
	}

	public void setMostrarBuscador(boolean mostrarBuscador) {
		this.mostrarBuscador = mostrarBuscador;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idTipoServicio);
		dest.writeString(this.servicio);
		dest.writeString(this.descripcion);
		dest.writeByte((byte) (this.mostrarBuscador ? 1 : 0));
	}

	protected TipoServicio(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idTipoServicio = in.readInt();
		this.servicio = in.readString();
		this.descripcion = in.readString();
		this.mostrarBuscador = in.readByte() != 0;
	}

	public static final Parcelable.Creator<TipoServicio> CREATOR = new Parcelable.Creator<TipoServicio>() {

		@Override
		public TipoServicio createFromParcel(Parcel source) {
			return new TipoServicio(source);
		}

		@Override
		public TipoServicio[] newArray(int size) {
			return new TipoServicio[size];
		}

	};

}
