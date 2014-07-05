package com.brymm.brymmapp.usuario.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PedidoAdapter extends ArrayAdapter<PedidoUsuario> {
	private Context context;
	private int textViewResourceId;
	private List<PedidoUsuario> pedidos;

	private TextView tvNombreLocal, tvPedido, tvPrecio, tvEstado,
			tvFechaPedido, tvFechaEntrega, tvDireccionEnvio;

	public PedidoAdapter(Context context, int textViewResourceId,
			List<PedidoUsuario> pedidos) {
		super(context, textViewResourceId, pedidos);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.pedidos = pedidos;
	}

	private void inicializar(View convertView) {
		tvNombreLocal = (TextView) convertView
				.findViewById(R.id.itemPedidoTvNombreLocal);
		tvPedido = (TextView) convertView
				.findViewById(R.id.itemPedidoTvIdPedido);
		tvPrecio = (TextView) convertView.findViewById(R.id.itemPedidoTvPrecio);
		tvEstado = (TextView) convertView.findViewById(R.id.itemPedidoTvEstado);
		tvFechaPedido = (TextView) convertView
				.findViewById(R.id.itemPedidoTvFechaPedido);
		tvFechaEntrega = (TextView) convertView
				.findViewById(R.id.itemPedidoTvFechaEntrega);
		tvDireccionEnvio = (TextView) convertView
				.findViewById(R.id.itemPedidoTvDireccionEnvio);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		PedidoUsuario pedido = pedidos.get(position);

		// Obtener un objeto a partir del layout (xml)
		view = li.inflate(textViewResourceId, parent, false);

		inicializar(view);
		Resources res = view.getResources();

		tvNombreLocal.setText(res.getString(R.string.item_pedido_local)
				+ pedido.getNombreLocal());
		tvPedido.setText(res.getString(R.string.item_pedido_id_pedido)
				+ Integer.toString(pedido.getIdPedido()));

		tvPrecio.setText(res.getString(R.string.item_pedido_precio)
				+ Float.toString(pedido.getPrecio()));
		tvEstado.setText(res.getString(R.string.item_pedido_estado)
				+ pedido.getEstado());
		tvFechaPedido.setText(res.getString(R.string.item_pedido_fecha_pedido)
				+ pedido.getFechaPedido());

		if (!pedido.getFechaEntrega().equals("null")) {
			tvFechaEntrega.setText(res
					.getString(R.string.item_pedido_fecha_entrega)
					+ pedido.getFechaEntrega());
		} else {
			tvFechaEntrega.setVisibility(View.GONE);
		}

		if (pedido.getDireccionEnvio() != null) {
			tvDireccionEnvio.setText(res
					.getString(R.string.item_pedido_direccion)
					+ pedido.getDireccionEnvio().getDireccion());
		} else {
			tvDireccionEnvio.setVisibility(View.GONE);
		}

		return view;
	}
}
