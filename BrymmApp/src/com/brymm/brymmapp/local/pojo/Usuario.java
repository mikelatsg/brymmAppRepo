package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable {

	private int idUsuario;
	private String nick;
	private String nombre;
	private String apellido;
	private String email;
	private String localidad;
	private String provincia;
	private int codPostal;

	public Usuario(int idUsuario, String nick, String nombre, String apellido,
			String email, String localidad, String provincia, int codPostal) {
		super();
		this.idUsuario = idUsuario;
		this.nick = nick;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.localidad = localidad;
		this.provincia = provincia;
		this.codPostal = codPostal;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public int getCodPostal() {
		return codPostal;
	}

	public void setCodPostal(int codPostal) {
		this.codPostal = codPostal;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.idUsuario);
		dest.writeString(this.nick);
		dest.writeString(this.nombre);
		dest.writeString(this.apellido);
		dest.writeString(this.email);
		dest.writeString(this.localidad);
		dest.writeString(this.provincia);
		dest.writeInt(this.codPostal);
	}

	protected Usuario(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		this.idUsuario = in.readInt();
		this.nick = in.readString();
		this.nombre = in.readString();
		this.apellido = in.readString();
		this.email = in.readString();
		this.localidad = in.readString();
		this.provincia = in.readString();
		this.codPostal = in.readInt();
	}

	public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {

		@Override
		public Usuario createFromParcel(Parcel source) {
			return new Usuario(source);
		}

		@Override
		public Usuario[] newArray(int size) {
			return new Usuario[size];
		}

	};

}
