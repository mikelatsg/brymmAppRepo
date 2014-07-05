package com.brymm.brymmapp.local.pojo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Reserva implements Parcelable {
	private int idReserva;
	private Usuario usuario;
	private int numeroPersonas;
	private String fecha;
	private TipoMenu tipoMenu;
	private String horaInicio;
	private String horaFin;
	private String estado;
	private String motivo;
	private String observaciones;
	private String nombreEmisor;
	private List<Mesa> mesas = new ArrayList<Mesa>();

	public Reserva(int idReserva, Usuario usuario, int numeroPersonas,
			String fecha, TipoMenu tipoMenu, String horaInicio, String horaFin,
			String estado, String motivo, String observaciones,
			String nombreEmisor, List<Mesa> mesas) {
		super();
		this.idReserva = idReserva;
		this.usuario = usuario;
		this.numeroPersonas = numeroPersonas;
		this.fecha = fecha;
		this.tipoMenu = tipoMenu;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.estado = estado;
		this.motivo = motivo;
		this.observaciones = observaciones;
		this.nombreEmisor = nombreEmisor;
		this.mesas = mesas;
	}

	public int getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(int idReserva) {
		this.idReserva = idReserva;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getNumeroPersonas() {
		return numeroPersonas;
	}

	public void setNumeroPersonas(int numeroPersonas) {
		this.numeroPersonas = numeroPersonas;
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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getNombreEmisor() {
		return nombreEmisor;
	}

	public void setNombreEmisor(String nombreEmisor) {
		this.nombreEmisor = nombreEmisor;
	}

	public List<Mesa> getMesas() {
		return mesas;
	}

	public void setMesas(List<Mesa> mesas) {
		this.mesas = mesas;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idReserva);
		dest.writeParcelable(this.usuario, flags);
		dest.writeInt(this.numeroPersonas);
		dest.writeString(this.fecha);
		dest.writeParcelable(this.tipoMenu, flags);
		dest.writeString(this.horaInicio);
		dest.writeString(this.horaFin);
		dest.writeString(this.estado);
		dest.writeString(this.motivo);
		dest.writeString(this.observaciones);
		dest.writeString(this.nombreEmisor);
		dest.writeTypedList(this.mesas);
	}

	protected Reserva(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idReserva = in.readInt();
		this.usuario = in.readParcelable(Usuario.class.getClassLoader());
		this.numeroPersonas = in.readInt();
		this.fecha = in.readString();
		this.tipoMenu = in.readParcelable(TipoMenu.class.getClassLoader());
		this.horaInicio = in.readString();
		this.horaFin = in.readString();
		this.estado = in.readString();
		this.motivo = in.readString();
		this.observaciones = in.readString();
		this.nombreEmisor = in.readString();
		in.readTypedList(this.mesas, Mesa.CREATOR);
	}

	public static final Parcelable.Creator<Reserva> CREATOR = new Parcelable.Creator<Reserva>() {

		@Override
		public Reserva createFromParcel(Parcel source) {
			return new Reserva(source);
		}

		@Override
		public Reserva[] newArray(int size) {
			return new Reserva[size];
		}

	};
}
