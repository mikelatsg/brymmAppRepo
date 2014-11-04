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
import com.brymm.brymmapp.local.bbdd.GestionTipoArticuloLocal;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.Camarero;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DiaCierre;
import com.brymm.brymmapp.local.pojo.DiaCierreReserva;
import com.brymm.brymmapp.local.pojo.HorarioLocal;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.local.pojo.Pedido;
import com.brymm.brymmapp.local.pojo.Plato;
import com.brymm.brymmapp.local.pojo.Reserva;
import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;
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

	public static final String TIPO_OBJECTO = "tipoObjeto";
	public static final String ALERTAS = "alertas";
	public static final String OBJETO = "objeto";
	public static final String ACCION = "accion";
	public static final String BORRAR = "BOR";
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
			dl.execute(Integer.toString(idLocal), fecha);
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

	private void guardarDatos(JSONObject datosActualizados)
			throws JSONException {
		// Se guardan los tipos de articulo en la bd
		JSONArray alertasJson = datosActualizados.getJSONArray(ALERTAS);

		for (int i = 0; i < alertasJson.length(); i++) {
			JSONObject jsonObject = alertasJson.getJSONObject(i);

			// Articulos
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionArticulo.JSON_ARTICULO)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionArticulo ga = new GestionArticulo(this);
					ga.borrarArticulo(jsonObject.getJSONObject(OBJETO).getInt(
							GestionArticulo.JSON_ID_ARTICULO));
					ga.cerrarDatabase();
				} else {
					Articulo articulo = GestionArticulo
							.articuloJson2Articulo(jsonObject
									.getJSONObject(OBJETO));

					GestionArticulo ga = new GestionArticulo(this);
					ga.guardarArticulo(articulo);
					ga.cerrarDatabase();
				}

			}

			// Pedidos
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionPedido.JSON_PEDIDO)) {

				Pedido pedido = GestionPedido.pedidoJson2Pedido(jsonObject
						.getJSONObject(OBJETO));

				GestionPedido gp = new GestionPedido(this);
				gp.guardarPedido(pedido);
				gp.cerrarDatabase();
			}

			// Reservas
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionReserva.JSON_RESERVA)) {

				Reserva reserva = GestionReserva.reservaJson2Reserva(jsonObject
						.getJSONObject(OBJETO));

				GestionReserva gr = new GestionReserva(this);
				gr.guardarReserva(reserva);
				gr.cerrarDatabase();
			}

			// Comandas
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionComanda.JSON_COMANDA)) {

				Comanda comanda = GestionComanda.comandaJson2Comanda(jsonObject
						.getJSONObject(OBJETO));

				GestionComanda gc = new GestionComanda(this);
				gc.guardarComanda(comanda);
				gc.cerrarDatabase();
			}

			// Ingredientes
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionIngrediente.JSON_INGREDIENTE)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionIngrediente gi = new GestionIngrediente(this);
					gi.borrarIngrediente(jsonObject.getJSONObject(OBJETO)
							.getInt(GestionIngrediente.JSON_ID_INGREDIENTE));
					gi.cerrarDatabase();
				} else {
					Ingrediente ingrediente = GestionIngrediente
							.ingredienteJson2Ingrediente(jsonObject
									.getJSONObject(OBJETO));

					GestionIngrediente gi = new GestionIngrediente(this);
					gi.guardarIngrediente(ingrediente);
					gi.cerrarDatabase();
				}

			}

			// TiposArticuloLocal
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionTipoArticuloLocal.JSON_TIPO_ARTICULO_LOCAL)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionTipoArticuloLocal gtal = new GestionTipoArticuloLocal(
							this);
					gtal.borrarTipoArticuloLocal(jsonObject
							.getJSONObject(OBJETO)
							.getInt(GestionTipoArticuloLocal.JSON_ID_TIPO_ARTICULO_LOCAL));
					gtal.cerrarDatabase();
				} else {
					TipoArticuloLocal tipoArticuloLocal = GestionTipoArticuloLocal
							.tipoArticuloLocalJson2TipoArticuloLocal(jsonObject
									.getJSONObject(OBJETO));

					GestionTipoArticuloLocal gtal = new GestionTipoArticuloLocal(
							this);
					gtal.guardarTipoArticuloLocal(tipoArticuloLocal);
					gtal.cerrarDatabase();
				}

			}

			// Horarios local
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionHorarioLocal.JSON_HORARIO_LOCAL)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionHorarioLocal ghl = new GestionHorarioLocal(this);
					ghl.borrarHorarioLocal(jsonObject.getJSONObject(OBJETO)
							.getInt(GestionHorarioLocal.JSON_ID_HORARIO_LOCAL));
					ghl.cerrarDatabase();
				} else {
					HorarioLocal horarioLocal = GestionHorarioLocal
							.horarioLocalJson2HorarioLocal(jsonObject
									.getJSONObject(OBJETO));

					GestionHorarioLocal ghl = new GestionHorarioLocal(this);
					ghl.guardarHorarioLocal(horarioLocal);
					ghl.cerrarDatabase();
				}
			}

			// Horarios pedido
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionHorarioPedido.JSON_HORARIO_PEDIDO)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionHorarioPedido ghp = new GestionHorarioPedido(this);
					ghp.borrarHorarioPedido(jsonObject
							.getJSONObject(OBJETO)
							.getInt(GestionHorarioPedido.JSON_ID_HORARIO_PEDIDO));
					ghp.cerrarDatabase();
				} else {
					HorarioPedido horarioPedido = GestionHorarioPedido
							.horarioPedidoJson2HorarioPedido(jsonObject
									.getJSONObject(OBJETO));

					GestionHorarioPedido ghp = new GestionHorarioPedido(this);
					ghp.guardarHorarioPedido(horarioPedido);
					ghp.cerrarDatabase();
				}
			}

			// Dias cierre
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionDiaCierre.JSON_DIA_CIERRE)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionDiaCierre gdc = new GestionDiaCierre(this);
					gdc.borrarDiaCierre(jsonObject.getJSONObject(OBJETO)
							.getInt(GestionDiaCierre.JSON_ID_DIA_CIERRE));
					gdc.cerrarDatabase();
				} else {
					DiaCierre diaCierre = GestionDiaCierre
							.diaCierreJson2DiaCierre(jsonObject
									.getJSONObject(OBJETO));

					GestionDiaCierre gdc = new GestionDiaCierre(this);
					gdc.guardarDiaCierre(diaCierre);
					gdc.cerrarDatabase();
				}
			}

			// Mesas
			if (jsonObject.getString(TIPO_OBJECTO)
					.equals(GestionMesa.JSON_MESA)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionMesa gm = new GestionMesa(this);
					gm.borrarMesa(jsonObject.getJSONObject(OBJETO).getInt(
							GestionMesa.JSON_ID_MESA));
					gm.cerrarDatabase();
				} else {
					Mesa mesa = GestionMesa.mesaJson2Mesa(jsonObject
							.getJSONObject(OBJETO));

					GestionMesa gm = new GestionMesa(this);
					gm.guardarMesa(mesa);
					gm.cerrarDatabase();
				}
			}

			// Menus
			if (jsonObject.getString(TIPO_OBJECTO)
					.equals(GestionMenu.JSON_MENU)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionMenu gm = new GestionMenu(this);
					gm.borrarMenu(jsonObject.getJSONObject(OBJETO).getInt(
							GestionMenu.JSON_ID_MENU));
					gm.cerrarDatabase();
				} else {
					MenuLocal menu = GestionMenu.menuJson2Menu(jsonObject
							.getJSONObject(OBJETO));

					GestionMenu gm = new GestionMenu(this);
					gm.guardarMenu(menu);
					gm.cerrarDatabase();
				}
			}

			// Menus dia
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionMenuDia.JSON_MENU_DIA)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionMenuDia gm = new GestionMenuDia(this);
					gm.borrarMenuDia(jsonObject.getJSONObject(OBJETO).getInt(
							GestionMenuDia.JSON_ID_MENU_DIA));
					gm.cerrarDatabase();
				} else {
					MenuDia menuDia = GestionMenuDia
							.menuDiaJson2MenuDia(jsonObject
									.getJSONObject(OBJETO));

					GestionMenuDia gm = new GestionMenuDia(this);
					gm.guardarMenuDia(menuDia);
					gm.cerrarDatabase();
				}
			}

			// Platos
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionPlato.JSON_PLATO)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionPlato gp = new GestionPlato(this);
					gp.borrarPlato(jsonObject.getJSONObject(OBJETO).getInt(
							GestionPlato.JSON_ID_PLATO));
					gp.cerrarDatabase();
				} else {
					Plato plato = GestionPlato.platoJson2Plato(jsonObject
							.getJSONObject(OBJETO));

					GestionPlato gp = new GestionPlato(this);
					gp.guardarPlato(plato);
					gp.cerrarDatabase();
				}
			}

			// Camareros
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionCamarero.JSON_CAMARERO)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionCamarero gc = new GestionCamarero(this);
					gc.borrarCamarero(jsonObject.getJSONObject(OBJETO).getInt(
							GestionCamarero.JSON_ID_CAMARERO));
					gc.cerrarDatabase();
				} else {
					Camarero camarero = GestionCamarero
							.camareroJson2Camarero(jsonObject
									.getJSONObject(OBJETO));

					GestionCamarero gc = new GestionCamarero(this);
					gc.guardarCamarero(camarero);
					gc.cerrarDatabase();
				}
			}

			// Dias cierre reserva
			if (jsonObject.getString(TIPO_OBJECTO).equals(
					GestionDiaCierreReserva.JSON_DIA_CIERRE_RESERVA)) {

				if (jsonObject.getString(ACCION).equals(BORRAR)) {
					GestionDiaCierreReserva gdcr = new GestionDiaCierreReserva(
							this);
					gdcr.borrarDiaCierre(jsonObject
							.getJSONObject(OBJETO)
							.getInt(GestionDiaCierreReserva.JSON_ID_DIA_CIERRE_RESERVA));
					gdcr.cerrarDatabase();
				} else {
					DiaCierreReserva diaCierreReserva = GestionDiaCierreReserva
							.diaCierreReservaJson2DiaCierreReserva(jsonObject
									.getJSONObject(OBJETO));

					GestionDiaCierreReserva gdcr = new GestionDiaCierreReserva(this);
					gdcr.guardarDiaCierreReserva(diaCierreReserva);
					gdcr.cerrarDatabase();
				}

			}
		}

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
				operacionOk = respJSON.getInt(Resultado.JSON_OPERACION_OK) == 1 ? true
						: false;

				if (operacionOk) {
					guardarDatos(respJSON);

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
