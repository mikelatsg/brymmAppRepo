package com.brymm.brymmapp.usuario.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Local implements Parcelable {

	private int idLocal;
	private String nombre;
	private String localidad;
	private String provincia;
	private String direccion;
	private String codigoPostal;
	private String email;
	private String telefono;
	private String tipoComida;

	public Local(int idLocal, String nombre, String localidad,
			String provincia, String direccion, String codigoPostal,
			String email, String telefono, String tipoComida) {
		super();
		this.idLocal = idLocal;
		this.nombre = nombre;
		this.localidad = localidad;
		this.provincia = provincia;
		this.direccion = direccion;
		this.codigoPostal = codigoPostal;
		this.email = email;
		this.telefono = telefono;
		this.tipoComida = tipoComida;
	}

	public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getTipoComida() {
		return tipoComida;
	}

	public void setTipoComida(String tipoComida) {
		this.tipoComida = tipoComida;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idLocal);
		dest.writeString(nombre);
		dest.writeString(localidad);
		dest.writeString(provincia);
		dest.writeString(direccion);
		dest.writeString(codigoPostal);
		dest.writeString(email);
		dest.writeString(telefono);
		dest.writeString(tipoComida);
	}

	private Local(Parcel in) {
		super();
		this.idLocal = in.readInt();
		this.nombre = in.readString();
		this.localidad = in.readString();
		this.provincia = in.readString();
		this.direccion = in.readString();
		this.codigoPostal = in.readString();
		this.email = in.readString();
		this.telefono = in.readString();
		this.tipoComida = in.readString();
	}

	public static final Parcelable.Creator<Local> CREATOR = new Parcelable.Creator<Local>() {

		@Override
		public Local createFromParcel(Parcel source) {
			return new Local(source);
		}

		@Override
		public Local[] newArray(int size) {
			return new Local[size];
		}

	};
}
