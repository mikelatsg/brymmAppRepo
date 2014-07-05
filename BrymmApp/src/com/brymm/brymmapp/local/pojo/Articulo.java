package com.brymm.brymmapp.local.pojo;

import java.util.ArrayList;
import java.util.List;


import android.os.Parcel;
import android.os.Parcelable;

public class Articulo implements Parcelable {

	private int idArticulo;
	private TipoArticulo tipoArticulo;
	private String nombre;
	private String descripcion;
	private float precio;
	private boolean validoPedidos;
	private List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

	public Articulo() {
		super();
		this.idArticulo = -1;
		this.tipoArticulo = null;
		this.nombre = "";
		this.descripcion = "";
		this.precio = 0;
		this.validoPedidos = false;
	}
	
	public Articulo(int idArticulo, TipoArticulo tipoArticulo, String nombre,
			String descripcion, float precio, boolean validoPedidos,
			List<Ingrediente> ingredientes) {
		super();
		this.idArticulo = idArticulo;
		this.tipoArticulo = tipoArticulo;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precio = precio;
		this.validoPedidos = validoPedidos;
		this.ingredientes = ingredientes;
	}

	public int getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(int idArticulo) {
		this.idArticulo = idArticulo;
	}

	public TipoArticulo getTipoArticulo() {
		return tipoArticulo;
	}

	public void setTipoArticulo(TipoArticulo tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public boolean isValidoPedidos() {
		return validoPedidos;
	}

	public void setValidoPedidos(boolean validoPedidos) {
		this.validoPedidos = validoPedidos;
	}

	
	public List<Ingrediente> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}

	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idArticulo);
		dest.writeParcelable(this.tipoArticulo, flags);
		dest.writeString(this.nombre);
		dest.writeString(this.descripcion);
		dest.writeFloat(this.precio);
		dest.writeByte((byte) (this.validoPedidos ? 1 : 0));
		dest.writeTypedList(ingredientes);
	}

	protected Articulo(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idArticulo = in.readInt();
		this.tipoArticulo = in.readParcelable(TipoArticulo.class
				.getClassLoader());
		this.nombre = in.readString();
		this.descripcion = in.readString();
		this.precio = in.readFloat();
		this.validoPedidos = in.readByte() != 0;
		in.readTypedList(ingredientes, Ingrediente.CREATOR);
	}

	public static final Parcelable.Creator<Articulo> CREATOR = new Parcelable.Creator<Articulo>() {

		@Override
		public Articulo createFromParcel(Parcel source) {
			return new Articulo(source);
		}

		@Override
		public Articulo[] newArray(int size) {
			return new Articulo[size];
		}

	};

}
