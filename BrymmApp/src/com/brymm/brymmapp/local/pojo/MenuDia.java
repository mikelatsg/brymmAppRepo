package com.brymm.brymmapp.local.pojo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuDia implements Parcelable {

	private int idMenuDia;
	private MenuLocal menu;
	private String fecha;
	private List<Plato> platos = new ArrayList<Plato>();

	public MenuDia(int idMenuDia, MenuLocal menu, String fecha,
			List<Plato> platos) {
		super();
		this.idMenuDia = idMenuDia;
		this.menu = menu;
		this.fecha = fecha;
		this.platos = platos;
	}

	public int getIdMenuDia() {
		return idMenuDia;
	}

	public void setIdMenuDia(int idMenuDia) {
		this.idMenuDia = idMenuDia;
	}

	public MenuLocal getMenu() {
		return menu;
	}

	public void setMenu(MenuLocal menu) {
		this.menu = menu;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public List<Plato> getPlatos() {
		return platos;
	}

	public void setPlatos(List<Plato> platos) {
		this.platos = platos;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idMenuDia);
		dest.writeParcelable(this.menu, flags);
		dest.writeString(this.fecha);		
		dest.writeTypedList(platos);
	}

	protected MenuDia(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idMenuDia = in.readInt();
		this.menu = in.readParcelable(MenuLocal.class
				.getClassLoader());
		this.fecha = in.readString();		
		in.readTypedList(platos, Plato.CREATOR);
	}

	public static final Parcelable.Creator<MenuDia> CREATOR = new Parcelable.Creator<MenuDia>() {

		@Override
		public MenuDia createFromParcel(Parcel source) {
			return new MenuDia(source);
		}

		@Override
		public MenuDia[] newArray(int size) {
			return new MenuDia[size];
		}

	};

}
