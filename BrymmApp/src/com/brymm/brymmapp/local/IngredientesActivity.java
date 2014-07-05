package com.brymm.brymmapp.local;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.adapters.IngredienteAdapter;
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.menu.MenuLocal;
import com.brymm.brymmapp.usuario.LocalesFavoritosActivity;
import com.brymm.brymmapp.usuario.DireccionesActivity.BorrarDireccion;
import com.brymm.brymmapp.usuario.adapters.DireccionAdapter;
import com.brymm.brymmapp.usuario.bbdd.GestionLocalFavorito;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.util.Resultado;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
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

public class IngredientesActivity extends Activity {

	private final static int REQUEST_CODE_ANADIR_INGREDIENTE = 1;
	private final static int REQUEST_CODE_MODIFICAR_INGREDIENTE = 2;

	private Button btAnadir;
	private ListView lvIngredientes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ingredientes);
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
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			actualizarListaIngredientes();
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_ingrediente, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		IngredienteAdapter ingredienteAdapter = (IngredienteAdapter) lvIngredientes
				.getAdapter();
		Ingrediente ingrediente = ingredienteAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuIngredienteBorrar:
			BorrarIngrediente bi = new BorrarIngrediente();
			bi.execute(ingrediente.getIdIngrediente());
			return true;
		case R.id.contextMenuIngredienteModificar:
			irModificarIngrediente(ingrediente);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {

		btAnadir = (Button) findViewById(R.id.ingredientesBtAnadir);
		lvIngredientes = (ListView) findViewById(R.id.ingredientesLvIngrediente);

		actualizarListaIngredientes();

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				irAnadirIngrediente();
			}
		});
		// Se registra el listview para poder tener menu contextual
		registerForContextMenu(lvIngredientes);

	}

	private void actualizarListaIngredientes() {
		// Se obtienen los ingredientes
		GestionIngrediente gi = new GestionIngrediente(this);
		List<Ingrediente> ingredientes = gi.obtenerIngredientes();
		gi.cerrarDatabase();

		// Se genera el adaptador del listView
		IngredienteAdapter ingredienteAdapter = new IngredienteAdapter(this,
				R.layout.ingrediente_item, ingredientes);

		lvIngredientes.setAdapter(ingredienteAdapter);
	}

	private void irAnadirIngrediente() {
		Intent intent = new Intent(this,
				AnadirModificarIngredienteActivity.class);
		startActivityForResult(intent, REQUEST_CODE_ANADIR_INGREDIENTE);
	}

	private void irModificarIngrediente(Ingrediente ingrediente) {
		Intent intent = new Intent(this,
				AnadirModificarIngredienteActivity.class);
		intent.putExtra(AnadirModificarIngredienteActivity.EXTRA_INGREDIENTE,
				ingrediente);
		startActivityForResult(intent, REQUEST_CODE_MODIFICAR_INGREDIENTE);
	}

	private JSONObject borrarIngrediente(int idIngrediente) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/ingredientes/borrarIngrediente/idIngrediente/"
					+ idIngrediente + "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			respStr = EntityUtils.toString(resp.getEntity());
			Log.d("res", respStr);
			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {				
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	public class BorrarIngrediente extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idIngrediente;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(IngredientesActivity.this, "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idIngrediente = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarIngrediente(this.idIngrediente);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						// Se borra el ingrediente de la bbdd del movil.
						GestionIngrediente gi = new GestionIngrediente(
								IngredientesActivity.this);
						gi.borrarIngrediente(this.idIngrediente);
						gi.cerrarDatabase();
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
				Toast.makeText(IngredientesActivity.this,
						resultado.getMensaje(), Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					actualizarListaIngredientes();
				}
			}

		}
	}

}
