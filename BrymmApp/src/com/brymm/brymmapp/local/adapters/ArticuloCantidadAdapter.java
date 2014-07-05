package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.Ingrediente;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArticuloCantidadAdapter extends ArrayAdapter<ArticuloCantidad> {
	private Context context;
	private int textViewResourceId;
	private List<ArticuloCantidad> articulos;

	private TextView tvNombre, tvPrecioUnidad, tvPrecioTotal,
			tvIngredientes, tvCantidad;

	public ArticuloCantidadAdapter(Context context, int textViewResourceId,
			List<ArticuloCantidad> articulos) {
		super(context, textViewResourceId, articulos);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.articulos = articulos;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemArticuloCantidadTvNombre);

		tvPrecioUnidad = (TextView) convertView
				.findViewById(R.id.itemArticuloCantidadTvPrecioUnidad);

		tvPrecioTotal = (TextView) convertView
				.findViewById(R.id.itemArticuloCantidadTvPrecioTotal);

		tvIngredientes = (TextView) convertView
				.findViewById(R.id.itemArticuloCantidadTvIngredientes);

		tvCantidad = (TextView) convertView
				.findViewById(R.id.itemArticuloCantidadTvCantidad);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Resources res = convertView.getResources();

		ArticuloCantidad articulo = articulos.get(position);

		tvNombre.setText(articulo.getNombre());
		tvPrecioUnidad.setText(res
				.getString(R.string.item_articulo_cantidad_precio_unidad)
				+ Float.toString(articulo.getPrecio()));
		tvPrecioTotal
				.setText(res
						.getString(R.string.item_articulo_cantidad_precio_total)
						+ Float.toString(articulo.getPrecio()
								* articulo.getCantidad()));

		StringBuilder stringIngredientes = new StringBuilder();
		for (Ingrediente ingrediente : articulo.getIngredientes()) {
			if (stringIngredientes.length() > 0) {
				stringIngredientes.append(", ");
			}
			stringIngredientes.append(ingrediente.getNombre());
		}

		// Si no tiene ingredientes no se oculta el textview
		if (articulo.getIngredientes().size() > 0) {
			tvIngredientes.setText(stringIngredientes);
		} else {
			tvIngredientes.setVisibility(View.GONE);
		}

		tvCantidad.setText(res
				.getString(R.string.item_articulo_cantidad_cantidad)
				+ Integer.toString(articulo.getCantidad()));

		return convertView;
	}
}
