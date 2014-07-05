package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class HorarioPedido implements Parcelable {
	private int idHorarioPedido;
	private DiaSemana dia;
	private String horaInicio;
	private String horaFin;

	public HorarioPedido(int idHorarioLocal, DiaSemana dia, String horaInicio,
			String horaFin) {
		super();
		this.idHorarioPedido = idHorarioLocal;
		this.dia = dia;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	public int getIdHorarioPedido() {
		return idHorarioPedido;
	}

	public void setIdHorarioPedido(int idHorarioPedido) {
		this.idHorarioPedido = idHorarioPedido;
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
		dest.writeInt(this.idHorarioPedido);
		dest.writeParcelable(this.dia, flags);
		dest.writeString(this.horaInicio);
		dest.writeString(this.horaFin);
	}

	protected HorarioPedido(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idHorarioPedido = in.readInt();
		this.dia = in.readParcelable(DiaSemana.class.getClassLoader());
		this.horaInicio = in.readString();
		this.horaFin = in.readString();
	}

	public static final Parcelable.Creator<HorarioPedido> CREATOR = new Parcelable.Creator<HorarioPedido>() {

		@Override
		public HorarioPedido createFromParcel(Parcel source) {
			return new HorarioPedido(source);
		}

		@Override
		public HorarioPedido[] newArray(int size) {
			return new HorarioPedido[size];
		}

	};

}
