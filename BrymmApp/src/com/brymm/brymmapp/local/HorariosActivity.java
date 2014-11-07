package com.brymm.brymmapp.local;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.fragments.ListaDiasCierreFragment;
import com.brymm.brymmapp.local.fragments.ListaHorariosLocalFragment;
import com.brymm.brymmapp.local.fragments.ListaHorariosPedidoFragment;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.menu.MenuLocal;

import android.os.Bundle;
import android.content.Intent;
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

public class HorariosActivity extends FragmentActivity {

	public static final int REQUEST_ANADIR_HORARIO_LOCAL = 1;
	public static final int REQUEST_ANADIR_HORARIO_PEDIDO = 2;
	public static final int REQUEST_ANADIR_DIA_CIERRE = 3;
	private static final int CODE_HORARIO_LOCAL = 0;
	private static final int CODE_HORARIO_PEDIDO = 1;
	private static final int CODE_DIA_CIERRE = 2;

	private ListView navList;
	private DrawerLayout mDrawerLayout;
	private List<String> horarios;

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
		setContentView(R.layout.activity_horarios);
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
			Lista listaFragment = (Lista) getSupportFragmentManager()
					.findFragmentById(R.id.listaHorariosFl);
			listaFragment.actualizarLista();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void inicializar() {

		mDrawerLayout = (DrawerLayout) findViewById(R.id.horariosDl);
		navList = (ListView) findViewById(R.id.horariosLvNavegador);

		// Se obtienen los estado de los pedidos para añadirlos al nav
		String[] horariosArray = getResources().getStringArray(
				R.array.horariosArray);
		horarios = new ArrayList<String>();
		for (int i = 0; i < horariosArray.length; i++) {
			String[] stringEstado = horariosArray[i].split("#");
			horarios.add(stringEstado[0]);
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, horarios);

		navList.setAdapter(adaptador);
		navList.setOnItemClickListener(oicl);

		Fragment fragment = new ListaHorariosLocalFragment();

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.listaHorariosFl, fragment);

		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

		navList.setItemChecked(0, true);

	}

	private void cambiarFragment(int posicion) {

		Fragment fragment = null;
		// Se obtiene un fragment diferente dependiendo de la opcion
		// seleccionada
		switch (posicion) {
		case CODE_HORARIO_LOCAL:
			fragment = new ListaHorariosLocalFragment();
			/*
			 * ListaHorariosLocalFragment listaFragment =
			 * (ListaHorariosLocalFragment) getSupportFragmentManager()
			 * .findFragmentById(R.id.listaHorariosFl);
			 * 
			 * listaFragment.ocultarDetalle();
			 */
			break;
		case CODE_HORARIO_PEDIDO:
			fragment = new ListaHorariosPedidoFragment();
			break;
		case CODE_DIA_CIERRE:
			fragment = new ListaDiasCierreFragment();
			break;
		}

		Fragment fragmentActivo = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.listaHorariosFl);

		if (fragmentActivo instanceof Lista) {
			((Lista) fragmentActivo).ocultarDetalle();
		} 

		FragmentManager fragmentManager = getSupportFragmentManager();

		fragmentManager.beginTransaction()
				.replace(R.id.listaHorariosFl, fragment).commit();

		navList.setItemChecked(posicion, true);

		mDrawerLayout.closeDrawer(navList);

	}

}
