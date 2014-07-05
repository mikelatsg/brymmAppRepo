package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class ServicioLocal implements Parcelable {
	private int idServicio;
	private TipoServicio tipoServicio;
	private boolean activo;
	private float importeMinimo;
	private float precio;

	public ServicioLocal(int idServicio, TipoServicio tipoServicio,
			boolean activo, float importeMinimo, float precio) {
		super();
		this.idServicio = idServicio;
		this.tipoServicio = tipoServicio;
		this.activo = activo;
		this.importeMinimo = importeMinimo;
		this.precio = precio;
	}

	public int getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(int idServicio) {
		this.idServicio = idServicio;
	}

	public TipoServicio getTipoServicio() {
		return tipoServicio;
	}

	public void setTipoServicio(TipoServicio tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public float getImporteMinimo() {
		return importeMinimo;
	}

	public void setImporteMinimo(float importeMinimo) {
		this.importeMinimo = importeMinimo;
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
		dest.writeInt(this.idServicio);
		dest.writeParcelable(this.tipoServicio, flags);
		dest.writeByte((byte) (this.activo ? 1 : 0));
		dest.writeFloat(this.importeMinimo);
		dest.writeFloat(this.precio);
	}

	protected ServicioLocal(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idServicio = in.readInt();
		this.tipoServicio = in.readParcelable(TipoServicio.class
				.getClassLoader());
		this.activo = in.readByte() != 0;
		this.importeMinimo = in.readFloat();
		this.precio = in.readFloat();
	}

	public static final Parcelable.Creator<ServicioLocal> CREATOR = new Parcelable.Creator<ServicioLocal>() {

		@Override
		public ServicioLocal createFromParcel(Parcel source) {
			return new ServicioLocal(source);
		}

		@Override
		public ServicioLocal[] newArray(int size) {
			return new ServicioLocal[size];
		}

	};

}
