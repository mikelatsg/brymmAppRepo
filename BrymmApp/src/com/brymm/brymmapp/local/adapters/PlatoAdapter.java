package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.Plato;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlatoAdapter extends ArrayAdapter<Plato> {
	private Context context;
	private int textViewResourceId;
	private List<Plato> platos;

	private TextView tvNombre, tvTipoPlato,tvPrecio;

	public PlatoAdapter(Context context, int textViewResourceId, List<Plato> platos) {
		super(context, textViewResourceId, platos);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.platos = platos;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView.findViewById(R.id.itemPlatoTvNombre);
		tvTipoPlato = (TextView) convertView.findViewById(R.id.itemPlatoTvTipoPlato);
		tvPrecio = (TextView) convertView.findViewById(R.id.itemPlatoTvPrecio);

		tvNombre.setFocusable(false);
		tvTipoPlato.setFocusable(false);
		tvPrecio.setFocusable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Plato plato = platos.get(position);

		tvNombre.setText(plato.getNombre());
		tvTipoPlato.setText(plato.getTipoPlato().getDescripcion());
		tvPrecio.setText(Float.toString(plato.getPrecio()));

		return convertView;
	}
}
