package com.brymm.brymmapp.local.pojo;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Pedido implements Parcelable {

	private int idPedido;
	private Usuario usuario;
	private String fecha;
	private String fechaEntrega;
	private String estado;
	private Float precio;
	private String observaciones;
	private Direccion direccion;
	private String motivoRechazo;
	private List<ArticuloCantidad> articulos;

	public Pedido(int idPedido, Usuario usuario, String fecha,
			String fechaEntrega, String estado, Float precio,
			String observaciones, Direccion direccion, String motivoRechazo,
			List<ArticuloCantidad> articulos) {
		super();
		this.idPedido = idPedido;
		this.usuario = usuario;
		this.fecha = fecha;
		this.fechaEntrega = fechaEntrega;
		this.estado = estado;
		this.precio = precio;
		this.observaciones = observaciones;
		this.direccion = direccion;
		this.motivoRechazo = motivoRechazo;
		this.articulos = articulos;
	}

	public int getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getFechaEntrega() {
		return fechaEntrega;
	}

	public void setFechaEntrega(String fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
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

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Direccion getDireccion() {
		return direccion;
	}

	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
	}

	public String getMotivoRechazo() {
		return motivoRechazo;
	}

	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}

	public List<ArticuloCantidad> getArticulos() {
		return articulos;
	}

	public void setArticulos(List<ArticuloCantidad> articulos) {
		this.articulos = articulos;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idPedido);
		dest.writeParcelable(this.usuario, flags);
		dest.writeString(this.fecha);
		dest.writeString(this.fechaEntrega);
		dest.writeString(this.estado);
		dest.writeFloat(this.precio);
		dest.writeString(this.observaciones);
		dest.writeParcelable(this.direccion,flags);
		dest.writeString(this.motivoRechazo);
		dest.writeTypedList(articulos);
	}

	protected Pedido(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idPedido = in.readInt();
		this.usuario = in.readParcelable(Usuario.class
				.getClassLoader());
		this.fecha = in.readString();
		this.fechaEntrega = in.readString();
		this.estado = in.readString();
		this.precio = in.readFloat();
		this.observaciones = in.readString();
		this.direccion = in.readParcelable(Direccion.class
				.getClassLoader());
		this.motivoRechazo = in.readString();
		in.readTypedList(articulos, ArticuloCantidad.CREATOR);		
	}

	public static final Parcelable.Creator<Pedido> CREATOR = new Parcelable.Creator<Pedido>() {

		@Override
		public Pedido createFromParcel(Parcel source) {
			return new Pedido(source);
		}

		@Override
		public Pedido[] newArray(int size) {
			return new Pedido[size];
		}

	};


}
