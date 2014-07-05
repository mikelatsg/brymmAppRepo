package com.brymm.brymmapp.usuario.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.interfaces.ListaArticulosPedido;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalCantidad;
import com.brymm.brymmapp.usuario.pojo.ArticuloPersonalizado;
import com.brymm.brymmapp.usuario.pojo.IngredienteLocal;
import com.brymm.brymmapp.usuario.pojo.Local;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArticuloLocalAdapter extends ArrayAdapter<ListaArticulosPedido> {
	private Context context;
	private int textViewResourceId;
	private List<ListaArticulosPedido> articulos;

	private TextView tvNombre, tvTipoArticulo, tvIngredientes, tvCantidad,
			tvPrecio;

	public ArticuloLocalAdapter(Context context, int textViewResourceId,
			List<ListaArticulosPedido> articulos) {
		super(context, textViewResourceId, articulos);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.articulos = articulos;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemArticuloPedidoTvNombre);
		tvTipoArticulo = (TextView) convertView
				.findViewById(R.id.itemArticuloPedidoTvTipoArticulo);
		tvIngredientes = (TextView) convertView
				.findViewById(R.id.itemArticuloPedidoTvIngredientes);
		tvCantidad = (TextView) convertView
				.findViewById(R.id.itemArticuloPedidoTvCantidad);
		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemArticuloPedidoTvPrecio);
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

		ListaArticulosPedido articulo = articulos.get(position);

		if (articulo.isPersonalizado()) {
			ArticuloPersonalizado personalizado = (ArticuloPersonalizado) articulo;
			tvNombre.setText(personalizado.getNombre());
			tvTipoArticulo.setText(personalizado.getTipoArticulo().getNombre());
			String stringIngredientes = "";
			float precio = personalizado.getTipoArticulo().getPrecio();
			for (int i = 0; i < personalizado.getIngredientes().size(); i++) {
				stringIngredientes += personalizado.getIngredientes().get(i)
						.getNombre();
				precio += personalizado.getIngredientes().get(i).getPrecio();
				if (i < personalizado.getIngredientes().size() - 1) {
					stringIngredientes += ",";
				}
			}
			tvIngredientes.setText(stringIngredientes);
			tvCantidad.setText(Integer.toString(personalizado.getCantidad()));
			tvPrecio.setText(Float.toString(personalizado.getCantidad()
					* precio));
		} else {
			ArticuloLocalCantidad alc = (ArticuloLocalCantidad) articulo;
			tvNombre.setText(alc.getNombre());
			tvTipoArticulo.setText(alc.getTipoArticulo());
			String stringIngredientes = "";
			for (int i = 0; i < alc.getIngredientes().size(); i++) {
				stringIngredientes += alc.getIngredientes().get(i);

				if (i < alc.getIngredientes().size() - 1) {
					stringIngredientes += ",";
				}
			}
			if (stringIngredientes.equals("")) {
				tvIngredientes.setVisibility(View.GONE);
			} else {
				tvIngredientes.setText(stringIngredientes);
			}
			tvCantidad.setText(Integer.toString(alc.getCantidad()));
			tvPrecio.setText(Float.toString(alc.getCantidad() * alc.getPrecio()));
		}

		return convertView;
	}
}
