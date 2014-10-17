package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class TipoArticuloAdapter extends ArrayAdapter<TipoArticuloLocal> {
	private Context context;
	private int textViewResourceId;
	private List<TipoArticuloLocal> tiposArticulo;

	private TextView tvTipoArticulo, tvPrecio;
	private CheckBox cbPersonalizar;

	public TipoArticuloAdapter(Context context, int textViewResourceId,
			List<TipoArticuloLocal> tiposArticulo) {
		super(context, textViewResourceId, tiposArticulo);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.tiposArticulo = tiposArticulo;
	}

	private void inicializar(View convertView) {
		tvTipoArticulo = (TextView) convertView
				.findViewById(R.id.itemTipoArticuloTvTipoArticulo);

		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemTipoArticuloTvPrecio);
		// se pone clickable a false para que funcione el menu contextual
		tvTipoArticulo.setFocusable(false);
		tvPrecio.setFocusable(false);
		cbPersonalizar = (CheckBox) convertView
				.findViewById(R.id.itemTipoArticuloCbPersonalizar);
		cbPersonalizar.setFocusable(false);
		cbPersonalizar.setClickable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Resources res = context.getResources();

		TipoArticuloLocal tipoArticulo = tiposArticulo.get(position);
		
		tvPrecio.setText(Float.toString(tipoArticulo.getPrecio()));
		tvTipoArticulo.setText(tipoArticulo.getTipoArticulo());

		cbPersonalizar.setChecked(false);
		if (tipoArticulo.isPersonalizar()) {
			cbPersonalizar.setChecked(true);
		}

		return convertView;
	}
}
