package com.brymm.brymmapp.local;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.bbdd.GestionComanda;
import com.brymm.brymmapp.local.fragments.AnadirAComandaFragment;
import com.brymm.brymmapp.local.fragments.CrearComandaFragment;
import com.brymm.brymmapp.local.fragments.ListaComandasFragment;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.menu.MenuLocal;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ComandasActivity extends FragmentActivity {

	private ListView navList;
	private DrawerLayout mDrawerLayout;
	private List<String> estadosComanda;
	private List<String> idEstadosComanda;
	private String estadoComanda = GestionComanda.ESTADO_ACTIVO;

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
		setContentView(R.layout.activity_comandas);
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
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}

			return true;
		}
		
		if (MenuLocal.gestionMenu(item.getItemId(), this)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CrearComandaFragment.REQUEST_CODE_ANADIR_ARTICULO:
			case CrearComandaFragment.REQUEST_CODE_ANADIR_ARTICULO_PER:
			case CrearComandaFragment.REQUEST_CODE_ANADIR_MENU:
				Comanda comanda = data
						.getParcelableExtra(CrearComandaFragment.EXTRA_COMANDA);
				boolean esCrear = data.getBooleanExtra(
						CrearComandaFragment.EXTRA_CREAR, true);
				if (esCrear){
					CrearComandaFragment crearFragment;

					// Make new fragment to show this selection.
					crearFragment = (CrearComandaFragment) getSupportFragmentManager()
							.findFragmentById(R.id.listaComandasFl);					
					
					crearFragment.setComanda(comanda);
					crearFragment.actualizarComanda();
				}else{
					AnadirAComandaFragment anadirFragment;

					// Make new fragment to show this selection.
					anadirFragment = (AnadirAComandaFragment) getSupportFragmentManager()
							.findFragmentById(R.id.listaComandasFl);					
					
					anadirFragment.setComanda(comanda);
					anadirFragment.actualizarComanda();
				}
				break;
			default:
				ListaComandasFragment listaFragment = (ListaComandasFragment) getSupportFragmentManager()
						.findFragmentById(R.id.listaComandasFl);
				listaFragment.actualizarLista(estadoComanda);
				break;
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void inicializar() {

		mDrawerLayout = (DrawerLayout) findViewById(R.id.comandasDl);
		navList = (ListView) findViewById(R.id.comandasLvNavegador);

		// Se obtienen los estado de las comandas para añadirlos al nav
		String[] estadoComandaArray = getResources().getStringArray(
				R.array.estadoComandaArray);
		estadosComanda = new ArrayList<String>();
		idEstadosComanda = new ArrayList<String>();
		for (int i = 0; i < estadoComandaArray.length; i++) {
			String[] stringEstado = estadoComandaArray[i].split("#");
			estadosComanda.add(stringEstado[0]);
			idEstadosComanda.add(stringEstado[1]);
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, estadosComanda);

		navList.setAdapter(adaptador);
		navList.setOnItemClickListener(oicl);

		// FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment fragment = new ListaComandasFragment();

		// En el inicio se pasa el estado pendiente
		Bundle args = new Bundle();
		args.putString(ListaComandasFragment.EXTRA_ID_ESTADO,
				this.idEstadosComanda.get(0));
		fragment.setArguments(args);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.listaComandasFl, fragment);

		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		navList.setItemChecked(0, true);

	}

	private void cambiarFragment(int posicion) {
		boolean fragmentCambiado = true;

		FragmentManager fragmentManager = getSupportFragmentManager();
		// Se pasa el estado de las comandas a mostrar
		Bundle args = new Bundle();
		Fragment fragment = null;
		switch (posicion) {
		case 0:
		case 1:
			fragment = null;
			fragment = new ListaComandasFragment();

			args.putString(ListaComandasFragment.EXTRA_ID_ESTADO,
					this.idEstadosComanda.get(posicion));
			fragment.setArguments(args);

			// Se guarda el estado
			this.estadoComanda = this.idEstadosComanda.get(posicion);

			fragmentManager.beginTransaction()
					.replace(R.id.listaComandasFl, fragment).commit();
			break;
		case 2:

			// Comprubo si se trata de un camarero
			if (LoginActivity.esSesionCamarero(this)) {
				fragment = null;
				fragment = new CrearComandaFragment();
				// Se guarda el estado
				this.estadoComanda = this.idEstadosComanda.get(posicion);

				fragmentManager.beginTransaction()
						.replace(R.id.listaComandasFl, fragment).commit();
			} else {
				Resources res = this.getResources();
				fragmentCambiado = false;
				Toast.makeText(this,
						res.getString(R.string.crear_comanda_err_camarero),
						Toast.LENGTH_LONG).show();
			}
			break;
		case 3:

			// Comprubo si se trata de un camarero
			if (LoginActivity.esSesionCamarero(this)) {
				fragment = null;
				fragment = new AnadirAComandaFragment();
				// Se guarda el estado
				this.estadoComanda = this.idEstadosComanda.get(posicion);

				fragmentManager.beginTransaction()
						.replace(R.id.listaComandasFl, fragment).commit();
			} else {
				Resources res = this.getResources();
				fragmentCambiado = false;
				Toast.makeText(this,
						res.getString(R.string.crear_comanda_err_camarero),
						Toast.LENGTH_LONG).show();
			}
			break;
		}

		// Compruebo si se ha cambiado el fragment antes de actualizar ...
		if (fragmentCambiado) {
			ListaEstado listaFragment = (ListaEstado) getSupportFragmentManager()
					.findFragmentById(R.id.listaComandasFl);
			listaFragment.ocultarDetalle();
			navList.setItemChecked(posicion, true);
		}

		mDrawerLayout.closeDrawer(navList);

	}

}
