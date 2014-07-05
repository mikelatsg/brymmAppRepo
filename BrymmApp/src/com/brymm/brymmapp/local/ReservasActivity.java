package com.brymm.brymmapp.local;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.bbdd.GestionReserva;
import com.brymm.brymmapp.local.fragments.CalendarioReservasFragment;
import com.brymm.brymmapp.local.fragments.ListaHorariosLocalFragment;
import com.brymm.brymmapp.local.fragments.ListaMesasFragment;
import com.brymm.brymmapp.local.fragments.ListaReservasFragment;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.menu.MenuLocal;

import android.os.Bundle;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ReservasActivity extends FragmentActivity {

	public static final int REQUEST_RESERVA = 1;
	public static final int REQUEST_ANADIR_MESA = 2;
	public static final int REQUEST_ANADIR_RESERVA = 3;
	private static final int CODE_RESERVAS_PENDIENTES = 0;
	private static final int CODE_RESERVAS_ACEPTADAS = 1;
	private static final int CODE_RESERVAS_RECHAZADAS = 2;
	private static final int CODE_MESA = 3;
	private static final int CODE_CALENDARIO = 4;
	private String estadoPedido = GestionReserva.ESTADO_PENDIENTE;

	private ListView navList;
	private DrawerLayout mDrawerLayout;
	private List<String> reservas;

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
		setContentView(R.layout.activity_reservas);
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

		if (requestCode == REQUEST_ANADIR_MESA) {
			if (resultCode == RESULT_OK) {
				Lista listaFragment = (Lista) getSupportFragmentManager()
						.findFragmentById(R.id.listaReservasFl);
				listaFragment.actualizarLista();
			}
		} else {
			if (resultCode == RESULT_OK) {
				ListaEstado listaFragment = (ListaEstado) getSupportFragmentManager()
						.findFragmentById(R.id.listaReservasFl);
				listaFragment.actualizarLista(estadoPedido);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void inicializar() {

		mDrawerLayout = (DrawerLayout) findViewById(R.id.reservasDl);
		navList = (ListView) findViewById(R.id.reservasLvNavegador);

		// Se obtienen las reservas para añadirlos al nav
		String[] reservasArray = getResources().getStringArray(
				R.array.reservasArray);
		reservas = new ArrayList<String>();
		for (int i = 0; i < reservasArray.length; i++) {
			String[] stringEstado = reservasArray[i].split("#");
			reservas.add(stringEstado[0]);
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, reservas);

		navList.setAdapter(adaptador);
		navList.setOnItemClickListener(oicl);

		Fragment fragment = new ListaMesasFragment();

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.listaReservasFl, fragment);

		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		navList.setItemChecked(0, true);

	}

	private void cambiarFragment(int posicion) {

		Fragment fragment = null;
		Bundle bundle = null;
		
		// Se obtienen el frameLayout del detalle
		FrameLayout flDetalle = (FrameLayout) findViewById(R.id.detalleReservaFl);
		flDetalle.setVisibility(View.VISIBLE);

		// Se obtiene un fragment diferente dependiendo de la opcion
		// seleccionada
		switch (posicion) {
		case CODE_MESA:
			fragment = new ListaMesasFragment();
			break;
		case CODE_RESERVAS_PENDIENTES:
			fragment = new ListaReservasFragment();
			this.estadoPedido = GestionReserva.ESTADO_PENDIENTE;
			bundle = new Bundle();
			bundle.putString(ListaReservasFragment.EXTRA_ID_ESTADO,
					this.estadoPedido);
			break;
		case CODE_RESERVAS_ACEPTADAS:
			fragment = new ListaReservasFragment();
			this.estadoPedido = GestionReserva.ESTADO_ACEPTADA_LOCAL;
			bundle = new Bundle();
			bundle.putString(ListaReservasFragment.EXTRA_ID_ESTADO,
					this.estadoPedido);
			break;
		case CODE_RESERVAS_RECHAZADAS:
			fragment = new ListaReservasFragment();
			this.estadoPedido = GestionReserva.ESTADO_RECHAZADA_LOCAL;
			bundle = new Bundle();
			bundle.putString(ListaReservasFragment.EXTRA_ID_ESTADO,
					this.estadoPedido);
			break;
		case CODE_CALENDARIO:
			fragment = new CalendarioReservasFragment();
			flDetalle.setVisibility(View.GONE);
			break;
		}

		Fragment fragmentActivo = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.listaReservasFl);

		if (bundle != null) {
			fragment.setArguments(bundle);
		}

		if (fragmentActivo instanceof Lista) {
			((Lista) fragmentActivo).ocultarDetalle();
		} else if (fragmentActivo instanceof ListaEstado) {
			((ListaEstado) fragmentActivo).ocultarDetalle();
		}

		FragmentManager fragmentManager = getSupportFragmentManager();

		fragmentManager.beginTransaction()
				.replace(R.id.listaReservasFl, fragment).commit();

		navList.setItemChecked(posicion, true);

		mDrawerLayout.closeDrawer(navList);

	}

}
