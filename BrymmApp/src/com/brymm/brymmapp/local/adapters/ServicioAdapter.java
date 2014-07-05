package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.ServicioLocal;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ServicioAdapter extends ArrayAdapter<ServicioLocal> {
	private Context context;
	private int textViewResourceId;
	private List<ServicioLocal> servicios;

	private TextView tvNombre, tvImporteMinimo, tvPrecio;
	private CheckBox cbActivo;

	public ServicioAdapter(Context context, int textViewResourceId,
			List<ServicioLocal> servicios) {
		super(context, textViewResourceId, servicios);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.servicios = servicios;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemServicioTvNombre);
		tvImporteMinimo = (TextView) convertView
				.findViewById(R.id.itemServicioTvImporteMinimo);
		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemServicioTvPrecio);

		cbActivo = (CheckBox) convertView
				.findViewById(R.id.itemServicioCbActivo);
		
		tvNombre.setFocusable(false);
		tvImporteMinimo.setFocusable(false);
		tvPrecio.setFocusable(false);
		cbActivo.setFocusable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Resources res = context.getResources();

		ServicioLocal servicio = servicios.get(position);

		tvNombre.setText(servicio.getTipoServicio().getServicio());
		tvImporteMinimo.setText(
				res.getString(R.string.item_servicio_importe_minimo)
				+ " "
				+ Float.toString(servicio.getImporteMinimo()));

		tvPrecio.setText(res.getString(R.string.item_servicio_precio) + " "
				+ Float.toString(servicio.getPrecio()));
		
		cbActivo.setClickable(false);
		cbActivo.setChecked(false);
		if (servicio.isActivo()){
			cbActivo.setChecked(true);
		}

		return convertView;
	}
}
