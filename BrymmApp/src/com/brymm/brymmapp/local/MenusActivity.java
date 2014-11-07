package com.brymm.brymmapp.local;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.fragments.CalendarioMenusFragment;
import com.brymm.brymmapp.local.fragments.ListaMenusFragment;
import com.brymm.brymmapp.local.fragments.ListaPlatosFragment;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MenusActivity extends FragmentActivity {

	private ListView navList;
	private DrawerLayout mDrawerLayout;
	private List<String> menus;

	public static final int REQUEST_ANADIR_PLATO = 1;
	public static final int REQUEST_ANADIR_MENU =2;
	
	private static final int CODE_PLATOS = 0;
	private static final int CODE_MENUS = 1;
	private static final int CODE_CALENDARIO = 2;

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
		setContentView(R.layout.activity_menus);
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

		if (requestCode == REQUEST_ANADIR_PLATO || requestCode == REQUEST_ANADIR_MENU ) {
			if (resultCode == RESULT_OK) {
				Lista listaFragment = (Lista) getSupportFragmentManager()
						.findFragmentById(R.id.listaReservasFl);
				listaFragment.actualizarLista();
			}
		} 

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void inicializar() {

		mDrawerLayout = (DrawerLayout) findViewById(R.id.menusDl);
		navList = (ListView) findViewById(R.id.menusLvNavegador);

		// Se obtienen las pestañas de menus para añadirlos al nav
		String[] menusArray = getResources().getStringArray(R.array.menusArray);
		menus = new ArrayList<String>();
		for (int i = 0; i < menusArray.length; i++) {
			String[] stringEstado = menusArray[i].split("#");
			menus.add(stringEstado[0]);
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, menus);

		navList.setAdapter(adaptador);
		navList.setOnItemClickListener(oicl);

		// FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment fragment = new ListaPlatosFragment();

		/*
		 * fragmentManager.beginTransaction() .replace(R.id.listaPedidosFl,
		 * fragment).commit();
		 */

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// ft.replace(R.id.listaPedidosFl, fragment);
		ft.add(R.id.listaMenusFl, fragment);

		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		navList.setItemChecked(0, true);

	}

	private void cambiarFragment(int posicion) {

		Fragment fragment = null;

		// Se obtienen el frameLayout del detalle
		FrameLayout flDetalle = (FrameLayout) findViewById(R.id.detalleMenuFl);
		flDetalle.setVisibility(View.VISIBLE);

		switch (posicion) {
		case CODE_PLATOS:
			fragment = new ListaPlatosFragment();
			break;
		case CODE_MENUS:
			fragment = new ListaMenusFragment();
			break;
		case CODE_CALENDARIO:
			fragment = new CalendarioMenusFragment();
			flDetalle.setVisibility(View.GONE);
			break;
		}

		Fragment fragmentActivo = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.listaMenusFl);

		if (fragmentActivo instanceof Lista) {
			((Lista) fragmentActivo).ocultarDetalle();
		}

		FragmentManager fragmentManager = getSupportFragmentManager();

		fragmentManager.beginTransaction().replace(R.id.listaMenusFl, fragment)
				.commit();

		navList.setItemChecked(posicion, true);

		mDrawerLayout.closeDrawer(navList);

	}

}
