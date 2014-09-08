package com.brymm.brymmapp.local.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class IngredientePerComanda extends Ingrediente {
	private int idComandaArticuloPer;

	public IngredientePerComanda(Ingrediente ingrediente,
			int idComandaArticuloPer) {
		super(ingrediente.getIdIngrediente(), ingrediente.getNombre(),
				ingrediente.getDescripcion(), ingrediente.getPrecio());

		this.idComandaArticuloPer = idComandaArticuloPer;
	}



	public int getIdComandaArticuloPer() {
		return idComandaArticuloPer;
	}



	public void setIdComandaArticuloPer(int idComandaArticuloPer) {
		this.idComandaArticuloPer = idComandaArticuloPer;
	}



	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(idComandaArticuloPer);
	}

	private IngredientePerComanda(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		this.idComandaArticuloPer = in.readInt();
	}

	public static final Parcelable.Creator<IngredientePerComanda> CREATOR = new Parcelable.Creator<IngredientePerComanda>() {

		@Override
		public IngredientePerComanda createFromParcel(Parcel source) {
			return new IngredientePerComanda(source);
		}

		@Override
		public IngredientePerComanda[] newArray(int size) {
			return new IngredientePerComanda[size];
		}

	};

}
