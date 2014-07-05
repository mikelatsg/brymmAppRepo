package com.brymm.brymmapp.usuario.pojo;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ArticuloLocal extends ArticuloBase {

	private int idArticulo;
	private int idLocal;
	private int idTipoArticulo;
	private String descripcion;

	public ArticuloLocal() {
		this.idArticulo = -1;
		this.idLocal = -1;
		this.idTipoArticulo = -1;
		this.descripcion = "";
	}

	public ArticuloLocal(int idArticulo, int idLocal, int idTipoArticulo,
			String nombre, String descripcion, float precio,
			String tipoArticulo, List<String> ingredientes) {
		super(nombre, precio, tipoArticulo, ingredientes);
		this.idArticulo = idArticulo;
		this.idLocal = idLocal;
		this.idTipoArticulo = idTipoArticulo;
		this.descripcion = descripcion;

	}

	public int getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(int idArticulo) {
		this.idArticulo = idArticulo;
	}

	public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public int getIdTipoArticulo() {
		return idTipoArticulo;
	}

	public void setIdTipoArticulo(int idTipoArticulo) {
		this.idTipoArticulo = idTipoArticulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(idArticulo);
		dest.writeInt(idLocal);
		dest.writeInt(idTipoArticulo);
		dest.writeString(descripcion);
	}

	private ArticuloLocal(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		this.idArticulo = in.readInt();
		this.idLocal = in.readInt();
		this.idTipoArticulo = in.readInt();
		this.descripcion = in.readString();
	}

	public static final Parcelable.Creator<ArticuloLocal> CREATOR = new Parcelable.Creator<ArticuloLocal>() {

		@Override
		public ArticuloLocal createFromParcel(Parcel source) {
			return new ArticuloLocal(source);
		}

		@Override
		public ArticuloLocal[] newArray(int size) {
			return new ArticuloLocal[size];
		}

	};

}
