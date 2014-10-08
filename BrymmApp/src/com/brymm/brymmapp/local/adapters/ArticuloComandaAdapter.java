package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.Ingrediente;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ArticuloComandaAdapter extends ArrayAdapter<Articulo> {
	private Context context;
	private int textViewResourceId;
	private List<Articulo> articulos;

	private TextView tvNombre, tvPrecio, tvDescripcion, tvIngredientes;

	public ArticuloComandaAdapter(Context context, int textViewResourceId,
			List<Articulo> articulos) {
		super(context, textViewResourceId, articulos);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.articulos = articulos;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemArticuloTvNombre);

		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemArticuloTvPrecio);

		tvDescripcion = (TextView) convertView
				.findViewById(R.id.itemArticuloTvDescripcion);

		tvIngredientes = (TextView) convertView
				.findViewById(R.id.itemArticuloTvIngredientes);

		// se pone focusable a false para que funcione el menu contextual
		tvNombre.setFocusable(false);
		tvPrecio.setFocusable(false);
		tvDescripcion.setFocusable(false);
		tvIngredientes.setFocusable(false);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Resources res = convertView.getResources();

		Articulo articulo = articulos.get(position);

		tvNombre.setText(articulo.getNombre());
		tvPrecio.setText(res.getString(R.string.item_articulo_precio)
				+ Float.toString(articulo.getPrecio()));
		tvDescripcion.setText(res.getString(R.string.item_articulo_descripcion)
				+ articulo.getDescripcion());

		String stringIngredientes = "";
		for (Ingrediente ingrediente : articulo.getIngredientes()) {
			if (stringIngredientes.length() > 0) {
				stringIngredientes += ", ";
			}
			stringIngredientes += ingrediente.getNombre();
		}
		
		//Si no tiene ingredientes no se oculta el textview
		if (articulo.getIngredientes().size() > 0) {
			tvIngredientes.setText(stringIngredientes);
		} else {
			tvIngredientes.setVisibility(View.GONE);
		}

		return convertView;
	}
}
