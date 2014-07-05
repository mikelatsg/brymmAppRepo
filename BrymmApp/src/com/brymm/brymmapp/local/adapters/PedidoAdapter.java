package com.brymm.brymmapp.local.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.pojo.Pedido;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PedidoAdapter extends ArrayAdapter<Pedido> {
	private Context context;
	private int textViewResourceId;
	private List<Pedido> pedidos;

	private TextView tvIdPedido, tvUsuario, tvPrecio, tvEstado, tvDireccion,
			tvFechaPedido, tvFechaEntrega;

	public PedidoAdapter(Context context, int textViewResourceId,
			List<Pedido> pedidos) {
		super(context, textViewResourceId, pedidos);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.pedidos = pedidos;
	}

	private void inicializar(View convertView) {
		tvIdPedido = (TextView) convertView
				.findViewById(R.id.itemPedidoLocalTvIdPedido);

		tvUsuario = (TextView) convertView
				.findViewById(R.id.itemPedidoLocalTvUsuario);

		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemPedidoLocalTvPrecio);

		tvEstado = (TextView) convertView
				.findViewById(R.id.itemPedidoLocalTvEstado);

		tvDireccion = (TextView) convertView
				.findViewById(R.id.itemPedidoLocalTvDireccionEnvio);

		tvFechaEntrega = (TextView) convertView
				.findViewById(R.id.itemPedidoLocalTvFechaEntrega);

		tvFechaPedido = (TextView) convertView
				.findViewById(R.id.itemPedidoLocalTvFechaPedido);

		// se pone focusable a false para que funcione el menu contextual
		tvIdPedido.setFocusable(false);
		tvPrecio.setFocusable(false);
		tvUsuario.setFocusable(false);
		tvEstado.setFocusable(false);
		tvDireccion.setFocusable(false);
		tvFechaEntrega.setFocusable(false);
		tvFechaPedido.setFocusable(false);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = li.inflate(textViewResourceId, parent, false);

		inicializar(convertView);

		Pedido pedido = pedidos.get(position);

		tvIdPedido.setText(Integer.toString(pedido.getIdPedido()));
		tvPrecio.setText(Float.toString(pedido.getPrecio()));
		tvUsuario.setText(pedido.getUsuario().getNick());
		tvEstado.setText(pedido.getEstado());		
		
		// Si no hay direccion se oculta el campo
		if (pedido.getDireccion() != null) {
			tvDireccion.setText(pedido.getDireccion().getDireccion());
		} else {
			tvDireccion.setVisibility(View.GONE);
		}
		
		/*
		 * Si el pedido esta en estado pendiente se oculta la fecha de entrega
		 * que sera null
		 */
		
		if (pedido.getEstado().equals(GestionPedido.ESTADO_PENDIENTE)){
			tvFechaEntrega.setVisibility(View.GONE);
		}else{
			tvFechaEntrega.setText(pedido.getFechaEntrega());
		}
		
		tvFechaPedido.setText(pedido.getFecha());

		return convertView;
	}
}
