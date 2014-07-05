package com.brymm.brymmapp.usuario.pojo;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ArticuloBase implements Parcelable {

	private String nombre;
	private float precio;
	private String tipoArticulo;
	private List<String> ingredientes;

	public ArticuloBase() {
		this.nombre = "";
		this.precio = 0;
		this.tipoArticulo = "";
		this.ingredientes = new ArrayList<String>();
	}

	public ArticuloBase(String nombre, float precio, String tipoArticulo,
			List<String> ingredientes) {
		super();
		this.nombre = nombre;
		this.precio = precio;
		this.tipoArticulo = tipoArticulo;
		this.ingredientes = ingredientes;
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

	public String getTipoArticulo() {
		return tipoArticulo;
	}

	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}

	public List<String> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<String> ingredientes) {
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
		dest.writeFloat(this.precio);
		dest.writeString(this.tipoArticulo);
		dest.writeStringList(this.ingredientes);

	}

	protected ArticuloBase(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.setNombre(in.readString());
		this.setPrecio(in.readFloat());
		this.setTipoArticulo(in.readString());
		in.readStringList(ingredientes);
	}

	/*
	 * public static final Parcelable.Creator<ArticuloBase> CREATOR = new
	 * Parcelable.Creator<ArticuloBase>() {
	 * 
	 * @Override public ArticuloBase createFromParcel(Parcel source) { return
	 * new ArticuloBase(source); }
	 * 
	 * @Override public ArticuloBase[] newArray(int size) { return new
	 * ArticuloBase[size]; }
	 * 
	 * };
	 */

}
