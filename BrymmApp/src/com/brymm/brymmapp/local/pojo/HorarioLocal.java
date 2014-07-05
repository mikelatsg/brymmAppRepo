package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class HorarioLocal implements Parcelable {
	private int idHorarioLocal;
	private DiaSemana dia;
	private String horaInicio;
	private String horaFin;

	public HorarioLocal(int idHorarioLocal, DiaSemana dia, String horaInicio,
			String horaFin) {
		super();
		this.idHorarioLocal = idHorarioLocal;
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	public int getIdHorarioLocal() {
		return idHorarioLocal;
	}

	public void setIdHorarioLocal(int idHorarioLocal) {
		this.idHorarioLocal = idHorarioLocal;
	}

	public DiaSemana getDia() {
		return dia;
	}

	public void setDia(DiaSemana dia) {
		this.dia = dia;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	public String getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idHorarioLocal);
		dest.writeParcelable(this.dia, flags);
		dest.writeString(this.horaInicio);
		dest.writeString(this.horaFin);
	}

	protected HorarioLocal(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idHorarioLocal = in.readInt();
		this.dia = in.readParcelable(DiaSemana.class.getClassLoader());
		this.horaInicio = in.readString();
		this.horaFin = in.readString();
	}

	public static final Parcelable.Creator<HorarioLocal> CREATOR = new Parcelable.Creator<HorarioLocal>() {

		@Override
		public HorarioLocal createFromParcel(Parcel source) {
			return new HorarioLocal(source);
		}

		@Override
		public HorarioLocal[] newArray(int size) {
			return new HorarioLocal[size];
		}

	};

}
