package com.brymm.brymmapp.usuario;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.servicios.ServicioDatosUsuario;
import com.brymm.brymmapp.servicios.ServicioDatosUsuario.MiBinder;
import com.brymm.brymmapp.usuario.adapters.LocalAdapter;
import com.brymm.brymmapp.usuario.pojo.Local;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MostrarLocalesActivity extends Activity {

	public static final String EXTRA_LOCALES = "locales";

	private ServicioDatosUsuario servicioUsuario;

	private ListView lvListaLocales;
	private List<Local> locales;

	OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adaptador, View view,
				int posicion, long id) {
			Local local = locales.get(posicion);
			cargarDatosLocal(local.getIdLocal());
			irMostrarLocal(posicion);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mostrar_locales);
		Intent intent = new Intent(MostrarLocalesActivity.this,
				ServicioDatosUsuario.class);
		bindService(intent, conexion, Context.BIND_AUTO_CREATE);
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
		lvListaLocales = (ListView) findViewById(R.id.mostrarLocalesLvListaLocales);

		// Se obtiene el intent recibido
		Intent intent = getIntent();
		locales = intent.getParcelableArrayListExtra(EXTRA_LOCALES);

		LocalAdapter adapterLocal = new LocalAdapter(this, R.layout.local_item,
				locales);
		lvListaLocales.setAdapter(adapterLocal);

		lvListaLocales.setOnItemClickListener(oicl);
	}

	private void cargarDatosLocal(int idLocal) {
		servicioUsuario.cargarDatosLocal(idLocal);
	}

	private ServiceConnection conexion = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			MiBinder binder = (MiBinder) service;
			servicioUsuario = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}
	};

	private void irMostrarLocal(int posicion) {
		Intent intent = new Intent(this, MostrarLocalActivity.class);
		intent.putExtra(MostrarLocalActivity.EXTRA_LOCAL, locales.get(posicion));
		startActivity(intent);
	}
}
