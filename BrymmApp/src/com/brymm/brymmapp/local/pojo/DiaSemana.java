package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class DiaSemana implements Parcelable {
	private int idDia;
	private String dia;

	public DiaSemana(int idDia, String dia) {
		super();
		this.idDia = idDia;
		this.dia = dia;
	}

	public int getIdDia() {
		return idDia;
	}

	public void setIdDia(int idDia) {
		this.idDia = idDia;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idDia);
		dest.writeString(this.dia);
	}

	protected DiaSemana(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idDia = in.readInt();
		this.dia = in.readString();
	}

	public static final Parcelable.Creator<DiaSemana> CREATOR = new Parcelable.Creator<DiaSemana>() {

		@Override
		public DiaSemana createFromParcel(Parcel source) {
			return new DiaSemana(source);
		}

		@Override
		public DiaSemana[] newArray(int size) {
			return new DiaSemana[size];
		}

	};

}
