package com.brymm.brymmapp.usuario.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.pojo.Local;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LocalAdapter extends ArrayAdapter<Local> {
	private Context context;
	private int textViewResourceId;
	private List<Local> locales;

	private TextView tvNombre, tvTipoComida, tvDireccion, tvTelefono,
			tvLocalidad;

	public LocalAdapter(Context context, int textViewResourceId,
			List<Local> locales) {
		super(context, textViewResourceId, locales);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.locales = locales;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView.findViewById(R.id.itemLocalTvNombre);
		tvTipoComida = (TextView) convertView
				.findViewById(R.id.itemLocalTvTipoComida);
		tvDireccion = (TextView) convertView
				.findViewById(R.id.itemLocalTvDireccion);
		tvTelefono = (TextView) convertView
				.findViewById(R.id.itemLocalTvTelefono);
		tvLocalidad = (TextView) convertView
				.findViewById(R.id.itemLocalTvLocalidad);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			// Obtener un objeto a partir del layout (xml)
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(textViewResourceId, parent, false);
		}

		inicializar(convertView);
		Resources res = convertView.getResources();

		Local local = locales.get(position);

		tvNombre.setText(local.getNombre());
		tvTipoComida.setText(res.getString(R.string.mostrar_local_tipo_comida)
				+ local.getTipoComida());
		tvDireccion.setText(res.getString(R.string.mostrar_local_direccion)
				+ local.getDireccion());
		tvTelefono.setText(res.getString(R.string.mostrar_local_telefono)
				+ local.getTelefono());
		tvLocalidad.setText(local.getLocalidad() + "(" + local.getProvincia()
				+ ")");

		return convertView;
	}
}
