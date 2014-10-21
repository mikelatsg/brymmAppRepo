package com.brymm.brymmapp.local.pojo;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Comanda implements Parcelable {

	private int idComanda;
	private String destino;
	private Camarero camarero;
	private String estado;
	private Float precio;
	private Mesa mesa;
	private String fecha;
	private String observaciones;
	private List<DetalleComanda> detallesComanda;

	public Comanda(int idComanda, String destino, Camarero camarero,
			String estado, Float precio, Mesa mesa, String fecha,
			String observaciones, List<DetalleComanda> detallesComanda) {
		super();
		this.idComanda = idComanda;
		this.destino = destino;
		this.camarero = camarero;
		this.estado = estado;
		this.precio = precio;
		this.mesa = mesa;
		this.fecha = fecha;
		this.observaciones = observaciones;
		this.detallesComanda = detallesComanda;
	}

	public int getIdComanda() {
		return idComanda;
	}

	public void setIdComanda(int idComanda) {
		this.idComanda = idComanda;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public Camarero getCamarero() {
		return camarero;
	}

	public void setCamarero(Camarero camarero) {
		this.camarero = camarero;
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

	public Mesa getMesa() {
		return mesa;
	}

	public void setMesa(Mesa mesa) {
		this.mesa = mesa;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public List<DetalleComanda> getDetallesComanda() {
		return detallesComanda;
	}

	public void setDetallesComanda(List<DetalleComanda> detallesComanda) {
		this.detallesComanda = detallesComanda;
	}
	
	public String toString(){
		String text = "";
		text += Integer.toString(this.idComanda);
		text +=  " - ";
		if (this.mesa != null){
			text += this.mesa.getNombre();
		}else{
			text += this.destino;
		}
		return text;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idComanda);
		dest.writeString(this.destino);
		dest.writeParcelable(this.camarero, flags);
		dest.writeString(this.estado);
		dest.writeFloat(this.precio);
		dest.writeParcelable(this.mesa, flags);
		dest.writeString(this.fecha);
		dest.writeString(this.observaciones);
		dest.writeTypedList(detallesComanda);
	}

	protected Comanda(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idComanda = in.readInt();
		this.destino = in.readString();
		this.camarero = in.readParcelable(Camarero.class.getClassLoader());
		this.estado = in.readString();
		this.precio = in.readFloat();
		this.mesa = in.readParcelable(Mesa.class.getClassLoader());
		this.fecha = in.readString();
		this.observaciones = in.readString();
		in.readTypedList(detallesComanda, DetalleComanda.CREATOR);
	}

	public static final Parcelable.Creator<Comanda> CREATOR = new Parcelable.Creator<Comanda>() {

		@Override
		public Comanda createFromParcel(Parcel source) {
			return new Comanda(source);
		}

		@Override
		public Comanda[] newArray(int size) {
			return new Comanda[size];
		}

	};

}
