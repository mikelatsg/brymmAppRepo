package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TipoMenu implements Parcelable {
	private int idTipoMenu;
	private String descripcion;

	public TipoMenu(int idTipoMenu, String descripcion) {
		super();
		this.idTipoMenu = idTipoMenu;
		this.descripcion = descripcion;
	}

	public int getIdTipoMenu() {
		return idTipoMenu;
	}

	public void setIdTipoMenu(int idTipoMenu) {
		this.idTipoMenu = idTipoMenu;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idTipoMenu);
		dest.writeString(this.descripcion);
	}

	protected TipoMenu(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idTipoMenu = in.readInt();
		this.descripcion = in.readString();
	}

	public static final Parcelable.Creator<TipoMenu> CREATOR = new Parcelable.Creator<TipoMenu>() {

		@Override
		public TipoMenu createFromParcel(Parcel source) {
			return new TipoMenu(source);
		}

		@Override
		public TipoMenu[] newArray(int size) {
			return new TipoMenu[size];
		}

	};

}
