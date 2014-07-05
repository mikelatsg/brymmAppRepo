package com.brymm.brymmapp.usuario.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.interfaces.ListaArticulosPedido;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalCantidad;
import com.brymm.brymmapp.usuario.pojo.ArticuloPedido;
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

public class ArticuloPedidoAdapter extends ArrayAdapter<ArticuloPedido> {
	private Context context;
	private int textViewResourceId;
	private List<ArticuloPedido> articulos;

	private TextView tvNombre, tvTipoArticulo, tvIngredientes, tvCantidad,
			tvPrecio;

	public ArticuloPedidoAdapter(Context context, int textViewResourceId,
			List<ArticuloPedido> articulos) {
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

		ArticuloPedido articulo = articulos.get(position);

		tvNombre.setText(articulo.getNombre());
		tvTipoArticulo.setText(articulo.getTipoArticulo());
		String stringIngredientes = "";
		for (int i = 0; i < articulo.getIngredientes().size(); i++) {
			stringIngredientes += articulo.getIngredientes().get(i);

			if (i < articulo.getIngredientes().size() - 1) {
				stringIngredientes += ",";
			}
		}
		if (stringIngredientes.equals("")) {
			tvIngredientes.setVisibility(View.GONE);
		} else {
			tvIngredientes.setText(stringIngredientes);
		}

		tvCantidad.setText(Integer.toString(articulo.getCantidad()));
		tvPrecio.setText(Float.toString(articulo.getCantidad()
				* articulo.getPrecio()));

		return convertView;
	}
}
