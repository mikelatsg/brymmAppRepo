package com.brymm.brymmapp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.HomeLocalActivity;
import com.brymm.brymmapp.servicios.ServicioDatosLocal;
import com.brymm.brymmapp.servicios.ServicioDatosUsuario;
import com.brymm.brymmapp.usuario.BuscadorLocalesActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

public class LoginActivity extends Activity {

	// Extras
	public static final String EXTRA_ID_USUARIO = "extraIdUsuario";
	public static final String EXTRA_ID_LOCAL = "extraIdLocal";

	// Preferences key
	public static final String KEY_ID_USUARIO = "keyIdUsuario";
	public static final String KEY_ID_LOCAL = "keyIdLocal";
	public static final String KEY_GUARDAR_SESION_USUARIO = "guardarSesionUsuario";
	public static final String KEY_GUARDAR_SESION_LOCAL = "guardarSesionLocal";

	// Campos JSON
	private static final String JSON_ID_USUARIO = "idUsuario";
	private static final String JSON_ID_LOCAL = "idLocal";

	// Codigos de los tab
	private static final String TAB_USUARIO = "tabUsuario";
	private static final String TAB_LOCAL = "tabLocal";

	// Login posibles
	private static final int LOGIN_USUARIO = 1;
	private static final int LOGIN_LOCAL = 2;
	private static final int LOGIN_CAMARERO = 3;

	// Codigos error
	public static final int CODE_LOGIN_OK = 200;
	private static final int CODE_LOGIN_KO = 404;

	// Url
	public static final String SITE_URL = "http://10.0.2.2/brymm/index.php";

	private Button bLoginUsuario, bLoginLocal;
	private EditText etNick, etPasswordUsuario, etNombreLocal, etCamarero,
			etPasswordLocal;
	private CheckBox cbGuardarSesionUsuario, cbGuardarSesionLocal, cbCamarero;

	private OnClickListener oclLogin = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Login login = new Login();
			switch (v.getId()) {
			case R.id.loginUsuarioBtLogin:
				login.execute(LOGIN_USUARIO);
				break;
			case R.id.loginLocalBtLogin:
				login.execute(LOGIN_LOCAL);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		inicializar();
	}

	private void inicializar() {
		// Boton login usuario
		bLoginUsuario = (Button) findViewById(R.id.loginUsuarioBtLogin);
		bLoginUsuario.setOnClickListener(oclLogin);

		// Boton login local
		bLoginLocal = (Button) findViewById(R.id.loginLocalBtLogin);
		bLoginLocal.setOnClickListener(oclLogin);

		// Campos usuario
		etNick = (EditText) findViewById(R.id.loginUsuarioEtNick);
		etPasswordUsuario = (EditText) findViewById(R.id.loginUsuarioEtPassword);
		cbGuardarSesionUsuario = (CheckBox) findViewById(R.id.loginUsuarioCbGuardarSesion);

		// Campos local
		etNombreLocal = (EditText) findViewById(R.id.loginLocalEtNombreLocal);
		etPasswordLocal = (EditText) findViewById(R.id.loginLocalEtPassword);
		cbGuardarSesionLocal = (CheckBox) findViewById(R.id.loginLocalCbGuardarSesion);
		cbCamarero = (CheckBox) findViewById(R.id.loginLocalCbCamarero);

		Resources res = getResources();

		// Creo las etiquetas y les asigno su contenido.
		TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec(TAB_USUARIO);
		spec.setContent(R.id.tabUsuario);
		spec.setIndicator(res.getString(R.string.login_usuario_tab_usuario));
		tabs.addTab(spec);

		spec = tabs.newTabSpec(TAB_LOCAL);
		spec.setContent(R.id.tabLocal);
		spec.setIndicator(res.getString(R.string.login_local_tab_local));
		tabs.addTab(spec);

		tabs.setCurrentTab(0);
	}

	public class Login extends AsyncTask<Integer, Void, JSONObject> {

		private int tipoLogin;

		@Override
		protected JSONObject doInBackground(Integer... params) {
			this.tipoLogin = params[0];
			switch (tipoLogin) {
			case LOGIN_USUARIO:
				return loginUsuario();
			case LOGIN_LOCAL:
				return loginLocal();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			Resources res = getResources();
			if (result != null) {
				switch (tipoLogin) {
				case LOGIN_USUARIO:
					int idUsuario;
					try {
						idUsuario = result.getInt(JSON_ID_USUARIO);
					} catch (JSONException e1) {
						idUsuario = -1;
					}
					// Se comprueba si se ha obtenido bien el idUsuario
					if (idUsuario > 0) {
						guardarUsuario(idUsuario);
						// arrancarServicioUsuario(idUsuario);
						ServicioUsuario su = new ServicioUsuario();
						su.execute(idUsuario);
						irHomeUsuario();
					} else {
						// Se muetra error de login
						Toast.makeText(
								LoginActivity.this,
								res.getString(R.string.login_usuario_error_login),
								Toast.LENGTH_LONG).show();
					}
					break;
				case LOGIN_LOCAL:
					int idLocal;
					try {
						idLocal = result.getInt(JSON_ID_LOCAL);
					} catch (JSONException e1) {
						idLocal = -1;
					}
					// Se comprueba si se ha obtenido bien el idLocal
					if (idLocal > 0) {
						guardarLocal(idLocal);
						// Se arranca el servicio del local
						ServicioLocal sl = new ServicioLocal();
						sl.execute(idLocal);
						irHomeLocal();
					} else {
						// Se muetra error de login
						Toast.makeText(
								LoginActivity.this,
								res.getString(R.string.login_local_error_login),
								Toast.LENGTH_LONG).show();
					}
					break;
				}

			} else {
				String msg = "";
				switch (tipoLogin) {
				case LOGIN_USUARIO:
					msg = res.getString(R.string.login_usuario_error_login);
					break;
				case LOGIN_LOCAL:
					msg = res.getString(R.string.login_local_error_login);
					break;
				}
				// Se muetra error de login
				Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG)
						.show();
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * Metodo que se conecta al servidor para comprobar el login de un usuario.
	 * Devuelve un objeto JSONObject con la respuesta, nulo si no es correcto el
	 * login.
	 * 
	 * @return JSONObject con los datos del usuario
	 * @see JSONObject
	 */

	private JSONObject loginUsuario() {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = SITE_URL + "/api/usuarios/loginUsuario/nick/"
					+ etNick.getText().toString() + "/password/"
					+ etPasswordUsuario.getText().toString() + "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			respStr = EntityUtils.toString(resp.getEntity());
			if (resp.getStatusLine().getStatusCode() == CODE_LOGIN_OK) {
				// respStr = EntityUtils.toString(resp.getEntity());
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	private JSONObject loginLocal() {
		String respStr = null;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = SITE_URL + "/api/locales/loginLocal/nombreLocal/"
					+ etNombreLocal.getText().toString() + "/password/"
					+ etPasswordLocal.getText().toString() + "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			respStr = EntityUtils.toString(resp.getEntity());
			Log.d("d",respStr);
			if (resp.getStatusLine().getStatusCode() == CODE_LOGIN_OK) {
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	/**
	 * Metodo que guarda los datos de usuario en el archivo de preferencias.
	 * 
	 */
	private void guardarUsuario(int idUsuario) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sp.edit();
		editor.putInt(KEY_ID_USUARIO, idUsuario);

		boolean guardarSesion = false;
		if (cbGuardarSesionUsuario.isChecked()) {
			guardarSesion = true;
		}
		editor.putBoolean(KEY_GUARDAR_SESION_USUARIO, guardarSesion);
		editor.commit();
	}

	public static int getUsuario(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		return sp.getInt(KEY_ID_USUARIO, 0);
	}

	/**
	 * Metodo que carga la actividad HomeUsuario
	 * 
	 */
	private void irHomeUsuario() {

		Intent intent = new Intent(this, BuscadorLocalesActivity.class);
		startActivity(intent);

	}

	/**
	 * Metodo que guarda los datos del local en el archivo de preferencias.
	 * 
	 */
	private void guardarLocal(int idLocal) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sp.edit();
		editor.putInt(KEY_ID_LOCAL, idLocal);

		boolean guardarSesion = false;
		if (cbGuardarSesionLocal.isChecked()) {
			guardarSesion = true;
		}
		editor.putBoolean(KEY_GUARDAR_SESION_LOCAL, guardarSesion);

		editor.commit();
	}

	/**
	 * Metodo que carga la actividad HomeLocal
	 * 
	 */
	private void irHomeLocal() {

		Intent intent = new Intent(this, HomeLocalActivity.class);
		startActivity(intent);

	}
	
	public static int getLocal(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		return sp.getInt(KEY_ID_LOCAL, 0);
	}

	public void arrancarServicioUsuario(int idUsuario) {
		Intent i = new Intent(this, ServicioDatosUsuario.class);
		i.putExtra(EXTRA_ID_USUARIO, idUsuario);
		startService(i);
	}

	public class ServicioUsuario extends AsyncTask<Integer, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Integer... params) {
			arrancarServicioUsuario(params[0]);
			return null;
		}

	}

	public void arrancarServicioLocal(int idLocal) {
		Intent i = new Intent(this, ServicioDatosLocal.class);
		i.putExtra(EXTRA_ID_LOCAL, idLocal);
		startService(i);
	}

	public class ServicioLocal extends AsyncTask<Integer, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Integer... params) {
			arrancarServicioLocal(params[0]);
			return null;
		}

	}
}
