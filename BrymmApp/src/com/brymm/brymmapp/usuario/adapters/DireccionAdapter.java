package com.brymm.brymmapp.usuario.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.pojo.Direccion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DireccionAdapter extends ArrayAdapter<Direccion> {
	private Context context;
	private int textViewResourceId;
	private List<Direccion> direcciones;

	private TextView tvNombre, tvDireccion, tvPoblacion, tvProvincia;

	public DireccionAdapter(Context context, int textViewResourceId,
			List<Direccion> direcciones) {
		super(context, textViewResourceId, direcciones);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.direcciones = direcciones;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemDireccionTvNombre);
		tvDireccion = (TextView) convertView
				.findViewById(R.id.itemDireccionTvDireccion);
		tvPoblacion = (TextView) convertView
				.findViewById(R.id.itemDireccionTvPoblacion);
		tvProvincia = (TextView) convertView
				.findViewById(R.id.itemDireccionTvProvincia);
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

		Direccion direccion = direcciones.get(position);

		tvNombre.setText(direccion.getNombre());
		tvDireccion.setText(direccion.getDireccion());
		tvPoblacion.setText(direccion.getPoblacion());
		tvProvincia.setText(direccion.getProvincia());

		return convertView;
	}
}
