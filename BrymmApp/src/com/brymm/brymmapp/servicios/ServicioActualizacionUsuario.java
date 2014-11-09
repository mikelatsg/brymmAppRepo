package com.brymm.brymmapp.servicios;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.ReservasActivity;
import com.brymm.brymmapp.local.bbdd.GestionActualizaciones;
import com.brymm.brymmapp.usuario.UltimasReservasActivity;
import com.brymm.brymmapp.usuario.UltimosPedidosActivity;
import com.brymm.brymmapp.usuario.bbdd.GestionPedidoUsuario;
import com.brymm.brymmapp.usuario.bbdd.GestionReservaUsuario;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;
import com.brymm.brymmapp.usuario.pojo.ReservaUsuario;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class ServicioActualizacionUsuario extends Service {

	public static final String TIPO_OBJECTO = "tipoObjeto";
	public static final String ALERTAS = "alertas";
	public static final String OBJETO = "objeto";
	public static final String ACCION = "accion";
	public static final String JSON_ID_USUARIO = "idUsuario";
	private final IBinder binder = new MiBinder();

	public class MiBinder extends Binder {
		public ServicioActualizacionUsuario getService() {
			return ServicioActualizacionUsuario.this;
		}
	}

	@Override
	public void onCreate() {
		Toast.makeText(getApplicationContext(), "Service created ", 1).show();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int idUsuario = LoginActivity.getUsuario(this);
		GestionActualizaciones ga = new GestionActualizaciones(this);
		String fecha = ga.obtenerUltimaFechaActualizacion();
		ga.cerrarDatabase();

		if (idUsuario > 0) {
			DatosUsuario du = new DatosUsuario();
			du.execute(Integer.toString(idUsuario), fecha);
		}

		Toast.makeText(getApplicationContext(),
				"Service usuario " + Integer.toString(idUsuario), 1).show();
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

	private void guardarDatos(JSONObject datosActualizados)
			throws JSONException {
		
		String textoAlerta = null;
		Class<?> clase = null;
		// Se guardan los tipos de articulo en la bd
		JSONArray alertasJson = datosActualizados.getJSONArray(ALERTAS);

		for (int i = 0; i < alertasJson.length(); i++) {
			JSONObject jsonObject = alertasJson.getJSONObject(i);
			Resources res = getResources();

			// Pedidos
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionPedidoUsuario.JSON_PEDIDO)) {
				
				//Se comprueba si requiere notificacion
				if (jsonObject.getInt(ServicioActualizacionLocal.CREAR_NOTIFICACION) == 1) {
					if (textoAlerta == null) {
						textoAlerta = res
								.getString(R.string.notificacion_modificacion_dia_cierre_reserva);
						
						clase = ReservasActivity.class;
					} else {
						textoAlerta += res
								.getString(R.string.notificacion_modificacion_pedido);
						
						clase = UltimosPedidosActivity.class;
					}
				}

				PedidoUsuario pedido = GestionPedidoUsuario
						.pedidoJson2PedidoUsuario(jsonObject
								.getJSONObject(OBJETO));

				GestionPedidoUsuario gp = new GestionPedidoUsuario(this);
				gp.guardarPedido(pedido);
				gp.cerrarDatabase();
			}

			// Reservas
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionReservaUsuario.JSON_RESERVA)) {
				
				//Se comprueba si requiere notificacion
				if (jsonObject.getInt(ServicioActualizacionLocal.CREAR_NOTIFICACION) == 1) {
					if (textoAlerta == null) {
						textoAlerta = res
								.getString(R.string.notificacion_modificacion_reserva);
						
						clase = ReservasActivity.class;
					} else {
						textoAlerta += res
								.getString(R.string.notificacion_dia_cierre_reserva);
						
						clase = UltimasReservasActivity.class;
					}
				}

				ReservaUsuario reserva = GestionReservaUsuario
						.reservaJson2ReservaUsuario(jsonObject
								.getJSONObject(OBJETO));

				GestionReservaUsuario gr = new GestionReservaUsuario(this);
				gr.guardarReserva(reserva);
				gr.cerrarDatabase();
			}

		}
		
		if (textoAlerta != null){
			crearNotificacion(clase, textoAlerta);
		}

	}
	
	public void crearNotificacion(Class<?> destino, String texto) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(android.R.drawable.stat_sys_warning)
				.setContentTitle("Brymm").setContentText(texto)
				.setContentInfo("4").setTicker("Alerta!")
				.setAutoCancel(true);

		Intent notIntent = new Intent(this, destino);

		PendingIntent contIntent = PendingIntent.getActivity(this, 0,
				notIntent, 0);

		mBuilder.setContentIntent(contIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);		
		
		mNotificationManager.notify(1, mBuilder.build());
		
		
	}

	private JSONObject obtenerActualicacionDatosUsuario(int idUsuario, String fecha) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {

			String url = LoginActivity.SITE_URL
					+ "/api/alertas/obtenerAlertasUsuario/format/json";

			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(JSON_ID_USUARIO,
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

	public class DatosUsuario extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			JSONObject respJSON = null;
			respJSON = obtenerActualicacionDatosUsuario(
					Integer.parseInt(params[0]), params[1]);

			boolean operacionOk;
			try {
				operacionOk = respJSON.getInt(Resultado.JSON_OPERACION_OK) == 1 ? true
						: false;

				if (operacionOk) {
					guardarDatos(respJSON);

					GestionActualizaciones ga = new GestionActualizaciones(
							ServicioActualizacionUsuario.this);
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
