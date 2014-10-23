package com.brymm.brymmapp.servicios;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionCamarero;
import com.brymm.brymmapp.local.bbdd.GestionComanda;
import com.brymm.brymmapp.local.bbdd.GestionDiaCierre;
import com.brymm.brymmapp.local.bbdd.GestionDiaCierreReserva;
import com.brymm.brymmapp.local.bbdd.GestionDiaSemana;
import com.brymm.brymmapp.local.bbdd.GestionHorarioLocal;
import com.brymm.brymmapp.local.bbdd.GestionHorarioPedido;
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.bbdd.GestionMenu;
import com.brymm.brymmapp.local.bbdd.GestionMenuDia;
import com.brymm.brymmapp.local.bbdd.GestionMesa;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.bbdd.GestionPlato;
import com.brymm.brymmapp.local.bbdd.GestionReserva;
import com.brymm.brymmapp.local.bbdd.GestionServicioLocal;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticulo;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticuloLocal;
import com.brymm.brymmapp.local.bbdd.GestionTipoComanda;
import com.brymm.brymmapp.local.bbdd.GestionTipoMenu;
import com.brymm.brymmapp.local.bbdd.GestionTipoPlato;
import com.brymm.brymmapp.local.bbdd.GestionTipoServicio;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.Camarero;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DiaCierre;
import com.brymm.brymmapp.local.pojo.DiaCierreReserva;
import com.brymm.brymmapp.local.pojo.DiaSemana;
import com.brymm.brymmapp.local.pojo.HorarioLocal;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.local.pojo.Pedido;
import com.brymm.brymmapp.local.pojo.Plato;
import com.brymm.brymmapp.local.pojo.Reserva;
import com.brymm.brymmapp.local.pojo.ServicioLocal;
import com.brymm.brymmapp.local.pojo.TipoArticulo;
import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;
import com.brymm.brymmapp.local.pojo.TipoComanda;
import com.brymm.brymmapp.local.pojo.TipoMenu;
import com.brymm.brymmapp.local.pojo.TipoPlato;
import com.brymm.brymmapp.local.pojo.TipoServicio;
import com.brymm.brymmapp.local.pojo.HorarioPedido;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServicioActualizacionLocal extends Service {

	private final IBinder binder = new MiBinder();

	public class MiBinder extends Binder {
		public ServicioActualizacionLocal getService() {
			return ServicioActualizacionLocal.this;
		}
	}

	@Override
	public void onCreate() {
		Toast.makeText(getApplicationContext(), "Service created ", 1).show();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int idLocal = intent.getIntExtra(LoginActivity.EXTRA_ID_LOCAL, -1);

		Toast.makeText(getApplicationContext(), "Service Running ", 1).show();
		return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(getApplicationContext(), "Service destroyed ", 1).show();
		super.onDestroy();
	}

	private JSONObject obtenerDatosLocal(int idLocal) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/locales/datosLocal/idLocal/" + idLocal
					+ "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			respStr = EntityUtils.toString(resp.getEntity());
			// Log.w("res", respStr);
			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
			ex.printStackTrace();
		}

		return respJSON;
	}

	
	public class DatosLocal extends AsyncTask<Integer, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Integer... params) {
			return obtenerDatosLocal(params[0]);
		}

		@Override
		protected void onPostExecute(JSONObject respJSON) {
			super.onPostExecute(respJSON);
			if (respJSON != null) {

			}
		}

	}

}
