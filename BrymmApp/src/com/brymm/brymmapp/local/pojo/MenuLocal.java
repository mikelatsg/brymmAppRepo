package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuLocal implements Parcelable {
	
	private int idMenu;
	private String nombre;
	private float precio;
	private boolean carta;
	private TipoMenu tipoMenu;


	public MenuLocal(int idMenu, String nombre, float precio, boolean carta,
			TipoMenu tipoMenu) {
		super();
		this.idMenu = idMenu;
		this.nombre = nombre;
		this.precio = precio;
		this.carta = carta;
		this.tipoMenu = tipoMenu;
	}
	
	

	public int getIdMenu() {
		return idMenu;
	}



	public void setIdMenu(int idMenu) {
		this.idMenu = idMenu;
	}



	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public float getPrecio() {
		return precio;
	}



	public void setPrecio(float precio) {
		this.precio = precio;
	}



	public boolean isCarta() {
		return carta;
	}



	public void setCarta(boolean carta) {
		this.carta = carta;
	}



	public TipoMenu getTipoMenu() {
		return tipoMenu;
	}



	public void setTipoMenu(TipoMenu tipoMenu) {
		this.tipoMenu = tipoMenu;
	}



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idMenu);		
		dest.writeString(this.nombre);
		dest.writeFloat(this.precio);
		dest.writeByte((byte) (this.carta ? 1 : 0));
		dest.writeParcelable(this.tipoMenu, flags);
	}

	protected MenuLocal(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idMenu = in.readInt();		
		this.nombre = in.readString();
		this.precio = in.readFloat();
		this.carta = in.readByte() != 0;
		this.tipoMenu = in.readParcelable(TipoMenu.class
				.getClassLoader());
	}

	public static final Parcelable.Creator<MenuLocal> CREATOR = new Parcelable.Creator<MenuLocal>() {

		@Override
		public MenuLocal createFromParcel(Parcel source) {
			return new MenuLocal(source);
		}

		@Override
		public MenuLocal[] newArray(int size) {
			return new MenuLocal[size];
		}

	};

}
