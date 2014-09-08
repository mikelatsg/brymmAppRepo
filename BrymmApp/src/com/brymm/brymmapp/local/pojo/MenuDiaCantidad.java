package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuDiaCantidad extends MenuDia {
	private int cantidad;

	public MenuDiaCantidad(MenuDia menuDia, int cantidad) {
		super(menuDia.getIdMenuDia(), menuDia.getMenu(), menuDia.getFecha(),
				menuDia.getPlatos());

		this.cantidad = cantidad;
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
		dest.writeInt(cantidad);
	}

	private MenuDiaCantidad(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		this.cantidad = in.readInt();
	}

	public static final Parcelable.Creator<MenuDiaCantidad> CREATOR = new Parcelable.Creator<MenuDiaCantidad>() {

		@Override
		public MenuDiaCantidad createFromParcel(Parcel source) {
			return new MenuDiaCantidad(source);
		}

		@Override
		public MenuDiaCantidad[] newArray(int size) {
			return new MenuDiaCantidad[size];
		}

	};

}
