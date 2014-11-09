package com.brymm.brymmapp.usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.usuario.bbdd.GestionTipoServicio;
import com.brymm.brymmapp.usuario.bbdd.GestionTipoComida;
import com.brymm.brymmapp.usuario.pojo.Local;
import com.brymm.brymmapp.usuario.pojo.TipoServicio;
import com.brymm.brymmapp.usuario.pojo.TipoComida;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class BuscadorLocalesActivity extends Activity {

	private static final String JSON_POBLACION = "poblacion";
	private static final String JSON_CALLE = "calle";
	private static final String JSON_CODIGO_POSTAL = "codigoPostal";
	private static final String JSON_NOMBRE_LOCAL = "nombreLocal";
	private static final String JSON_SERVICIOS = "servicios";
	private static final String JSON_TIPO_COMIDA = "tipoComida";
	private static final String JSON_L_LOCALES = "locales";
	private static final String JSON_L_ID_LOCAL = "id_local";
	private static final String JSON_L_NOMBRE = "nombre";
	private static final String JSON_L_LOCALIDAD = "localidad";
	private static final String JSON_L_PROVINCIA = "provincia";
	private static final String JSON_L_DIRECCION = "direccion";
	private static final String JSON_L_CODIGO_POSTAL = "cod_postal";
	private static final String JSON_L_EMAIL = "email";
	private static final String JSON_L_TELEFONO = "telefono";
	private static final String JSON_L_TIPO_COMIDA = "tipo_comida";

	private Button bBuscar;
	private EditText etNombre, etPoblacion, etCalle, etCodigoPostal;
	private Spinner spTipoComida;
	private ArrayList<CheckBox> checks = new ArrayList<CheckBox>();

	private OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Buscador buscador = new Buscador();
			buscador.execute();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscador_locales);
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
		if (MenuUsuario.gestionMenuUsuario(item.getItemId(), this)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void inicializar() {
		bBuscar = (Button) findViewById(R.id.buscadorLocalesBtBuscar);
		etNombre = (EditText) findViewById(R.id.buscadorLocalesEtLocal);
		etPoblacion = (EditText) findViewById(R.id.buscadorLocalesEtPoblacion);
		etCalle = (EditText) findViewById(R.id.buscadorLocalesEtCalle);
		etCodigoPostal = (EditText) findViewById(R.id.buscadorLocalesEtCodigoPostal);
		spTipoComida = (Spinner) findViewById(R.id.buscadorLocalesSpTiposComida);

		bBuscar.setOnClickListener(ocl);

		// Se cargan los tipos de comida en el spinner
		GestionTipoComida gtc = new GestionTipoComida(this);
		ArrayList<TipoComida> tiposComida = gtc.obtenerTiposComida();

		// A�ado todas las comidas (no filtrar)
		Resources res = getResources();
		TipoComida tipoComida = new TipoComida(0,
				res.getString(R.string.buscador_local_todas));
		tiposComida.add(0, tipoComida);

		ArrayAdapter<TipoComida> adapter = new ArrayAdapter<TipoComida>(this,
				android.R.layout.simple_spinner_item, tiposComida);

		spTipoComida.setAdapter(adapter);

		GestionTipoServicio gsl = new GestionTipoServicio(this);
		ArrayList<TipoServicio> servicios = gsl.obtenerServicios();

		LinearLayout llBuscador = (LinearLayout) findViewById(R.id.buscadorLocalesLlscroll);

		int posicion = 6;
		// Se generan dinamicamente los checbox de servicios
		for (TipoServicio servicio : servicios) {
			CheckBox cbServicio = new CheckBox(this);
			cbServicio.setText(servicio.getNombre());
			cbServicio.setTag(servicio.getIdServicioLocal());
			llBuscador.addView(cbServicio, posicion);
			posicion++;
			checks.add(cbServicio);
		}

	}

	public class Buscador extends AsyncTask<Void, Void, List<Local>> {

		@Override
		protected List<Local> doInBackground(Void... params) {
			List<Local> locales = null;
			try {
				JSONObject localesJson = buscarLocales();
				locales = localesJsonToLocales(localesJson);
				return locales;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Local> locales) {
			super.onPostExecute(locales);
			if (locales != null) {
				irMostrarLocales(locales);
			} else {
				Toast.makeText(BuscadorLocalesActivity.this,
						R.string.buscador_local_no_locales, Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	private void irMostrarLocales(List<Local> locales) {
		Intent intent = new Intent(this, MostrarLocalesActivity.class);
		intent.putParcelableArrayListExtra(
				MostrarLocalesActivity.EXTRA_LOCALES,
				(ArrayList<? extends Parcelable>) locales);
		startActivity(intent);
	}

	private List<Local> localesJsonToLocales(JSONObject localesJsonObject)
			throws JSONException {
		List<Local> locales = new ArrayList<Local>();
		JSONArray localesJson = localesJsonObject.getJSONArray(JSON_L_LOCALES);
		for (int i = 0; i < localesJson.length(); i++) {
			JSONObject localJson = localesJson.getJSONObject(i);
			Local local = new Local(localJson.getInt(JSON_L_ID_LOCAL),
					localJson.getString(JSON_L_NOMBRE),
					localJson.getString(JSON_L_LOCALIDAD),
					localJson.getString(JSON_L_PROVINCIA),
					localJson.getString(JSON_L_DIRECCION),
					localJson.getString(JSON_L_CODIGO_POSTAL),
					localJson.getString(JSON_L_EMAIL),
					localJson.getString(JSON_L_TELEFONO),
					localJson.getString(JSON_L_TIPO_COMIDA));
			locales.add(local);
		}

		return locales;
	}

	private JSONObject buscarLocales() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url = new String(LoginActivity.SITE_URL
				+ "/api/locales/buscar/format/json");
		HttpClient client = new DefaultHttpClient();
		HttpPut put = new HttpPut(url);

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair(JSON_NOMBRE_LOCAL, etNombre.getText()
				.toString()));
		pairs.add(new BasicNameValuePair(JSON_POBLACION, etPoblacion.getText()
				.toString()));
		pairs.add(new BasicNameValuePair(JSON_CALLE, etCalle.getText()
				.toString()));
		pairs.add(new BasicNameValuePair(JSON_CODIGO_POSTAL, etCodigoPostal
				.getText().toString()));

		int contador = 0;
		for (CheckBox cb : checks) {
			if (cb.isChecked()) {
				pairs.add(new BasicNameValuePair(JSON_SERVICIOS + "["
						+ contador + "]", Integer.toString((Integer) cb
						.getTag())));
				contador++;
			}
		}

		TipoComida tc = (TipoComida) spTipoComida.getSelectedItem();

		pairs.add(new BasicNameValuePair(JSON_TIPO_COMIDA, Integer.toString(tc
				.getIdTipoComida())));

		put.setEntity(new UrlEncodedFormEntity(pairs));

		HttpResponse response = null;

		response = client.execute(put);
		/*
		 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
		 * nulo
		 */
		if (response.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
			String respStr = EntityUtils.toString(response.getEntity());
			respJSON = new JSONObject(respStr);
		}

		return respJSON;
	}
}
