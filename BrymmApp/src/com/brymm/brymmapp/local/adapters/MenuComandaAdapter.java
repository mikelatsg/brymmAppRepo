package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.MenuComanda;
import com.brymm.brymmapp.local.pojo.PlatoComanda;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MenuComandaAdapter extends ArrayAdapter<MenuComanda> {
	private Context context;
	private int textViewResourceId;
	private List<MenuComanda> menus;

	private TextView tvNombre, tvPrecio, tvCantidad;

	public MenuComandaAdapter(Context context, int textViewResourceId,
			List<MenuComanda> menus) {
		super(context, textViewResourceId, menus);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.menus = menus;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemMenuComandaTvNombre);

		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemMenuComandaTvPrecio);

		tvCantidad = (TextView) convertView
				.findViewById(R.id.itemMenuComandaTvCantidad);

		// se pone focusable a false para que funcione el menu contextual
		tvNombre.setFocusable(false);
		tvPrecio.setFocusable(false);
		tvCantidad.setFocusable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Resources res = convertView.getResources();

		MenuComanda menuComanda = menus.get(position);

		tvNombre.setText(menuComanda.getMenu().getNombre());
		tvPrecio.setText(res.getString(R.string.item_articulo_precio)
				+ Float.toString((menuComanda.getMenu().getPrecio())));
		tvCantidad.setText(Integer.toString(menuComanda.getCantidad()));

		// Generar dinamicamente los platos.
		for(PlatoComanda platoComanda: menuComanda.getPlatos()){
			
		}

		return convertView;
	}
}
