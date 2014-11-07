package com.brymm.brymmapp.local;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.fragments.ListaArticulosFragment;
import com.brymm.brymmapp.local.fragments.ListaIngredientesFragment;
import com.brymm.brymmapp.local.fragments.ListaTiposArticuloFragment;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.menu.MenuLocal;

import android.content.Intent;
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

public class ArticulosActivity extends FragmentActivity {

	private ListView navList;
	private DrawerLayout mDrawerLayout;
	private List<String> menuMostrado;
	private List<Integer> idMenuMostrado;
	
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
		setContentView(R.layout.activity_articulos);
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
			ListaArticulosFragment listaFragment = (ListaArticulosFragment) getSupportFragmentManager()
					.findFragmentById(R.id.listaArticulosFl);
			listaFragment.actualizarLista();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	
	private void inicializar(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.articulosDl);
		navList = (ListView) findViewById(R.id.articulosLvNavegador);

		// Se obtienen los estado de las comandas para añadirlos al nav
		String[] estadoComandaArray = getResources().getStringArray(
				R.array.articulosArray);
		menuMostrado = new ArrayList<String>();
		idMenuMostrado = new ArrayList<Integer>();
		for (int i = 0; i < estadoComandaArray.length; i++) {
			String[] stringEstado = estadoComandaArray[i].split("#");
			menuMostrado.add(stringEstado[0]);
			idMenuMostrado.add(Integer.parseInt(stringEstado[1]));
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, menuMostrado);

		navList.setAdapter(adaptador);
		navList.setOnItemClickListener(oicl);

		// FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment fragment = new ListaArticulosFragment();

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.listaArticulosFl, fragment);

		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		navList.setItemChecked(0, true);
	}
	
	private void cambiarFragment(int posicion) {

		FragmentManager fragmentManager = getSupportFragmentManager();
		// Se pasa el estado de las comandas a mostrar
		Fragment fragment = null;
		switch (posicion) {
		case 0:					
			fragment = new ListaIngredientesFragment();

			fragmentManager.beginTransaction()
					.replace(R.id.listaArticulosFl, fragment).commit();	
			break;
		case 1:			
			fragment = new ListaTiposArticuloFragment();

			fragmentManager.beginTransaction()
					.replace(R.id.listaArticulosFl, fragment).commit();			
			break;
		case 2:
			fragment = new ListaArticulosFragment();
			// Se guarda el estado			

			fragmentManager.beginTransaction()
					.replace(R.id.listaArticulosFl, fragment).commit();

			break;
		}
		
		Lista listaFragment = (Lista) getSupportFragmentManager()
				.findFragmentById(R.id.listaArticulosFl);

		listaFragment.ocultarDetalle();

		navList.setItemChecked(posicion, true);

		mDrawerLayout.closeDrawer(navList);

	}

}
