package com.brymm.brymmapp.usuario.pojo;

import java.util.List;

import com.brymm.brymmapp.usuario.interfaces.ListaArticulosPedido;

import android.os.Parcel;
import android.os.Parcelable;

public class ArticuloLocalCantidad extends ArticuloLocal implements
		ListaArticulosPedido {
	private int cantidad;

	public ArticuloLocalCantidad(ArticuloLocal articulo, int cantidad) {
		super(articulo.getIdArticulo(), articulo.getIdLocal(), articulo
				.getIdTipoArticulo(), articulo.getNombre(), articulo
				.getDescripcion(), articulo.getPrecio(), articulo
				.getTipoArticulo(), articulo.getIngredientes());
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

	private ArticuloLocalCantidad(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		this.cantidad = in.readInt();
	}

	public static final Parcelable.Creator<ArticuloLocalCantidad> CREATOR = new Parcelable.Creator<ArticuloLocalCantidad>() {

		@Override
		public ArticuloLocalCantidad createFromParcel(Parcel source) {
			return new ArticuloLocalCantidad(source);
		}

		@Override
		public ArticuloLocalCantidad[] newArray(int size) {
			return new ArticuloLocalCantidad[size];
		}

	};

	@Override
	public boolean isPersonalizado() {
		return false;
	}
}
