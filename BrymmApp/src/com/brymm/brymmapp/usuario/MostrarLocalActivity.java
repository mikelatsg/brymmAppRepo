package com.brymm.brymmapp.usuario;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.usuario.bbdd.GestionServicioLocal;

import com.brymm.brymmapp.usuario.pojo.ServicioLocal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MostrarLocalActivity extends FragmentActivity {

	public static final String EXTRA_LOCAL = "local";
	private static final int TIPO_SERVICIO_PEDIDO = 1;
	private static final int TIPO_SERVICIO_RESERVA = 3;
	private static final int TIPO_SERVICIO_MENU = 4;

	private ListView navList;
	private DrawerLayout mDrawerLayout;
	private List<ServicioLocal> serviciosLocalMenu;	

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
		setContentView(R.layout.activity_mostrar_local);
		
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
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.mostrarLocalDl);
		navList = (ListView) findViewById(R.id.mostrarLocalLvNavegador);

		List<ServicioLocal> serviciosLocal;
		
		// Se asigna la lista de servicios
		GestionServicioLocal gsl = new GestionServicioLocal(this);
		serviciosLocal = gsl.obtenerServiciosLocal();
		gsl.cerrarDatabase();

		List<String> serviciosString = new ArrayList<String>();
		serviciosLocalMenu = new ArrayList<ServicioLocal>();
		for (ServicioLocal servicioLocal : serviciosLocal) {
			// Solo se a�aden los servicios menu,pedido y reserva
			if (servicioLocal.getIdTipoServicio() == 1
					|| servicioLocal.getIdTipoServicio() == 3
					|| servicioLocal.getIdTipoServicio() == 4) {
				serviciosString.add(servicioLocal.getNombre());
				serviciosLocalMenu.add(servicioLocal);
			}
		}

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, serviciosString);

		navList.setAdapter(adaptador);
		// navList.setOnItemSelectedListener(oisl);
		navList.setOnItemClickListener(oicl);

		FragmentManager fragmentManager = getSupportFragmentManager();

		Fragment fragment = new LocalInicioFragment();

		/*
		 * Local local = (Local) getIntent().getParcelableExtra(EXTRA_LOCAL);
		 * Bundle args = new Bundle(); args.putParcelable(EXTRA_LOCAL, local);
		 * fragment.setArguments(args);
		 */

		fragmentManager.beginTransaction()
				.replace(R.id.mostrarLocalLlPrincipal, fragment).commit();
		
	}

	private void cambiarFragment(int posicion) {
		ServicioLocal servicio = serviciosLocalMenu.get(posicion);

		Fragment fragment = null;
		switch (servicio.getIdTipoServicio()) {
		case TIPO_SERVICIO_PEDIDO:
			fragment = new HacerPedidoFragment();
			break;
		case TIPO_SERVICIO_RESERVA:
			fragment = new ReservarFragment();
			break;
		case TIPO_SERVICIO_MENU:
			fragment = new MenuFragment();
			break;
		}

		FragmentManager fragmentManager = getSupportFragmentManager();

		fragmentManager.beginTransaction()
				.replace(R.id.mostrarLocalLlPrincipal, fragment).commit();

		navList.setItemChecked(posicion, true);

		mDrawerLayout.closeDrawer(navList);

	}

}
