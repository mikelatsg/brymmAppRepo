package com.brymm.brymmapp.usuario.pojo;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class PedidoUsuario implements Parcelable {

	private int idPedido;
	private String estado;
	private float precio;
	private String fechaPedido;
	private Direccion direccionEnvio;
	private String observaciones;
	private String nombreLocal;
	private String motivoRechazo;
	private String fechaEntrega;
	private ArrayList<ArticuloPedido> articulos = new ArrayList<ArticuloPedido>();

	public PedidoUsuario(int idPedido, String estado, float precio,
			String fechaPedido, Direccion direccionEnvio, String observaciones,
			String nombreLocal, String motivoRechazo, String fechaEntrega,
			ArrayList<ArticuloPedido> articulos) {
		super();
		this.idPedido = idPedido;
		this.estado = estado;
		this.precio = precio;
		this.fechaPedido = fechaPedido;
		this.direccionEnvio = direccionEnvio;
		this.observaciones = observaciones;
		this.nombreLocal = nombreLocal;
		this.motivoRechazo = motivoRechazo;
		this.fechaEntrega = fechaEntrega;
		this.articulos = articulos;
	}

	public int getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(int idPedido) {
		this.idPedido = idPedido;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public String getFechaPedido() {
		return fechaPedido;
	}

	public void setFechaPedido(String fechaPedido) {
		this.fechaPedido = fechaPedido;
	}

	public Direccion getDireccionEnvio() {
		return direccionEnvio;
	}

	public void setDireccionEnvio(Direccion direccionEnvio) {
		this.direccionEnvio = direccionEnvio;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getNombreLocal() {
		return nombreLocal;
	}

	public void setNombreLocal(String nombreLocal) {
		this.nombreLocal = nombreLocal;
	}

	public String getMotivoRechazo() {
		return motivoRechazo;
	}

	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}

	public String getFechaEntrega() {
		return fechaEntrega;
	}

	public void setFechaEntrega(String fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}

	public ArrayList<ArticuloPedido> getArticulos() {
		return articulos;
	}

	public void setArticulos(ArrayList<ArticuloPedido> articulos) {
		this.articulos = articulos;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idPedido);
		dest.writeString(estado);
		dest.writeFloat(precio);
		dest.writeString(fechaPedido);
		dest.writeParcelable(direccionEnvio, flags);
		dest.writeString(observaciones);
		dest.writeString(nombreLocal);
		dest.writeString(motivoRechazo);
		dest.writeString(fechaEntrega);
		dest.writeTypedList(articulos);
	}

	private PedidoUsuario(Parcel in) {
		super();
		this.idPedido = in.readInt();
		this.estado = in.readString();
		this.precio = in.readFloat();
		this.fechaPedido = in.readString();
		this.direccionEnvio = in.readParcelable(Direccion.class.getClassLoader());
		this.observaciones = in.readString();
		this.nombreLocal = in.readString();
		this.motivoRechazo = in.readString();
		this.fechaEntrega = in.readString();
		in.readTypedList(articulos, ArticuloPedido.CREATOR);
	}

	public static final Parcelable.Creator<PedidoUsuario> CREATOR = new Parcelable.Creator<PedidoUsuario>() {

		@Override
		public PedidoUsuario createFromParcel(Parcel source) {
			return new PedidoUsuario(source);
		}

		@Override
		public PedidoUsuario[] newArray(int size) {
			return new PedidoUsuario[size];
		}

	};
}
