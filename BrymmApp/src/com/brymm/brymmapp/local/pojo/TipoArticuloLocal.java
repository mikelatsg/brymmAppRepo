package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TipoArticuloLocal extends TipoArticulo implements Parcelable {

	private int idTipoArticuloLocal;
	private float precio;
	private boolean personalizar;

	public TipoArticuloLocal(int idTipoArticulo, String tipoArticulo,
			String descripcion, float precio, boolean personalizar,
			int idTipoArticuloLocal) {
		super(idTipoArticulo, tipoArticulo, descripcion);

		this.idTipoArticuloLocal = idTipoArticuloLocal;
		this.precio = precio;
		this.personalizar = personalizar;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public boolean isPersonalizar() {
		return personalizar;
	}

	public void setPersonalizar(boolean personalizar) {
		this.personalizar = personalizar;
	}

	public int getIdTipoArticuloLocal() {
		return idTipoArticuloLocal;
	}

	public void setIdTipoArticuloLocal(int idTipoArticuloLocal) {
		this.idTipoArticuloLocal = idTipoArticuloLocal;
	}
	
	public String toString(){
		return this.getTipoArticulo();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idTipoArticuloLocal);
		dest.writeFloat(this.precio);
		dest.writeByte((byte) (this.personalizar ? 1 : 0));
	}

	protected TipoArticuloLocal(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idTipoArticuloLocal = in.readInt();
		this.precio = in.readFloat();
		this.personalizar = in.readByte() != 0;
	}

	public static final Parcelable.Creator<TipoArticuloLocal> CREATOR = new Parcelable.Creator<TipoArticuloLocal>() {

		@Override
		public TipoArticuloLocal createFromParcel(Parcel source) {
			return new TipoArticuloLocal(source);
		}

		@Override
		public TipoArticuloLocal[] newArray(int size) {
			return new TipoArticuloLocal[size];
		}

	};

}
