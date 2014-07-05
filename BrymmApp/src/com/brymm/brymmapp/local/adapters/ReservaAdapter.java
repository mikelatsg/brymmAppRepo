package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.Reserva;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReservaAdapter extends ArrayAdapter<Reserva> {
	private Context context;
	private int textViewResourceId;
	private List<Reserva> reservas;

	private TextView tvFecha, tvEstado, tvHoraInicio, tvIdReserva,
			tvObservaciones, tvNumeroPersonas, tvTipoMenu, tvUsuario, tvMotivo;

	public ReservaAdapter(Context context, int textViewResourceId,
			List<Reserva> reservas) {
		super(context, textViewResourceId, reservas);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.reservas = reservas;
	}

	private void inicializar(View convertView) {
		tvFecha = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvFecha);

		tvEstado = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvEstado);

		tvHoraInicio = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvHoraInicio);

		tvIdReserva = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvIdReserva);

		tvObservaciones = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvObservaciones);

		tvNumeroPersonas = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvNumeroPersonas);

		tvTipoMenu = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvTipoMenu);

		tvUsuario = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvUsuario);

		tvMotivo = (TextView) convertView
				.findViewById(R.id.itemReservaLocalTvMotivo);

		// se pone focusable a false para que funcione el menu contextual
		tvFecha.setFocusable(false);
		tvEstado.setFocusable(false);
		tvHoraInicio.setFocusable(false);
		tvIdReserva.setFocusable(false);
		tvObservaciones.setFocusable(false);
		tvNumeroPersonas.setFocusable(false);
		tvTipoMenu.setFocusable(false);
		tvUsuario.setFocusable(false);
		tvMotivo.setFocusable(false);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Reserva reserva = reservas.get(position);

		tvIdReserva.setText(Integer.toString(reserva.getIdReserva()));
		tvFecha.setText(reserva.getFecha());
		tvHoraInicio.setText(reserva.getHoraInicio());
		tvEstado.setText(reserva.getEstado());
		tvNumeroPersonas.setText(Integer.toString(reserva.getNumeroPersonas()));
		tvTipoMenu.setText(reserva.getTipoMenu().getDescripcion());

		if (reserva.getUsuario() == null) {
			tvUsuario.setText(reserva.getNombreEmisor());
		} else {
			tvUsuario.setText(reserva.getUsuario().getNombre()
					+ " " + reserva.getUsuario().getApellido());
		}

		// Si el campo no esta informado no se muestra
		if (reserva.getObservaciones().toLowerCase().equals("null")
				|| reserva.getObservaciones().toLowerCase().equals("")) {
			tvObservaciones.setVisibility(View.GONE);
		} else {
			tvObservaciones.setText(reserva.getObservaciones());
		}

		if (reserva.getMotivo().toLowerCase().equals("null")
				|| reserva.getMotivo().toLowerCase().equals("")) {
			tvMotivo.setVisibility(View.GONE);
		} else {
			tvMotivo.setText(reserva.getMotivo());
		}

		return convertView;
	}
}
