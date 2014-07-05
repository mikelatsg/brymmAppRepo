package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class DiaCierre implements Parcelable {

	private int idDiaCierre;
	private String fecha;

	public DiaCierre(int idDiaCierre, String fecha) {
		super();
		this.idDiaCierre = idDiaCierre;
		this.fecha = fecha;
	}

	public int getIdDiaCierre() {
		return idDiaCierre;
	}

	public void setIdDiaCierre(int idDiaCierre) {
		this.idDiaCierre = idDiaCierre;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
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
		dest.writeInt(this.idDiaCierre);
		dest.writeString(this.fecha);
	}

	protected DiaCierre(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idDiaCierre = in.readInt();
		this.fecha = in.readString();
	}

	public static final Parcelable.Creator<DiaCierre> CREATOR = new Parcelable.Creator<DiaCierre>() {

		@Override
		public DiaCierre createFromParcel(Parcel source) {
			return new DiaCierre(source);
		}

		@Override
		public DiaCierre[] newArray(int size) {
			return new DiaCierre[size];
		}

	};

}
