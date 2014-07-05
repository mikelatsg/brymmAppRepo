package com.brymm.brymmapp.usuario.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.ReservaUsuario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReservaAdapter extends ArrayAdapter<ReservaUsuario> {
	private Context context;
	private int textViewResourceId;
	private List<ReservaUsuario> reservas;

	private TextView tvNombreLocal, tvFecha, tvTipoMenu, tvNumeroPersonas,
			tvEstado, tvObservaciones, tvMotivo;

	public ReservaAdapter(Context context, int textViewResourceId,
			List<ReservaUsuario> reservas) {
		super(context, textViewResourceId, reservas);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.reservas = reservas;
	}

	private void inicializar(View convertView) {
		tvNombreLocal = (TextView) convertView
				.findViewById(R.id.itemReservaTvNombre);
		tvFecha = (TextView) convertView.findViewById(R.id.itemReservaTvFecha);
		tvTipoMenu = (TextView) convertView
				.findViewById(R.id.itemReservaTvTipoMenu);
		tvNumeroPersonas = (TextView) convertView
				.findViewById(R.id.itemReservaTvNumeroPersonas);
		tvEstado = (TextView) convertView
				.findViewById(R.id.itemReservaTvEstado);
		tvObservaciones = (TextView) convertView
				.findViewById(R.id.itemReservaTvObservaciones);
		tvMotivo = (TextView) convertView
				.findViewById(R.id.itemReservaTvMotivo);
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

		ReservaUsuario reserva = reservas.get(position);

		tvNombreLocal.setText(reserva.getNombreLocal());
		tvFecha.setText(reserva.getFecha() + " " + reserva.getHora());
		tvTipoMenu.setText(reserva.getTipoMenu());
		tvNumeroPersonas.setText(Integer.toString(reserva.getNumeroPersonas()));
		tvEstado.setText(reserva.getEstado());
		if (reserva.getObservaciones().equals("") || reserva.getObservaciones().equals("null")) {
			tvObservaciones.setVisibility(View.GONE);
		} else {
			tvObservaciones.setText(reserva.getObservaciones());
		}
		if (reserva.getMotivo().equals("") || reserva.getMotivo().equals("null")) {
			tvMotivo.setVisibility(View.GONE);
		} else {
			tvMotivo.setText(reserva.getMotivo());
		}

		return convertView;
	}
}
