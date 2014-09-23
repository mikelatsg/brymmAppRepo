package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.Comanda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ComandaAdapter extends ArrayAdapter<Comanda> {
	private Context context;
	private int textViewResourceId;
	private List<Comanda> comandas;

	private TextView tvIdComanda, tvCamarero, tvPrecio, tvEstado,
			tvObservaciones, tvMesa;

	public ComandaAdapter(Context context, int textViewResourceId,
			List<Comanda> comandas) {
		super(context, textViewResourceId, comandas);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.comandas = comandas;
	}

	private void inicializar(View convertView) {
		tvIdComanda = (TextView) convertView
				.findViewById(R.id.itemComandaTvIdComanda);

		tvCamarero = (TextView) convertView
				.findViewById(R.id.itemComandaTvCamarero);

		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemComandaTvPrecio);

		tvEstado = (TextView) convertView
				.findViewById(R.id.itemComandaTvEstado);

		tvObservaciones = (TextView) convertView
				.findViewById(R.id.itemComandaTvObservaciones);

		tvMesa = (TextView) convertView.findViewById(R.id.itemComandaTvMesa);

		// se pone focusable a false para que funcione el menu contextual
		tvIdComanda.setFocusable(false);
		tvPrecio.setFocusable(false);
		tvCamarero.setFocusable(false);
		tvEstado.setFocusable(false);
		tvObservaciones.setFocusable(false);
		tvMesa.setFocusable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Comanda comanda = comandas.get(position);

		tvIdComanda.setText(Integer.toString(comanda.getIdComanda()));
		tvPrecio.setText(Float.toString(comanda.getPrecio()));
		tvCamarero.setText(comanda.getCamarero().getNombre());
		tvEstado.setText(comanda.getEstado());

		// Si no hay mesa se oculta el campo
		if (comanda.getMesa() != null) {
			tvMesa.setText(comanda.getMesa().getNombre());
		} else {
			tvMesa.setVisibility(View.GONE);
		}

		return convertView;
	}
}
