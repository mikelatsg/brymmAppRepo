package com.brymm.brymmapp.usuario;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.R.layout;
import com.brymm.brymmapp.R.menu;
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.servicios.ServicioDatosUsuario;
import com.brymm.brymmapp.servicios.ServicioDatosUsuario.MiBinder;
import com.brymm.brymmapp.usuario.DireccionesActivity.BorrarDireccion;
import com.brymm.brymmapp.usuario.adapters.DireccionAdapter;
import com.brymm.brymmapp.usuario.adapters.LocalAdapter;
import com.brymm.brymmapp.usuario.bbdd.GestionDireccion;
import com.brymm.brymmapp.usuario.bbdd.GestionLocalFavorito;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.Local;
import com.brymm.brymmapp.usuario.pojo.LocalFavorito;
import com.brymm.brymmapp.util.Resultado;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class LocalesFavoritosActivity extends ListActivity {
	
	private ServicioDatosUsuario servicioUsuario;

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

	OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long arg3) {
			Local local = (Local) getListAdapter().getItem(position);
			cargarDatosLocal(local.getIdLocal());
			while(!servicioUsuario.isDatosLocalCargados()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			irMostrarLocal(local);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_favoritos, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		LocalAdapter localAdapter = (LocalAdapter) getListView().getAdapter();
		Local local = localAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuFavoritosEliminar:
			EliminarFavorito ef = new EliminarFavorito();
			ef.execute(local.getIdLocal());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		Intent intent = new Intent(LocalesFavoritosActivity.this,
				ServicioDatosUsuario.class);
		
		bindService(intent, conexion, Context.BIND_AUTO_CREATE);
		actualizarListaLocales();
		getListView().setOnItemClickListener(oicl);
		registerForContextMenu(getListView());
	}
	
	private void cargarDatosLocal(int idLocal) {
		servicioUsuario.cargarDatosLocal(idLocal);
	}

	private void irMostrarLocal(Local local) {
		Intent intent = new Intent(this, MostrarLocalActivity.class);
		intent.putExtra(MostrarLocalActivity.EXTRA_LOCAL, local);
		startActivity(intent);
	}

	private void actualizarListaLocales() {
		GestionLocalFavorito glf = new GestionLocalFavorito(this);
		List<Local> locales = (List) glf.obtenerLocalesFavoritos();
		glf.cerrarDatabase();

		LocalAdapter localAdapter = new LocalAdapter(this, R.layout.local_item,
				locales);

		setListAdapter(localAdapter);
	}

	@Override
	protected void onDestroy() {
		unbindService(conexion);
		super.onDestroy();
	}
	
	private JSONObject eliminarFavorito(int idLocal, int idUsuario) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/usuarios/eliminarFavorito/idLocal/" + idLocal
					+ "/idUsuario/" + idUsuario + "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respStr = EntityUtils.toString(resp.getEntity());
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	public class EliminarFavorito extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idLocal;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(LocalesFavoritosActivity.this, "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idLocal = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = eliminarFavorito(idLocal,
						LoginActivity.getUsuario(LocalesFavoritosActivity.this));

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						GestionLocalFavorito glf = new GestionLocalFavorito(
								LocalesFavoritosActivity.this);
						glf.borrarLocalFavorito(idLocal);
						glf.cerrarDatabase();
					}

					res = new Resultado(
							respJSON.getInt(Resultado.JSON_OPERACION_OK),
							mensaje);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(Resultado resultado) {
			super.onPostExecute(resultado);
			progress.dismiss();

			if (resultado != null) {
				Toast.makeText(LocalesFavoritosActivity.this,
						resultado.getMensaje(), Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					actualizarListaLocales();
				}
			}

		}
	}

}
