package com.brymm.brymmapp.usuario;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.R.layout;
import com.brymm.brymmapp.R.menu;
import com.brymm.brymmapp.usuario.adapters.ArticuloAdapter;
import com.brymm.brymmapp.usuario.adapters.ArticuloLocalAdapter;
import com.brymm.brymmapp.usuario.adapters.ArticuloPedidoAdapter;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalLista;
import com.brymm.brymmapp.usuario.pojo.ArticuloPedido;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class PedidoActivity extends Activity {

	public static final String EXTRA_PEDIDO = "extraPedido";

	private TextView tvNombreLocal, tvPedido, tvPrecio, tvEstado,
			tvFechaPedido, tvFechaEntrega, tvDireccionEnvio, tvMotivo,
			tvObservaciones;

	private ListView lvArticulos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedido);

		inicializar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usuario, menu);
		return true;
	}

	private void inicializar() {
		tvNombreLocal = (TextView) findViewById(R.id.pedidoTvNombreLocal);
		tvPedido = (TextView) findViewById(R.id.pedidoTvIdPedido);
		tvPrecio = (TextView) findViewById(R.id.pedidoTvPrecio);
		tvEstado = (TextView) findViewById(R.id.pedidoTvEstado);
		tvFechaPedido = (TextView) findViewById(R.id.pedidoTvFechaPedido);
		tvFechaEntrega = (TextView) findViewById(R.id.pedidoTvFechaEntrega);
		tvDireccionEnvio = (TextView) findViewById(R.id.pedidoTvDireccionEnvio);
		tvMotivo = (TextView) findViewById(R.id.pedidoTvMotivoRechazo);
		tvObservaciones = (TextView) findViewById(R.id.pedidoTvObservaciones);
		lvArticulos = (ListView) findViewById(R.id.pedidoLvArticulos);

		PedidoUsuario pedido = getIntent().getParcelableExtra(EXTRA_PEDIDO);
		Resources res = getResources();

		tvNombreLocal.setText(res.getString(R.string.pedido_local)
				+ pedido.getNombreLocal());
		tvPedido.setText(res.getString(R.string.pedido_id_pedido)
				+ Integer.toString(pedido.getIdPedido()));

		tvPrecio.setText(res.getString(R.string.pedido_precio)
				+ Float.toString(pedido.getPrecio()));
		tvEstado.setText(res.getString(R.string.pedido_estado)
				+ pedido.getEstado());
		tvFechaPedido.setText(res.getString(R.string.pedido_fecha_pedido)
				+ pedido.getFechaPedido());

		if (!pedido.getFechaEntrega().equals("null")) {
			tvFechaEntrega.setText(res.getString(R.string.pedido_fecha_entrega)
					+ pedido.getFechaEntrega());
		} else {
			tvFechaEntrega.setVisibility(View.GONE);
		}

		if (pedido.getDireccionEnvio() != null) {
			tvDireccionEnvio.setText(res.getString(R.string.pedido_direccion)
					+ pedido.getDireccionEnvio().getDireccion());
		} else {
			tvDireccionEnvio.setVisibility(View.GONE);
		}

		if (!pedido.getObservaciones().equals("")) {
			tvObservaciones.setText(res
					.getString(R.string.pedido_observaciones)
					+ pedido.getObservaciones());
		} else {
			tvObservaciones.setVisibility(View.GONE);
		}

		if (!pedido.getMotivoRechazo().equals("null")) {
			tvMotivo.setText(res.getString(R.string.pedido_motivo_rechazo)
					+ pedido.getMotivoRechazo());
		} else {
			tvMotivo.setVisibility(View.GONE);
		}

		ArticuloPedidoAdapter articulosAdapter = new ArticuloPedidoAdapter(
				this, R.layout.articulo_pedido_item, pedido.getArticulos());
		
		lvArticulos.setAdapter(articulosAdapter);

	}
}
