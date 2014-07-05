package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.Mesa;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MesaAdapter extends ArrayAdapter<Mesa> {
	private Context context;
	private int textViewResourceId;
	private List<Mesa> mesas;

	private TextView tvNombre, tvCapacidad;

	public MesaAdapter(Context context, int textViewResourceId, List<Mesa> mesas) {
		super(context, textViewResourceId, mesas);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.mesas = mesas;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView.findViewById(R.id.itemMesaTvNombre);
		tvCapacidad = (TextView) convertView
				.findViewById(R.id.itemMesaTvCapacidad);

		tvNombre.setFocusable(false);
		tvCapacidad.setFocusable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Resources res = context.getResources();

		Mesa mesa = mesas.get(position);

		tvNombre.setText(mesa.getNombre());
		tvCapacidad.setText(Integer.toString(mesa.getCapacidad()));

		return convertView;
	}
}
