package com.brymm.brymmapp.usuario;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.R.layout;
import com.brymm.brymmapp.R.menu;
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.usuario.adapters.PedidoAdapter;
import com.brymm.brymmapp.usuario.bbdd.GestionPedidoUsuario;
import com.brymm.brymmapp.usuario.pojo.Local;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class UltimosPedidosActivity extends ListActivity {

	OnItemClickListener oiclPedido = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adaptador, View view,
				int posicion, long id) {
			PedidoUsuario pedido = (PedidoUsuario) getListView().getAdapter()
					.getItem(posicion);
			
			irPedido(pedido);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_ultimos_pedidos);
		inicializar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usuario, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (MenuUsuario.gestionMenuUsuario(item.getItemId(), this)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void inicializar() {
		GestionPedidoUsuario gpu = new GestionPedidoUsuario(this);
		List<PedidoUsuario> pedidos = gpu.obtenerPedidosUsuario();
		gpu.cerrarDatabase();

		PedidoAdapter pedidoAdapter = new PedidoAdapter(this,
				R.layout.pedido_item, pedidos);

		getListView().setAdapter(pedidoAdapter);
		getListView().setOnItemClickListener(oiclPedido);
	}

	private void irPedido(PedidoUsuario pedido) {
		Intent intent = new Intent(this, PedidoActivity.class);
		intent.putExtra(PedidoActivity.EXTRA_PEDIDO, pedido);
		startActivity(intent);
	}

}
