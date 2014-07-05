package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class ArticuloCantidad extends Articulo {
	private int cantidad;

	public ArticuloCantidad(Articulo articulo, int cantidad) {
		super(articulo.getIdArticulo(), articulo.getTipoArticulo(), articulo
				.getNombre(), articulo.getDescripcion(), articulo.getPrecio(),
				articulo.isValidoPedidos(), articulo.getIngredientes());

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

	private ArticuloCantidad(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		this.cantidad = in.readInt();
	}

	public static final Parcelable.Creator<ArticuloCantidad> CREATOR = new Parcelable.Creator<ArticuloCantidad>() {

		@Override
		public ArticuloCantidad createFromParcel(Parcel source) {
			return new ArticuloCantidad(source);
		}

		@Override
		public ArticuloCantidad[] newArray(int size) {
			return new ArticuloCantidad[size];
		}

	};

}
