package com.brymm.brymmapp.usuario.pojo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ArticuloPedido extends ArticuloBase implements Parcelable {

	private int cantidad;
	private Boolean personalizado;

	public ArticuloPedido(String nombre, float precio, int cantidad,
			String tipoArticulo, Boolean personalizado,
			ArrayList<String> ingredientes) {
		super(nombre, precio, tipoArticulo, ingredientes);
		this.cantidad = cantidad;
		this.personalizado = personalizado;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public Boolean getPersonalizado() {
		return personalizado;
	}

	public void setPersonalizado(Boolean personalizado) {
		this.personalizado = personalizado;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(cantidad);
		dest.writeByte((byte) (personalizado ? 1 : 0));
	}

	private ArticuloPedido(Parcel in) {
		readFromParcel(in);				
	}
	
	public void readFromParcel(Parcel in){
		super.readFromParcel(in);
		this.cantidad = in.readInt();
		this.personalizado = in.readByte() != 0;
	}

	public static final Parcelable.Creator<ArticuloPedido> CREATOR = new Parcelable.Creator<ArticuloPedido>() {

		@Override
		public ArticuloPedido createFromParcel(Parcel source) {
			return new ArticuloPedido(source);
		}

		@Override
		public ArticuloPedido[] newArray(int size) {
			return new ArticuloPedido[size];
		}

	};
}
