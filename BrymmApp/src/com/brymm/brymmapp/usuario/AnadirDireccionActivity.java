package com.brymm.brymmapp.usuario;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.bbdd.GestionDireccion;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AnadirDireccionActivity extends Activity {

	private static final String JSON_ID_USUARIO = "idUsuario";
	private static final String JSON_DIRECCION = "direccion";
	public static final String JSON_DIRECCION_OK = "direccionOK";
	public static final String JSON_MENSAJE = "mensaje";

	private EditText etNombre, etDireccion, etPoblacion, etProvincia;
	private Button btGuardar, btCancelar;

	private boolean modificarDireccion = false;
	private int idDireccion = 0;

	private OnClickListener oclGuardar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			NuevaDireccion nd = new NuevaDireccion();
			nd.execute();

		}
	};

	private OnClickListener oclCancelar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anadir_direccion);
		inicializar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.anadir_direccion, menu);
		return false;
	}

	private void inicializar() {
		etNombre = (EditText) findViewById(R.id.anadirDireccionEtNombre);
		etDireccion = (EditText) findViewById(R.id.anadirDireccionEtDireccion);
		etPoblacion = (EditText) findViewById(R.id.anadirDireccionEtPoblacion);
		etProvincia = (EditText) findViewById(R.id.anadirDireccionEtProvincia);

		btGuardar = (Button) findViewById(R.id.anadirDireccionBtGuardar);
		btCancelar = (Button) findViewById(R.id.anadirDireccionBtCancelar);

		btGuardar.setOnClickListener(oclGuardar);
		btCancelar.setOnClickListener(oclCancelar);

		/*
		 * Se comprueba haber si vienen datos para modificar o crear nuevo
		 */

		Intent intent = getIntent();

		Direccion direccion = intent
				.getParcelableExtra(DireccionesActivity.EXTRA_DIRECCION);

		/*
		 * Modificar direccion, se guarda en la variable de clase idDireccion el
		 * id de la direcci�n para poder modificar en el servidor.
		 */

		if (direccion != null) {
			this.modificarDireccion = true;
			this.idDireccion = direccion.getIdDireccion();
			etNombre.setText(direccion.getNombre());
			etDireccion.setText(direccion.getDireccion());
			etPoblacion.setText(direccion.getPoblacion());
			etProvincia.setText(direccion.getProvincia());
		}

	}

	private JSONObject enviarDireccion() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		if (modificarDireccion) {
			url = new String(LoginActivity.SITE_URL
					+ "/api/direcciones/modificarDireccion/format/json");
		} else {
			url = new String(LoginActivity.SITE_URL
					+ "/api/direcciones/nuevaDireccion/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearDireccionJson();

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(json);

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			// inputStream = httpResponse.getEntity().getContent();

			String respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private String crearDireccionJson() {
		Gson gson = new Gson();

		Direccion direccion = obtenerDireccionFormulario();

		JsonElement jsonElementArticulos = gson.toJsonTree(direccion);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(JSON_ID_USUARIO, LoginActivity.getUsuario(this));

		jsonObject.add(JSON_DIRECCION, jsonElementArticulos);

		return jsonObject.toString();
	}

	private Direccion obtenerDireccionFormulario() {
		Direccion direccion = null;

		direccion = new Direccion(this.idDireccion, etNombre.getText()
				.toString(), etDireccion.getText().toString(), etPoblacion
				.getText().toString(), etProvincia.getText().toString());

		return direccion;
	}

	public class NuevaDireccion extends AsyncTask<Void, Void, Resultado> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(AnadirDireccionActivity.this, "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Void... params) {
			JSONObject respJSON = null;
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = enviarDireccion();

				boolean direccionOk = respJSON.getInt(JSON_DIRECCION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(JSON_MENSAJE);

				if (direccionOk) {
					Direccion direccion = GestionDireccion
							.direccionJson2Direccion(respJSON
									.getJSONObject(JSON_DIRECCION));

					GestionDireccion gd = new GestionDireccion(
							AnadirDireccionActivity.this);
					gd.guardarDireccion(direccion);
					gd.cerrarDatabase();
				}

				res = new Resultado(respJSON.getInt(JSON_DIRECCION_OK), mensaje);

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

				Toast.makeText(AnadirDireccionActivity.this,
						resultado.getMensaje(), Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					Intent intent = new Intent();
					setResult(RESULT_OK, intent);
					finish();
				}
			}

		}

	}

}
