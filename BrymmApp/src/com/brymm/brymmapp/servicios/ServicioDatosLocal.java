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

public class ServicioDatosLocal extends Service {

	private final IBinder binder = new MiBinder();

	public class MiBinder extends Binder {
		public ServicioDatosLocal getService() {
			return ServicioDatosLocal.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int idLocal = intent.getIntExtra(LoginActivity.EXTRA_ID_LOCAL, -1);

		if (idLocal > 0) {
			DatosLocal dl = new DatosLocal();
			dl.execute(idLocal);
		}
		return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
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

	private void guardarDatosLocal(JSONObject datosLocal) throws JSONException {
		// Se guardan los tipos de articulo en la bd
		JSONArray tiposArticulo = datosLocal
				.getJSONArray(GestionTipoArticulo.JSON_TIPOS_ARTICULO);
		guardarTiposArticulo(tiposArticulo);

		// Se guardan los tipos de articulo local en la bd
		JSONArray tiposArticuloLocal = datosLocal
				.getJSONArray(GestionTipoArticuloLocal.JSON_TIPOS_ARTICULO_LOCAL);
		guardarTiposArticuloLocal(tiposArticuloLocal);

		// Se guardan los ingredientes del local en la bd
		JSONArray ingredientes = datosLocal
				.getJSONArray(GestionIngrediente.JSON_INGREDIENTES);
		guardarIngredientes(ingredientes);

		// Se guardan los articulos del local en la bd
		JSONArray articulos = datosLocal
				.getJSONArray(GestionArticulo.JSON_ARTICULOS);
		guardarArticulos(articulos);

		// Se borran los pedidos
		GestionPedido gp = new GestionPedido(this);
		gp.borrarPedidos();
		gp.cerrarDatabase();

		// Se guardan los pedidos del local en la bd
		JSONArray pedidos = datosLocal
				.getJSONArray(GestionPedido.JSON_PEDIDOS_PENDIENTES);
		guardarPedidos(pedidos);

		pedidos = datosLocal.getJSONArray(GestionPedido.JSON_PEDIDOS_ACEPTADOS);
		guardarPedidos(pedidos);

		pedidos = datosLocal
				.getJSONArray(GestionPedido.JSON_PEDIDOS_RECHAZADOS);
		guardarPedidos(pedidos);

		pedidos = datosLocal
				.getJSONArray(GestionPedido.JSON_PEDIDOS_TERMINADOS);
		guardarPedidos(pedidos);

		// Se guardan los tipos de servicio en la bd
		JSONArray tiposServicio = datosLocal
				.getJSONArray(GestionTipoServicio.JSON_TIPOS_SERVICIO);
		guardarTiposServicio(tiposServicio);

		// Se guardan los servicios del local en la bd
		JSONArray serviciosLocal = datosLocal
				.getJSONArray(GestionServicioLocal.JSON_SERVICIOS_LOCAL);
		guardarServiciosLocal(serviciosLocal);

		// Se guardan los dias de la semanan en la bd
		JSONArray diasSemana = datosLocal
				.getJSONArray(GestionDiaSemana.JSON_DIAS_SEMANA);
		guardarDiasSemana(diasSemana);

		// Se guardan los horarios del local en la bd
		JSONArray horariosLocal = datosLocal
				.getJSONArray(GestionHorarioLocal.JSON_HORARIOS_LOCAL);
		guardarHorariosLocal(horariosLocal);

		// Se guardan los horarios de pedidos del local en la bd
		JSONArray horariosPedido = datosLocal
				.getJSONArray(GestionHorarioPedido.JSON_HORARIOS_PEDIDO);
		guardarHorariosPedido(horariosPedido);

		// Se guardan los dias de cierre del local en la bd
		JSONArray diasCierre = datosLocal
				.getJSONArray(GestionDiaCierre.JSON_DIAS_CIERRE);
		guardarDiasCierre(diasCierre);

		// Se guardan las mesas del local en la bd
		JSONArray mesas = datosLocal.getJSONArray(GestionMesa.JSON_MESAS);
		guardarMesas(mesas);

		// Se guardan los tipos_menu en la bd
		JSONArray tiposMenu = datosLocal
				.getJSONArray(GestionTipoMenu.JSON_TIPO_MENU);
		guardarTiposMenu(tiposMenu);

		// Se guardan las reservas en la bd
		JSONArray reservas = datosLocal
				.getJSONArray(GestionReserva.JSON_RESERVAS);
		guardarReservas(reservas);

		// Se guardan los dias de cierre de reservas
		JSONArray diasCierreReservas = datosLocal
				.getJSONArray(GestionDiaCierreReserva.JSON_DIAS_CIERRE_RESERVA);
		guardarDiasCierreReservas(diasCierreReservas);

		// Se guardan los tipos de platos
		JSONArray tiposPlato = datosLocal
				.getJSONArray(GestionTipoPlato.JSON_TIPOS_PLATO);
		guardarTiposPlato(tiposPlato);

		// Se guardan los tipos de platos
		JSONArray platos = datosLocal.getJSONArray(GestionPlato.JSON_PLATOS);
		guardarPlatos(platos);

		// Se guardan los menus
		JSONArray menus = datosLocal.getJSONArray(GestionMenu.JSON_MENUS);
		guardarMenus(menus);

		// Se guardan los menus dia
		JSONArray menusDia = datosLocal
				.getJSONArray(GestionMenuDia.JSON_MENUS_DIA);
		guardarMenusDia(menusDia);

		// Se guardan los tipos de comanda
		JSONArray tiposComanda = datosLocal
				.getJSONArray(GestionTipoComanda.JSON_TIPOS_COMANDA);
		guardarTiposComanda(tiposComanda);

		// Se guardan los camareros
		JSONArray camareros = datosLocal
				.getJSONArray(GestionCamarero.JSON_CAMAREROS);
		guardarCamareros(camareros);

		// Se guardan las comandas
		JSONArray comandasActivas = datosLocal
				.getJSONArray(GestionComanda.JSON_COMANDAS_ACTIVAS);
		//Log.d("K",comandasActivas.toString());
		guardarComandas(comandasActivas, true);

		JSONArray comandasCerradas = datosLocal
				.getJSONArray(GestionComanda.JSON_COMANDAS_CERRADAS);
		guardarComandas(comandasCerradas, false);

	}

	private void guardarTiposArticulo(JSONArray tiposArticuloJson)
			throws JSONException {
		GestionTipoArticulo gta = new GestionTipoArticulo(this);
		gta.borrarTiposArticulo();
		for (int i = 0; i < tiposArticuloJson.length(); i++) {
			JSONObject tipoArticuloJson = tiposArticuloJson.getJSONObject(i);

			TipoArticulo tipoArticulo = GestionTipoArticulo
					.tipoArticuloJson2TipoArticulo(tipoArticuloJson);

			gta.guardarTipoArticulo(tipoArticulo);

		}
		gta.cerrarDatabase();
	}

	private void guardarTiposArticuloLocal(JSONArray tiposArticuloLocalJson)
			throws JSONException {
		GestionTipoArticuloLocal gtal = new GestionTipoArticuloLocal(this);
		gtal.borrarTiposArticuloLocal();
		for (int i = 0; i < tiposArticuloLocalJson.length(); i++) {
			JSONObject tipoArticuloLocalJson = tiposArticuloLocalJson
					.getJSONObject(i);

			TipoArticuloLocal tipoArticuloLocal = GestionTipoArticuloLocal
					.tipoArticuloLocalJson2TipoArticuloLocal(tipoArticuloLocalJson);

			gtal.guardarTipoArticuloLocal(tipoArticuloLocal);

		}
		gtal.cerrarDatabase();
	}

	private void guardarIngredientes(JSONArray ingredientesJson)
			throws JSONException {
		GestionIngrediente gi = new GestionIngrediente(this);
		gi.borrarIngredientes();
		for (int i = 0; i < ingredientesJson.length(); i++) {
			JSONObject ingredienteJson = ingredientesJson.getJSONObject(i);

			Ingrediente ingrediente = GestionIngrediente
					.ingredienteJson2Ingrediente(ingredienteJson);

			gi.guardarIngrediente(ingrediente);

		}
		gi.cerrarDatabase();
	}

	private void guardarArticulos(JSONArray articulosJson) throws JSONException {
		GestionArticulo ga = new GestionArticulo(this);
		ga.borrarArticulos();
		for (int i = 0; i < articulosJson.length(); i++) {
			JSONObject articuloJson = articulosJson.getJSONObject(i);

			Articulo articulo = GestionArticulo
					.articuloJson2Articulo(articuloJson);

			ga.guardarArticulo(articulo);

		}
		ga.cerrarDatabase();
	}

	private void guardarPedidos(JSONArray pedidosJson) throws JSONException {
		GestionPedido gp = new GestionPedido(this);
		for (int i = 0; i < pedidosJson.length(); i++) {
			JSONObject pedidoJson = pedidosJson.getJSONObject(i);

			Pedido pedido = GestionPedido.pedidoJson2Pedido(pedidoJson);

			gp.guardarPedido(pedido);

		}
		gp.cerrarDatabase();
	}

	private void guardarTiposServicio(JSONArray tiposServicioJson)
			throws JSONException {
		GestionTipoServicio gts = new GestionTipoServicio(this);
		gts.borrarTiposServicio();
		for (int i = 0; i < tiposServicioJson.length(); i++) {
			JSONObject tipoServicioJson = tiposServicioJson.getJSONObject(i);

			TipoServicio tipoServicio = GestionTipoServicio
					.tipoServicioJson2TipoServicio(tipoServicioJson);

			gts.guardarTipoServicio(tipoServicio);

		}
		gts.cerrarDatabase();
	}

	private void guardarServiciosLocal(JSONArray serviciosJson)
			throws JSONException {
		GestionServicioLocal gsl = new GestionServicioLocal(this);
		gsl.borrarServiciosLocal();
		for (int i = 0; i < serviciosJson.length(); i++) {
			JSONObject servicioJson = serviciosJson.getJSONObject(i);

			ServicioLocal servicioLocal = GestionServicioLocal
					.servicioLocalJson2ServicioLocal(servicioJson);

			gsl.guardarServicioLocal(servicioLocal);

		}
		gsl.cerrarDatabase();
	}

	private void guardarDiasSemana(JSONArray diasSemanaJson)
			throws JSONException {
		GestionDiaSemana gds = new GestionDiaSemana(this);
		gds.borrarDiasSemana();
		for (int i = 0; i < diasSemanaJson.length(); i++) {
			JSONObject diaSemanaJson = diasSemanaJson.getJSONObject(i);

			DiaSemana diaSemana = GestionDiaSemana
					.diaSemanaJson2DiaSemana(diaSemanaJson);

			gds.guardarDiaSemana(diaSemana);

		}
		gds.cerrarDatabase();
	}

	private void guardarHorariosLocal(JSONArray horariosLocalJson)
			throws JSONException {
		GestionHorarioLocal gestor = new GestionHorarioLocal(this);
		gestor.borrarHorariosLocal();
		for (int i = 0; i < horariosLocalJson.length(); i++) {
			JSONObject horarioLocalJson = horariosLocalJson.getJSONObject(i);

			HorarioLocal horarioLocal = GestionHorarioLocal
					.horarioLocalJson2HorarioLocal(horarioLocalJson);

			gestor.guardarHorarioLocal(horarioLocal);

		}
		gestor.cerrarDatabase();
	}

	private void guardarHorariosPedido(JSONArray horariosPedidoJson)
			throws JSONException {
		GestionHorarioPedido gestor = new GestionHorarioPedido(this);
		gestor.borrarHorariosPedido();
		for (int i = 0; i < horariosPedidoJson.length(); i++) {
			JSONObject horarioPedidoJson = horariosPedidoJson.getJSONObject(i);

			HorarioPedido horarioPedido = GestionHorarioPedido
					.horarioPedidoJson2HorarioPedido(horarioPedidoJson);

			gestor.guardarHorarioPedido(horarioPedido);

		}
		gestor.cerrarDatabase();
	}

	private void guardarDiasCierre(JSONArray diasCierreJson)
			throws JSONException {
		GestionDiaCierre gestor = new GestionDiaCierre(this);
		gestor.borrarDiasCierre();
		for (int i = 0; i < diasCierreJson.length(); i++) {
			JSONObject diaCierreJson = diasCierreJson.getJSONObject(i);

			DiaCierre diaCierre = GestionDiaCierre
					.diaCierreJson2DiaCierre(diaCierreJson);

			gestor.guardarDiaCierre(diaCierre);

		}
		gestor.cerrarDatabase();
	}

	private void guardarMesas(JSONArray mesasJson) throws JSONException {
		GestionMesa gestor = new GestionMesa(this);
		gestor.borrarMesas();
		for (int i = 0; i < mesasJson.length(); i++) {
			JSONObject mesaJson = mesasJson.getJSONObject(i);

			Mesa mesa = GestionMesa.mesaJson2Mesa(mesaJson);

			gestor.guardarMesa(mesa);

		}
		gestor.cerrarDatabase();
	}

	private void guardarTiposMenu(JSONArray tiposMenuJson) throws JSONException {
		GestionTipoMenu gestor = new GestionTipoMenu(this);
		gestor.borrarTiposMenu();
		for (int i = 0; i < tiposMenuJson.length(); i++) {
			JSONObject tipoMenuJson = tiposMenuJson.getJSONObject(i);

			TipoMenu tipoMenu = GestionTipoMenu
					.tipoMenuJson2TipoMenu(tipoMenuJson);

			gestor.guardarTipoMenu(tipoMenu);

		}
		gestor.cerrarDatabase();
	}

	private void guardarReservas(JSONArray reservasJson) throws JSONException {
		GestionReserva gestor = new GestionReserva(this);
		gestor.borrarReservas();
		for (int i = 0; i < reservasJson.length(); i++) {
			JSONObject reservaJson = reservasJson.getJSONObject(i);

			Reserva reserva = GestionReserva.reservaJson2Reserva(reservaJson);

			gestor.guardarReserva(reserva);

		}
		gestor.cerrarDatabase();
	}

	private void guardarDiasCierreReservas(JSONArray diasCierreReservasJson)
			throws JSONException {
		GestionDiaCierreReserva gestor = new GestionDiaCierreReserva(this);
		gestor.borrarDiasCierreReserva();
		for (int i = 0; i < diasCierreReservasJson.length(); i++) {
			JSONObject diaCierreJson = diasCierreReservasJson.getJSONObject(i);

			DiaCierreReserva diaCierreReserva = GestionDiaCierreReserva
					.diaCierreReservaJson2DiaCierreReserva(diaCierreJson);

			gestor.guardarDiaCierreReserva(diaCierreReserva);

		}
		gestor.cerrarDatabase();
	}

	private void guardarTiposPlato(JSONArray tiposPlatoJson)
			throws JSONException {
		GestionTipoPlato gestor = new GestionTipoPlato(this);
		gestor.borrarTiposPlato();
		for (int i = 0; i < tiposPlatoJson.length(); i++) {
			JSONObject tipoPlatoJson = tiposPlatoJson.getJSONObject(i);

			TipoPlato tipoPlato = GestionTipoPlato
					.tipoPlatoJson2TipoPlato(tipoPlatoJson);

			gestor.guardarTipoPlato(tipoPlato);

		}
		gestor.cerrarDatabase();
	}

	private void guardarPlatos(JSONArray platosJson) throws JSONException {
		GestionPlato gestor = new GestionPlato(this);
		gestor.borrarPlatos();
		for (int i = 0; i < platosJson.length(); i++) {
			JSONObject platoJson = platosJson.getJSONObject(i);

			Plato plato = GestionPlato.platoJson2Plato(platoJson);

			gestor.guardarPlato(plato);

		}
		gestor.cerrarDatabase();
	}

	private void guardarMenus(JSONArray menusJson) throws JSONException {
		GestionMenu gestor = new GestionMenu(this);
		gestor.borrarMenus();
		for (int i = 0; i < menusJson.length(); i++) {
			JSONObject menuJson = menusJson.getJSONObject(i);

			MenuLocal menu = GestionMenu.menuJson2Menu(menuJson);

			gestor.guardarMenu(menu);

		}
		gestor.cerrarDatabase();
	}

	private void guardarMenusDia(JSONArray menusDiaJson) throws JSONException {
		GestionMenuDia gestor = new GestionMenuDia(this);
		gestor.borrarMenusDia();
		for (int i = 0; i < menusDiaJson.length(); i++) {
			JSONObject menuDiaJson = menusDiaJson.getJSONObject(i);

			MenuDia menuDia = GestionMenuDia.menuDiaJson2MenuDia(menuDiaJson);

			gestor.guardarMenuDia(menuDia);

		}
		gestor.cerrarDatabase();
	}

	private void guardarTiposComanda(JSONArray tiposComandaJson)
			throws JSONException {
		GestionTipoComanda gestor = new GestionTipoComanda(this);
		gestor.borrarTiposComanda();
		for (int i = 0; i < tiposComandaJson.length(); i++) {
			JSONObject tipoComandaJson = tiposComandaJson.getJSONObject(i);

			TipoComanda tipoComanda = GestionTipoComanda
					.tipoComandaJson2TipoComanda(tipoComandaJson);

			gestor.guardarTipoComanda(tipoComanda);

		}
		gestor.cerrarDatabase();
	}

	private void guardarCamareros(JSONArray camarerosJson) throws JSONException {
		GestionCamarero gestor = new GestionCamarero(this);
		gestor.borrarCamareros();
		for (int i = 0; i < camarerosJson.length(); i++) {
			JSONObject camareroJson = camarerosJson.getJSONObject(i);

			Camarero camarero = GestionCamarero
					.camareroJson2Camarero(camareroJson);

			gestor.guardarCamarero(camarero);

		}
		gestor.cerrarDatabase();
	}

	private void guardarComandas(JSONArray comandasJson, boolean borrarComandas)
			throws JSONException {
		GestionComanda gestor = new GestionComanda(this);
		if (borrarComandas) {
			gestor.borrarComandas();
		}
		for (int i = 0; i < comandasJson.length(); i++) {
			JSONObject comandaJson = comandasJson.getJSONObject(i);

			Comanda comanda = GestionComanda.comandaJson2Comanda(comandaJson);

			gestor.guardarComanda(comanda);

		}
		gestor.cerrarDatabase();
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
				try {
					guardarDatosLocal(respJSON);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

	}

}
