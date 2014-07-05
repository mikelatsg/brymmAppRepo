package com.brymm.brymmapp.usuario.pojo;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.usuario.interfaces.ListaArticulosPedido;

import android.os.Parcel;
import android.os.Parcelable;

public class ArticuloPersonalizado implements Parcelable, ListaArticulosPedido {

	private String nombre;
	private TipoArticulo tipoArticulo;
	private int cantidad;
	private List<IngredienteLocal> ingredientes = new ArrayList<IngredienteLocal>();

	public ArticuloPersonalizado(String nombre, TipoArticulo tipoArticulo,
			int cantidad, List<IngredienteLocal> ingredientes) {
		super();
		this.nombre = nombre;
		this.tipoArticulo = tipoArticulo;
		this.cantidad = cantidad;
		this.ingredientes = ingredientes;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public TipoArticulo getTipoArticulo() {
		return tipoArticulo;
	}

	public void setTipoArticulo(TipoArticulo tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public List<IngredienteLocal> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<IngredienteLocal> ingredientes) {
		this.ingredientes = ingredientes;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.nombre);
		dest.writeParcelable(tipoArticulo, flags);
		dest.writeInt(this.cantidad);
		dest.writeTypedList(ingredientes);
		// dest.writeList(ingredientes);
	}

	protected ArticuloPersonalizado(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.nombre = in.readString();
		this.tipoArticulo = in.readParcelable(TipoArticulo.class
				.getClassLoader());
		this.cantidad = in.readInt();
		in.readTypedList(ingredientes, IngredienteLocal.CREATOR);
		// in.readList(this.ingredientes, );
	}

	public static final Parcelable.Creator<ArticuloPersonalizado> CREATOR = new Parcelable.Creator<ArticuloPersonalizado>() {

		@Override
		public ArticuloPersonalizado createFromParcel(Parcel source) {
			return new ArticuloPersonalizado(source);
		}

		@Override
		public ArticuloPersonalizado[] newArray(int size) {
			return new ArticuloPersonalizado[size];
		}

	};

	@Override
	public boolean isPersonalizado() {
		return true;
	}

}
