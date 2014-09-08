package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class PlatoComanda extends Plato {
	private int idComandaMenu;
	private String estado;
	private int cantidad;

	public PlatoComanda(int idComandaMenu, Plato plato, String estado, int cantidad) {
		super(plato.getIdPlato(), plato.getTipoPlato(), plato.getNombre(),
				plato.getPrecio());

		this.idComandaMenu = idComandaMenu;
		this.estado = estado;
		this.cantidad = cantidad;
	}

	
	
	public int getidComandaMenu() {
		return idComandaMenu;
	}



	public void setidComandaMenu(int idComandaMenu) {
		this.idComandaMenu = idComandaMenu;
	}



	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(idComandaMenu);
		dest.writeString(estado);
		dest.writeInt(cantidad);
	}

	private PlatoComanda(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		this.idComandaMenu = in.readInt();
		this.estado = in.readString();
		this.cantidad = in.readInt();
	}

	public static final Parcelable.Creator<PlatoComanda> CREATOR = new Parcelable.Creator<PlatoComanda>() {

		@Override
		public PlatoComanda createFromParcel(Parcel source) {
			return new PlatoComanda(source);
		}

		@Override
		public PlatoComanda[] newArray(int size) {
			return new PlatoComanda[size];
		}

	};

}
