package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Camarero implements Parcelable {

	private int idCamarero;
	private String nombre;
	private boolean activo;
	private boolean controlTotal;	

	public Camarero(int idCamarero, String nombre, boolean activo,
			boolean controlTotal) {
		super();
		this.idCamarero = idCamarero;
		this.nombre = nombre;
		this.activo = activo;
		this.controlTotal = controlTotal;
	}

	public int getIdCamarero() {
		return idCamarero;
	}

	public void setIdCamarero(int idCamarero) {
		this.idCamarero = idCamarero;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public boolean isControlTotal() {
		return controlTotal;
	}

	public void setControlTotal(boolean controlTotal) {
		this.controlTotal = controlTotal;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idCamarero);
		dest.writeString(this.nombre);
		dest.writeByte((byte) (this.activo ? 1 : 0));
		dest.writeByte((byte) (this.controlTotal ? 1 : 0));				
	}

	protected Camarero(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idCamarero = in.readInt();
		this.nombre = in.readString();
		this.activo = in.readByte() != 0;
		this.controlTotal = in.readByte() != 0;		
	}

	public static final Parcelable.Creator<Camarero> CREATOR = new Parcelable.Creator<Camarero>() {

		@Override
		public Camarero createFromParcel(Parcel source) {
			return new Camarero(source);
		}

		@Override
		public Camarero[] newArray(int size) {
			return new Camarero[size];
		}

	};

}
