package com.brymm.brymmapp.local.pojo;


import android.os.Parcel;
import android.os.Parcelable;

public class DetalleComanda implements Parcelable {

	private int idDetalleComanda;
	private TipoComanda tipoComanda;
	private int cantidad;
	private float precio;		
	private String estado;	
	private ArticuloCantidad articulo;
	private MenuComanda menuComanda;		

	public DetalleComanda(int idDetalleComanda, TipoComanda tipoComanda,
			int cantidad, float precio, String estado,
			ArticuloCantidad articulo, MenuComanda menuComanda) {
		super();
		this.idDetalleComanda = idDetalleComanda;
		this.tipoComanda = tipoComanda;
		this.cantidad = cantidad;
		this.precio = precio;
		this.estado = estado;
		this.articulo = articulo;
		this.menuComanda = menuComanda;
	}

	public int getIdDetalleComanda() {
		return idDetalleComanda;
	}

	public void setIdDetalleComanda(int idDetalleComanda) {
		this.idDetalleComanda = idDetalleComanda;
	}

	public TipoComanda getTipoComanda() {
		return tipoComanda;
	}

	public void setTipoComanda(TipoComanda tipoComanda) {
		this.tipoComanda = tipoComanda;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public MenuComanda getMenuComanda() {
		return menuComanda;
	}

	public void setMenuComanda(MenuComanda menuComanda) {
		this.menuComanda = menuComanda;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}


	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Float getPrecio() {
		return precio;
	}

	public void setPrecio(Float precio) {
		this.precio = precio;
	}

	public ArticuloCantidad getArticulo() {
		return articulo;
	}

	public void setArticulo(ArticuloCantidad articulo) {
		this.articulo = articulo;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}		
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idDetalleComanda);
		dest.writeParcelable(this.tipoComanda, flags);
		dest.writeInt(this.cantidad);
		dest.writeFloat(this.precio);
		dest.writeString(this.estado);
		dest.writeParcelable(this.articulo, flags);
		dest.writeParcelable(this.menuComanda, flags);
	}

	protected DetalleComanda(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idDetalleComanda = in.readInt();
		this.tipoComanda = in.readParcelable(TipoComanda.class
				.getClassLoader());
		this.cantidad = in.readInt();
		this.precio = in.readFloat();
		this.estado = in.readString();	
		this.articulo = in.readParcelable(ArticuloCantidad.class
				.getClassLoader());
		this.menuComanda = in.readParcelable(MenuComanda.class
				.getClassLoader());
	}

	public static final Parcelable.Creator<DetalleComanda> CREATOR = new Parcelable.Creator<DetalleComanda>() {

		@Override
		public DetalleComanda createFromParcel(Parcel source) {
			return new DetalleComanda(source);
		}

		@Override
		public DetalleComanda[] newArray(int size) {
			return new DetalleComanda[size];
		}

	};


}
