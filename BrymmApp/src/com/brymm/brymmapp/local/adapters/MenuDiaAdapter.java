package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.MenuLocal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class MenuDiaAdapter extends ArrayAdapter<MenuDia> {
	private Context context;
	private int textViewResourceId;
	private List<MenuDia> menus;

	private TextView tvNombre, tvTipoMenu,tvPrecio;
	private RadioButton rbCarta,rbMenu;

	public MenuDiaAdapter(Context context, int textViewResourceId, List<MenuDia> menus) {
		super(context, textViewResourceId, menus);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.menus = menus;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView.findViewById(R.id.itemMenuTvNombre);
		tvTipoMenu = (TextView) convertView.findViewById(R.id.itemMenuTvTipoMenu);
		tvPrecio = (TextView) convertView.findViewById(R.id.itemMenuTvPrecio);
		
		rbCarta = (RadioButton) convertView.findViewById(R.id.itemMenuRbCarta);
		rbMenu = (RadioButton) convertView.findViewById(R.id.itemMenuRbMenu);
		rbCarta.setClickable(false);
		rbMenu.setClickable(false);

		tvNombre.setFocusable(false);
		tvTipoMenu.setFocusable(false);
		tvPrecio.setFocusable(false);
		rbMenu.setFocusable(false);
		rbCarta.setFocusable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		MenuDia menu = menus.get(position);

		tvNombre.setText(menu.getMenu().getNombre());
		tvTipoMenu.setText(menu.getMenu().getTipoMenu().getDescripcion());
		tvPrecio.setText(Float.toString(menu.getMenu().getPrecio()));
		
		
		if (menu.getMenu().isCarta()){
			rbCarta.setChecked(true);
			rbMenu.setChecked(false);
		}else{
			rbMenu.setChecked(true);
			rbCarta.setChecked(false);
		}

		return convertView;
	}
}
