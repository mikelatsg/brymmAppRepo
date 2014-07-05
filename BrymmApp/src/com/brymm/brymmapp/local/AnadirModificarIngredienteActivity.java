package com.brymm.brymmapp.local;

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
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.pojo.Ingrediente;
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

public class AnadirModificarIngredienteActivity extends Activity {

	public final static String EXTRA_INGREDIENTE = "extraIngrediente";

	private Button btAceptar, btCancelar;
	private EditText etNombre, etDescripcion, etPrecio;
	private boolean modificarIngrediente = false;
	private int idIngrediente = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anadir_modificar_ingrediente);
		inicializar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.anadir_modificar, menu);
		return true;
	}

	private void inicializar() {
		btAceptar = (Button) findViewById(R.id.anadirModificarIngredienteBtAceptar);
		btCancelar = (Button) findViewById(R.id.anadirModificarIngredienteBtCancelar);
		etNombre = (EditText) findViewById(R.id.anadirModificarIngredienteEtNombre);
		etDescripcion = (EditText) findViewById(R.id.anadirModificarIngredienteEtDescripcion);
		etPrecio = (EditText) findViewById(R.id.anadirModificarIngredienteEtPrecio);

		Intent intent = getIntent();

		Ingrediente ingrediente = intent.getParcelableExtra(EXTRA_INGREDIENTE);

		/*
		 * Modificar direccion, se guarda en la variable de clase idDireccion el
		 * id de la direcci�n para poder modificar en el servidor.
		 */

		if (ingrediente != null) {
			this.modificarIngrediente = true;
			this.idIngrediente = ingrediente.getIdIngrediente();
			etNombre.setText(ingrediente.getNombre());
			etDescripcion.setText(ingrediente.getDescripcion());
			etPrecio.setText(Float.toString(ingrediente.getPrecio()));
		}

		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NuevoIngrediente ni = new NuevoIngrediente();
				ni.execute();

			}
		});

		btCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();

			}
		});

	}

	private JSONObject enviarIngrediente() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		if (modificarIngrediente) {
			url = new String(LoginActivity.SITE_URL
					+ "/api/ingredientes/modificarIngrediente/format/json");
		} else {
			url = new String(LoginActivity.SITE_URL
					+ "/api/ingredientes/nuevoIngrediente/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearIngredienteJson();

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

	private String crearIngredienteJson() {
		Gson gson = new Gson();

		Ingrediente ingrediente = obtenerIngredienteFormulario();

		JsonElement jsonElementIngrediente = gson.toJsonTree(ingrediente);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionIngrediente.JSON_ID_LOCAL,
				LoginActivity.getLocal(this));

		jsonObject.add(GestionIngrediente.JSON_INGREDIENTE,
				jsonElementIngrediente);

		return jsonObject.toString();
	}

	private Ingrediente obtenerIngredienteFormulario() {
		Ingrediente ingrediente = null;

		ingrediente = new Ingrediente(this.idIngrediente, etNombre.getText()
				.toString(), etDescripcion.getText().toString(),
				Float.parseFloat(etPrecio.getText().toString()));

		return ingrediente;
	}

	public class NuevoIngrediente extends AsyncTask<Void, Void, Resultado> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(
					AnadirModificarIngredienteActivity.this, "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Void... params) {
			JSONObject respJSON = null;
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = enviarIngrediente();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {
					Ingrediente ingrediente = GestionIngrediente
							.ingredienteJson2Ingrediente(respJSON
									.getJSONObject(GestionIngrediente.JSON_INGREDIENTE));

					GestionIngrediente gi = new GestionIngrediente(
							AnadirModificarIngredienteActivity.this);

					gi.guardarIngrediente(ingrediente);
					gi.cerrarDatabase();

				}

				res = new Resultado(
						respJSON.getInt(Resultado.JSON_OPERACION_OK), mensaje);

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

				Toast.makeText(AnadirModificarIngredienteActivity.this,
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
