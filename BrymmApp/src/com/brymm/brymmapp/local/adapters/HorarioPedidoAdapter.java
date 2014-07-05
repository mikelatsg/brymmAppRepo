package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.pojo.HorarioPedido;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HorarioPedidoAdapter extends ArrayAdapter<HorarioPedido> {
	private Context context;
	private int textViewResourceId;
	private List<HorarioPedido> horariosPedido;

	private TextView tvDia, tvHoraInicio, tvHoraFin;

	public HorarioPedidoAdapter(Context context, int textViewResourceId,
			List<HorarioPedido> horariosPedido) {
		super(context, textViewResourceId, horariosPedido);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.horariosPedido = horariosPedido;
	}

	private void inicializar(View convertView) {
		tvDia = (TextView) convertView
				.findViewById(R.id.itemHorarioPedidoTvDia);
		tvHoraInicio = (TextView) convertView
				.findViewById(R.id.itemHorarioPedidoTvHoraInicio);
		tvHoraFin = (TextView) convertView
				.findViewById(R.id.itemHorarioPedidoTvHoraFin);
		
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

		HorarioPedido horarioPedido = horariosPedido.get(position);

		tvDia.setText(horarioPedido.getDia().getDia());
		tvHoraInicio.setText(
				res.getString(R.string.item_horario_pedido_hora_inicio)
				+ " "
				+ horarioPedido.getHoraInicio());

		tvHoraFin.setText(res.getString(R.string.item_horario_pedido_hora_fin) + " "
				+ horarioPedido.getHoraFin());	

		return convertView;
	}
}
