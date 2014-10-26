package com.brymm.brymmapp.servicios;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.local.bbdd.GestionActualizaciones;
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
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

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
		int idLocal = LoginActivity.getLocal(this);
		GestionActualizaciones ga = new GestionActualizaciones(this);
		String fecha = ga.obtenerUltimaFechaActualizacion();
		ga.cerrarDatabase();

		if (idLocal > 0) {
			DatosLocal dl = new DatosLocal();
			dl.execute(Integer.toString(idLocal),
					fecha);
		}

		Toast.makeText(getApplicationContext(),
				"Service Running " + Integer.toString(idLocal), 1).show();
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

	private JSONObject obtenerActualicacionDatosLocal(int idLocal, String fecha) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {

			String url = LoginActivity.SITE_URL
					+ "/api/alertas/obtenerAlertasLocal/format/json";

			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(this));
			jsonObject.addProperty(GestionActualizaciones.JSON_FECHA, fecha);

			StringEntity se = new StringEntity(jsonObject.toString());

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse httpResponse = httpClient.execute(httpPost);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception ex) {
			respJSON = null;
			ex.printStackTrace();
		}

		return respJSON;
	}

	public class DatosLocal extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			JSONObject respJSON = null;
			respJSON = obtenerActualicacionDatosLocal(
					Integer.parseInt(params[0]), params[1]);

			boolean operacionOk;
			try {
				operacionOk = respJSON.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				if (operacionOk) {
					GestionActualizaciones ga = new GestionActualizaciones(
							ServicioActualizacionLocal.this);
					ga.guardarActualizacion();
					ga.cerrarDatabase();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
		}

	}

}
