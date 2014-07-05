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
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.usuario.adapters.DireccionAdapter;
import com.brymm.brymmapp.usuario.bbdd.GestionDireccion;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.util.Resultado;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class DireccionesActivity extends Activity {

	private static final int REQUEST_CODE_ANADIR_DIRECCION = 1;
	private static final int REQUEST_CODE_MODIFICAR_DIRECCION = 2;
	public static final String EXTRA_DIRECCION = "extraDireccion";

	private Button btAnadir;
	private ListView lvDirecciones;

	OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			irAnadirDireccion();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direcciones);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ANADIR_DIRECCION:
			if (resultCode == RESULT_OK) {
				actualizarDireccionesLista();
			}
			break;
		case REQUEST_CODE_MODIFICAR_DIRECCION:
			if (resultCode == RESULT_OK) {
				actualizarDireccionesLista();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_direccion, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		DireccionAdapter direccionAdapter = (DireccionAdapter) lvDirecciones
				.getAdapter();
		Direccion direccion = direccionAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuDireccionBorrar:
			BorrarDireccion bd = new BorrarDireccion();
			bd.execute(direccion.getIdDireccion());
			return true;
		case R.id.contextMenuDireccionModificar:
			irModificarDireccion(direccion);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		btAnadir = (Button) findViewById(R.id.direccionesBtAnadir);
		lvDirecciones = (ListView) findViewById(R.id.direccionesLv);
		registerForContextMenu(lvDirecciones);

		actualizarDireccionesLista();

		btAnadir.setOnClickListener(ocl);
	}

	private void irAnadirDireccion() {
		Intent intent = new Intent(this, AnadirDireccionActivity.class);
		startActivityForResult(intent, REQUEST_CODE_ANADIR_DIRECCION);
	}

	private void irModificarDireccion(Direccion direccion) {
		Intent intent = new Intent(this, AnadirDireccionActivity.class);
		intent.putExtra(EXTRA_DIRECCION, direccion);
		startActivityForResult(intent, REQUEST_CODE_MODIFICAR_DIRECCION);
	}

	private void actualizarDireccionesLista() {
		GestionDireccion gd = new GestionDireccion(this);
		List<Direccion> direcciones = gd.obtenerDireccionesUsuario();
		gd.cerrarDatabase();

		DireccionAdapter direccionAdapter = new DireccionAdapter(this,
				R.layout.direccion_item, direcciones);
		lvDirecciones.setAdapter(direccionAdapter);
	}

	private JSONObject borrarDireccion(int idDireccion) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/direcciones/borrarDireccion/idDireccion/"
					+ idDireccion + "/format/json";

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

	public class BorrarDireccion extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idDireccion;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(DireccionesActivity.this, "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idDireccion = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarDireccion(idDireccion);

				if (respJSON != null) {

					boolean direccionOk = respJSON
							.getInt(AnadirDireccionActivity.JSON_DIRECCION_OK) == 0 ? false
							: true;

					mensaje = respJSON
							.getString(AnadirDireccionActivity.JSON_MENSAJE);

					if (direccionOk) {

						GestionDireccion gd = new GestionDireccion(
								DireccionesActivity.this);
						gd.borrarDireccion(this.idDireccion);
						gd.cerrarDatabase();
					}

					res = new Resultado(
							respJSON.getInt(AnadirDireccionActivity.JSON_DIRECCION_OK),
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
				Toast.makeText(DireccionesActivity.this,
						resultado.getMensaje(), Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					actualizarDireccionesLista();
				}
			}

		}
	}
}
