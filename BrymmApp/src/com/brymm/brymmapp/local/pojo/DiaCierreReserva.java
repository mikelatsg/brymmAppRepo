package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class DiaCierreReserva implements Parcelable {

	private int idDiaCierreReserva;
	private String fecha;
	private TipoMenu tipoMenu;	

	

	public DiaCierreReserva(int idDiaCierreReserva, String fecha,
			TipoMenu tipoMenu) {
		super();
		this.idDiaCierreReserva = idDiaCierreReserva;
		this.fecha = fecha;
		this.tipoMenu = tipoMenu;
	}
	
	

	public int getIdDiaCierreReserva() {
		return idDiaCierreReserva;
	}



	public void setIdDiaCierreReserva(int idDiaCierreReserva) {
		this.idDiaCierreReserva = idDiaCierreReserva;
	}



	public String getFecha() {
		return fecha;
	}



	public void setFecha(String fecha) {
		this.fecha = fecha;
	}



	public TipoMenu getTipoMenu() {
		return tipoMenu;
	}



	public void setTipoMenu(TipoMenu tipoMenu) {
		this.tipoMenu = tipoMenu;
	}



	@Override
	public String toString() {
		return this.fecha;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idDiaCierreReserva);
		dest.writeString(this.fecha);
		dest.writeParcelable(this.tipoMenu, flags);
	}

	protected DiaCierreReserva(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idDiaCierreReserva = in.readInt();
		this.fecha = in.readString();
		this.tipoMenu = in.readParcelable(TipoMenu.class
				.getClassLoader());
	}

	public static final Parcelable.Creator<DiaCierreReserva> CREATOR = new Parcelable.Creator<DiaCierreReserva>() {

		@Override
		public DiaCierreReserva createFromParcel(Parcel source) {
			return new DiaCierreReserva(source);
		}

		@Override
		public DiaCierreReserva[] newArray(int size) {
			return new DiaCierreReserva[size];
		}

	};

}
