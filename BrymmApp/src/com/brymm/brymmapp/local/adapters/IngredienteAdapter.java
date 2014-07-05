package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.Ingrediente;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IngredienteAdapter extends ArrayAdapter<Ingrediente> {
	private Context context;
	private int textViewResourceId;
	private List<Ingrediente> ingredientes;

	private TextView tvNombre, tvDescripcion, tvPrecio;

	public IngredienteAdapter(Context context, int textViewResourceId,
			List<Ingrediente> ingredientes) {
		super(context, textViewResourceId, ingredientes);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.ingredientes = ingredientes;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemIngredienteNombre);
		tvDescripcion = (TextView) convertView
				.findViewById(R.id.itemIngredienteDescripcion);
		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemIngredientePrecio);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Resources res = context.getResources();

		Ingrediente ingrediente = ingredientes.get(position);

		tvNombre.setText(ingrediente.getNombre());
		if (ingrediente.getDescripcion().length() > 2) {
			tvDescripcion.setText(res
					.getString(R.string.item_ingrediente_descripcion)
					+ " "
					+ ingrediente.getDescripcion());
		} else {
			tvDescripcion.setVisibility(View.GONE);
		}
		tvPrecio.setText(res.getString(R.string.item_ingrediente_precio) + " "
				+ Float.toString(ingrediente.getPrecio()));

		return convertView;
	}
}
