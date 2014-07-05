package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.HorarioLocal;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HorarioLocalAdapter extends ArrayAdapter<HorarioLocal> {
	private Context context;
	private int textViewResourceId;
	private List<HorarioLocal> horariosLocal;

	private TextView tvDia, tvHoraInicio, tvHoraFin;

	public HorarioLocalAdapter(Context context, int textViewResourceId,
			List<HorarioLocal> horariosLocal) {
		super(context, textViewResourceId, horariosLocal);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.horariosLocal = horariosLocal;
	}

	private void inicializar(View convertView) {
		tvDia = (TextView) convertView
				.findViewById(R.id.itemHorarioLocalTvDia);
		tvHoraInicio = (TextView) convertView
				.findViewById(R.id.itemHorarioLocalTvHoraInicio);
		tvHoraFin = (TextView) convertView
				.findViewById(R.id.itemHorarioLocalTvHoraFin);
		
		tvDia.setFocusable(false);
		tvHoraInicio.setFocusable(false);
		tvHoraFin.setFocusable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Resources res = context.getResources();

		HorarioLocal horarioLocal = horariosLocal.get(position);

		tvDia.setText(horarioLocal.getDia().getDia());
		tvHoraInicio.setText(
				res.getString(R.string.item_horario_local_hora_inicio)
				+ " "
				+ horarioLocal.getHoraInicio());

		tvHoraFin.setText(res.getString(R.string.item_horario_local_hora_fin) + " "
				+ horarioLocal.getHoraFin());	

		return convertView;
	}
}
