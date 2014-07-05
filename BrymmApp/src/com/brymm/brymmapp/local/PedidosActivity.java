package com.brymm.brymmapp.local;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.fragments.ListaPedidosFragment;
import com.brymm.brymmapp.menu.MenuLocal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PedidosActivity extends FragmentActivity {

	private ListView navList;
	private DrawerLayout mDrawerLayout;
	private List<String> estadosPedido;
	private List<String> idEstadosPedido;
	private String estadoPedido = GestionPedido.ESTADO_PENDIENTE;

	private OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adaptador, View view,
				int posicion, long arg3) {
			cambiarFragment(posicion);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos);
		inicializar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.local, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (MenuLocal.gestionMenu(item.getItemId(), this)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			ListaPedidosFragment listaFragment = (ListaPedidosFragment) getSupportFragmentManager()
					.findFragmentById(R.id.listaPedidosFl);
			listaFragment.actualizarLista(estadoPedido);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void inicializar() {

		mDrawerLayout = (DrawerLayout) findViewById(R.id.pedidosDl);
		navList = (ListView) findViewById(R.id.pedidosLvNavegador);

		// Se obtienen los estado de los pedidos para añadirlos al nav
		String[] estadoPedidoArray = getResources().getStringArray(
				R.array.estadoPedidoArray);
		estadosPedido = new ArrayList<String>();
		idEstadosPedido = new ArrayList<String>();
		for (int i = 0; i < estadoPedidoArray.length; i++) {
			String[] stringEstado = estadoPedidoArray[i].split("#");
			estadosPedido.add(stringEstado[0]);
			idEstadosPedido.add(stringEstado[1]);
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, estadosPedido);

		navList.setAdapter(adaptador);
		navList.setOnItemClickListener(oicl);

		// FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment fragment = new ListaPedidosFragment();

		// En el inicio se pasa el estado pendiente
		Bundle args = new Bundle();
		args.putString(ListaPedidosFragment.EXTRA_ID_ESTADO,
				this.idEstadosPedido.get(0));
		fragment.setArguments(args);		

		/*
		 * fragmentManager.beginTransaction() .replace(R.id.listaPedidosFl,
		 * fragment).commit();
		 */

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// ft.replace(R.id.listaPedidosFl, fragment);
		ft.add(R.id.listaPedidosFl, fragment);

		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		navList.setItemChecked(0, true);

	}

	private void cambiarFragment(int posicion) {

		Fragment fragment = null;
		fragment = new ListaPedidosFragment();

		FragmentManager fragmentManager = getSupportFragmentManager();
		// Se pasa el estado de los pedidos a mostrar
		Bundle args = new Bundle();
		args.putString(ListaPedidosFragment.EXTRA_ID_ESTADO,
				this.idEstadosPedido.get(posicion));
		fragment.setArguments(args);
		
		//Se guarda el estado
		this.estadoPedido = this.idEstadosPedido.get(posicion);

		fragmentManager.beginTransaction()
				.replace(R.id.listaPedidosFl, fragment).commit();

		ListaPedidosFragment listaFragment = (ListaPedidosFragment) getSupportFragmentManager()
				.findFragmentById(R.id.listaPedidosFl);

		listaFragment.ocultarDetalle();

		navList.setItemChecked(posicion, true);

		mDrawerLayout.closeDrawer(navList);

	}

}
