package com.brymm.brymmapp.local.pojo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuComanda implements Parcelable {

	private int cantidad;
	private MenuLocal menu;
	private List<PlatoComanda> platosComanda = new ArrayList<PlatoComanda>();

	public MenuComanda(MenuLocal menu, List<PlatoComanda> platosComanda, int cantidad) {
		super();
		this.menu = menu;
		this.platosComanda = platosComanda;
		this.cantidad = cantidad;
	}

	public MenuComanda() {
		super();		
		this.menu = null;
		this.platosComanda = null;
	}


	public MenuLocal getMenu() {
		return menu;
	}

	public void setMenu(MenuLocal menu) {
		this.menu = menu;
	}


	public List<PlatoComanda> getPlatos() {
		return platosComanda;
	}

	public void setPlatos(List<PlatoComanda> platosComanda) {
		this.platosComanda = platosComanda;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeParcelable(this.menu, flags);		
		dest.writeTypedList(this.platosComanda);
		dest.writeInt(this.cantidad);
	}

	protected MenuComanda(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {		
		this.menu = in.readParcelable(MenuLocal.class.getClassLoader());		
		in.readTypedList(platosComanda, PlatoComanda.CREATOR);
		this.cantidad = in.readInt();
	}

	public static final Parcelable.Creator<MenuComanda> CREATOR = new Parcelable.Creator<MenuComanda>() {

		@Override
		public MenuComanda createFromParcel(Parcel source) {
			return new MenuComanda(source);
		}

		@Override
		public MenuComanda[] newArray(int size) {
			return new MenuComanda[size];
		}

	};

}
