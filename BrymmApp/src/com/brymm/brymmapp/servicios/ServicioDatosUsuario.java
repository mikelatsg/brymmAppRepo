package com.brymm.brymmapp.servicios;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.local.bbdd.GestionActualizaciones;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticuloLocal;
import com.brymm.brymmapp.usuario.bbdd.GestionArticuloLocal;
import com.brymm.brymmapp.usuario.bbdd.GestionDireccion;
import com.brymm.brymmapp.usuario.bbdd.GestionHorariosPedido;
import com.brymm.brymmapp.usuario.bbdd.GestionIngredientesLocal;
import com.brymm.brymmapp.usuario.bbdd.GestionLocalFavorito;
import com.brymm.brymmapp.usuario.bbdd.GestionPedidoUsuario;
import com.brymm.brymmapp.usuario.bbdd.GestionReservaUsuario;
import com.brymm.brymmapp.usuario.bbdd.GestionServicioLocal;
import com.brymm.brymmapp.usuario.bbdd.GestionTipoServicio;
import com.brymm.brymmapp.usuario.bbdd.GestionTipoComida;
import com.brymm.brymmapp.usuario.bbdd.GestionTiposArticulo;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocal;
import com.brymm.brymmapp.usuario.pojo.ArticuloPedido;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.HorarioPedido;
import com.brymm.brymmapp.usuario.pojo.IngredienteLocal;
import com.brymm.brymmapp.usuario.pojo.LocalFavorito;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;
import com.brymm.brymmapp.usuario.pojo.ReservaUsuario;
import com.brymm.brymmapp.usuario.pojo.ServicioLocal;
import com.brymm.brymmapp.usuario.pojo.TipoArticulo;
import com.brymm.brymmapp.usuario.pojo.TipoServicio;
import com.brymm.brymmapp.usuario.pojo.TipoComida;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ServicioDatosUsuario extends Service {

	// Campos JSON

	/*
	 * public static final String JSON_ID_DIRECCION_ENVIO =
	 * "id_direccion_envio"; public static final String JSON_NOMBRE_DIRECCION =
	 * "nombre"; public static final String JSON_DIRECCION = "direccion"; public
	 * static final String JSON_POBLACION = "poblacion"; public static final
	 * String JSON_PROVINCIA = "provincia";
	 */

	/*
	 * private static final String JSON_ID_PEDIDO = "idPedido"; private static
	 * final String JSON_ESTADO = "estado"; private static final String
	 * JSON_PRECIO = "precio"; private static final String JSON_FECHA_PEDIDO =
	 * "fechaPedido"; private static final String JSON_OBSERVACIONES =
	 * "observaciones"; private static final String JSON_NOMBRE_LOCAL =
	 * "nombreLocal"; private static final String JSON_MOTIVO_RECHAZO =
	 * "motivoRechazo"; private static final String JSON_FECHA_ENTREGA =
	 * "fechaEntrega"; private static final String JSON_DETALLE_PEDIDO =
	 * "detallePedido"; private static final String JSON_ENVIO_PEDIDO =
	 * "envioPedido";
	 */

	public static final String JSON_NOMBRE_ARTICULO = "articulo";
	public static final String JSON_PRECIO_ARTICULO = "precioArticulo";
	public static final String JSON_CANTIDAD_ARTICULO = "cantidad";
	public static final String JSON_TIPO_ARTICULO = "tipoArticulo";
	public static final String JSON_ARTICULO_PERSONALIZADO = "personalizado";

	public static final String JSON_DETALLE_ARTICULO = "detalleArticulo";
	public static final String JSON_INGREDIENTE = "ingrediente";

	private static final String JSON_TS_TIPOS_SERVICIO = "tiposServicio";
	private static final String JSON_TS_ID_TIPO_SERVICIO = "id_tipo_servicio_local";
	private static final String JSON_TS_SERVICIO = "servicio";
	private static final String JSON_TS_MOSTRAR_BUSCADOR = "mostrar_buscador";

	private static final String JSON_TC_TIPOS_COMIDA = "tiposComida";
	private static final String JSON_TC_ID_TIPO_COMIDA = "id_tipo_comida";
	private static final String JSON_TC_TIPO_COMIDA = "tipo_comida";

	private static final String JSON_IL_INGREDIENTES = "ingredientes";
	private static final String JSON_IL_ID_INGREDIENTE = "id_ingrediente";
	private static final String JSON_IL_ID_LOCAL = "id_local";
	private static final String JSON_IL_INGREDIENTE = "ingrediente";
	private static final String JSON_IL_DESCRIPCION = "descripcion";
	private static final String JSON_IL_PRECIO = "precio";

	private static final String JSON_HP_HORARIO_PEDIDOS = "horarioPedidos";
	private static final String JSON_HP_ID_HORARIO_PEDIDO = "id_horario_pedido";
	private static final String JSON_HP_ID_LOCAL = "id_local";
	private static final String JSON_HP_ID_DIA = "id_dia";
	private static final String JSON_HP_HORA_INICIO = "hora_inicio";
	private static final String JSON_HP_HORA_FIN = "hora_fin";
	private static final String JSON_HP_DIA = "dia";

	private static final String JSON_SL_SERVICIOS = "servicios";
	private static final String JSON_SL_ID_TIPO_SERVICIO = "id_tipo_servicio_local";
	private static final String JSON_SL_ID_SERVICIO_LOCAL = "id_servicio_local";
	private static final String JSON_SL_ID_LOCAL = "id_local";
	private static final String JSON_SL_IMPORTE_MINIMO = "importe_minimo";
	private static final String JSON_SL_PRECIO = "precio";

	// private static final String JSON_TA_TIPOS_ARTICULO = "tiposArticulo";
	private static final String JSON_TA_ID_TIPO_ARTICULO = "id_tipo_articulo";
	private static final String JSON_TA_TIPO_ARTICULO = "tipo_articulo";
	private static final String JSON_TA_DESCRIPCION = "descripcion";
	private static final String JSON_TA_PERSONALIZAR = "personalizar";
	private static final String JSON_TA_ID_TIPO_ARTICULO_LOCAL = "id_tipo_articulo_local";
	private static final String JSON_TA_PRECIO = "precio";

	private static final String JSON_AR_ARTICULOS = "articulos";
	private static final String JSON_AR_ID_ARTICULO_LOCAL = "id_articulo_local";
	private static final String JSON_AR_ID_LOCAL = "id_local";
	private static final String JSON_AR_ID_TIPO_ARTICULO = "id_tipo_articulo";
	private static final String JSON_AR_ARTICULO = "articulo";
	private static final String JSON_AR_DESCRIPCION = "descripcion";
	private static final String JSON_AR_PRECIO = "precio";
	private static final String JSON_AR_TIPO_ARTICULO = "tipo_articulo";

	private static final String JSON_AR_INGREDIENTES = "ingredientes";
	private static final String JSON_AR_INGREDIENTE = "ingrediente";

	private static boolean datosLocalCargados = false;

	private final IBinder binder = new MiBinder();

	public class MiBinder extends Binder {
		public ServicioDatosUsuario getService() {
			return ServicioDatosUsuario.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("MiServicio", "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int idUsuario = intent.getIntExtra(LoginActivity.EXTRA_ID_USUARIO, -1);

		if (idUsuario > 0) {
			// Guardo la fecha de actualización
			GestionActualizaciones ga = new GestionActualizaciones(this);
			ga.guardarActualizacion();
			ga.cerrarDatabase();

			// cargaInicial(idUsuario);
			DatosUsuario du = new DatosUsuario();
			du.execute(idUsuario);
		}
		return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("MiServicio", "onBind");
		return binder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("MiServicio", "onDestroy");
	}

	private void cargaInicial(int idUsuario) {
		JSONObject datosUsuario = obtenerDatosUsuario(idUsuario);
		if (datosUsuario != null) {
			try {
				guardarDatosUsuario(datosUsuario);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void cargarDatosLocal(int idLocal) {
		DatosLocal dl = new DatosLocal();
		dl.execute(idLocal);
	}

	private void obtenerDatosLocal(int idLocal) {
		this.datosLocalCargados = false;
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/locales/datosLocalUsuario/idLocal/" + idLocal
					+ "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */

			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respStr = EntityUtils.toString(resp.getEntity());
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}

		if (respJSON != null) {
			try {
				guardarDatosLocal(respJSON);
				this.datosLocalCargados = true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void guardarDatosLocal(JSONObject datosLocal) throws JSONException {
		Log.d("guardarDatosLocal", "guardarDatosLocal");
		// Se guardan los ingredientes en la bd
		JSONArray ingredientes = datosLocal.getJSONArray(JSON_IL_INGREDIENTES);
		guardarIngredientesLocal(ingredientes);

		// Se guardan los horarios en la bd
		JSONArray horarios = datosLocal.getJSONArray(JSON_HP_HORARIO_PEDIDOS);
		guardarHorariosPedido(horarios);

		// Se guardan los horarios en la bd
		JSONArray servicios = datosLocal.getJSONArray(JSON_SL_SERVICIOS);
		guardarServiciosLocal(servicios);

		// Faltan guardar los tipos_articulos y los articulos

		// Se guardan los tipos articulo en la bd
		JSONArray tiposArticulo = datosLocal
				.getJSONArray(GestionTipoArticuloLocal.JSON_TIPOS_ARTICULO_LOCAL);
		guardarTiposArticulo(tiposArticulo);

		// Se guardan los articulos en la bd
		JSONArray articulosLocal = datosLocal.getJSONArray(JSON_AR_ARTICULOS);
		guardarArticulosLocal(articulosLocal);

	}

	private void guardarIngredientesLocal(JSONArray ingredientes)
			throws JSONException {
		GestionIngredientesLocal gil = new GestionIngredientesLocal(this);
		gil.borrarIngredientesLocal();
		for (int i = 0; i < ingredientes.length(); i++) {
			JSONObject ingredienteJson = ingredientes.getJSONObject(i);
			IngredienteLocal ingrediente = new IngredienteLocal(
					ingredienteJson.getInt(JSON_IL_ID_INGREDIENTE),
					ingredienteJson.getInt(JSON_IL_ID_LOCAL),
					ingredienteJson.getString(JSON_IL_INGREDIENTE),
					ingredienteJson.getString(JSON_IL_DESCRIPCION),
					(float) ingredienteJson.getDouble(JSON_IL_PRECIO));

			gil.guardarIngredienteLocal(ingrediente);

		}
		gil.cerrarDatabase();
	}

	private void guardarHorariosPedido(JSONArray horarios) throws JSONException {
		GestionHorariosPedido ghp = new GestionHorariosPedido(this);
		ghp.borrarHorariosPedido();
		for (int i = 0; i < horarios.length(); i++) {
			JSONObject horarioJson = horarios.getJSONObject(i);
			HorarioPedido horario = new HorarioPedido(
					horarioJson.getInt(JSON_HP_ID_HORARIO_PEDIDO),
					horarioJson.getInt(JSON_HP_ID_LOCAL),
					horarioJson.getInt(JSON_HP_ID_DIA),
					horarioJson.getString(JSON_HP_HORA_INICIO),
					horarioJson.getString(JSON_HP_HORA_FIN),
					horarioJson.getString(JSON_HP_DIA));

			ghp.guardarHorarioPedido(horario);

		}
		ghp.cerrarDatabase();
	}

	private void guardarArticulosLocal(JSONArray articulosJson)
			throws JSONException {
		GestionArticuloLocal gal = new GestionArticuloLocal(this);
		gal.borrarArticulosLocal();
		for (int i = 0; i < articulosJson.length(); i++) {
			JSONObject articuloJson = articulosJson.getJSONObject(i);
			JSONArray ingredientesArticulo = articuloJson
					.getJSONArray(JSON_AR_INGREDIENTES);
			List<String> ingredientes = new ArrayList<String>();
			for (int j = 0; j < ingredientesArticulo.length(); j++) {
				JSONObject ingredienteArticulo = ingredientesArticulo
						.getJSONObject(j);
				String ingrediente = ingredienteArticulo
						.getString(JSON_AR_INGREDIENTE);
				ingredientes.add(ingrediente);
			}

			ArticuloLocal articulo = new ArticuloLocal(
					articuloJson.getInt(JSON_AR_ID_ARTICULO_LOCAL),
					articuloJson.getInt(JSON_AR_ID_LOCAL),
					articuloJson.getInt(JSON_AR_ID_TIPO_ARTICULO),
					articuloJson.getString(JSON_AR_ARTICULO),
					articuloJson.getString(JSON_AR_DESCRIPCION),
					(float) articuloJson.getDouble(JSON_AR_PRECIO),
					articuloJson.getString(JSON_AR_TIPO_ARTICULO), ingredientes);

			gal.guardarArticuloLocal(articulo);
		}
		gal.cerrarDatabase();
	}

	private void guardarTiposArticulo(JSONArray tiposArticulo)
			throws JSONException {
		GestionTiposArticulo gta = new GestionTiposArticulo(this);
		gta.borrarTiposArticulo();
		for (int i = 0; i < tiposArticulo.length(); i++) {
			JSONObject tipoArticuloJson = tiposArticulo.getJSONObject(i);
			TipoArticulo tipoArticulo = new TipoArticulo(
					tipoArticuloJson.getInt(JSON_TA_ID_TIPO_ARTICULO_LOCAL),
					tipoArticuloJson.getInt(JSON_TA_ID_TIPO_ARTICULO),
					tipoArticuloJson.getString(JSON_TA_TIPO_ARTICULO),
					tipoArticuloJson.getString(JSON_TA_DESCRIPCION),
					tipoArticuloJson.getInt(JSON_TA_PERSONALIZAR),
					(float) tipoArticuloJson.getDouble(JSON_TA_PRECIO));

			gta.guardarTipoArticulo(tipoArticulo);

		}
		gta.cerrarDatabase();
	}

	private void guardarServiciosLocal(JSONArray servicios)
			throws JSONException {
		GestionServicioLocal gsl = new GestionServicioLocal(this);
		gsl.borrarServiciosLocal();
		for (int i = 0; i < servicios.length(); i++) {
			JSONObject servicioJson = servicios.getJSONObject(i);
			ServicioLocal servicio = new ServicioLocal(
					servicioJson.getInt(JSON_SL_ID_SERVICIO_LOCAL),
					servicioJson.getInt(JSON_SL_ID_TIPO_SERVICIO),
					servicioJson.getInt(JSON_SL_ID_LOCAL),
					(float) servicioJson.getDouble(JSON_SL_IMPORTE_MINIMO),
					(float) servicioJson.getDouble(JSON_SL_PRECIO));

			gsl.guardarServicioLocal(servicio);

		}
		gsl.cerrarDatabase();
	}

	private JSONObject obtenerDatosUsuario(int idUsuario) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/usuarios/datosUsuario/idUsuario/" + idUsuario
					+ "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */

			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respStr = EntityUtils.toString(resp.getEntity());
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	private void guardarDatosUsuario(JSONObject datosUsuario)
			throws JSONException {
		JSONArray direcciones = datosUsuario
				.getJSONArray(GestionDireccion.JSON_DIRECCIONES);
		if (direcciones.length() > 0) {
			guardarDireccionesUsuario(direcciones);
		}

		// Ultimos pedidos
		JSONArray pedidosJson = datosUsuario
				.getJSONArray(GestionPedidoUsuario.JSON_ULTIMOS_PEDIDOS);
		if (pedidosJson.length() > 0) {
			guardarUltimosPedidos(pedidosJson);
		}

		// Locales favoritos
		JSONArray localesFavoritosJson = datosUsuario
				.getJSONArray(GestionLocalFavorito.JSON_LOCALES_FAVORITOS);
		if (localesFavoritosJson.length() > 0) {
			guardarLocalesFavoritos(localesFavoritosJson);
		}

		// Ultimas reservas
		JSONArray reservasJson = datosUsuario
				.getJSONArray(GestionReservaUsuario.JSON_ULTIMAS_RESERVAS);
		if (reservasJson.length() > 0) {
			guardarUltimasReservas(reservasJson);
		}

		// Tipos servicio
		JSONArray tiposServicioJson = datosUsuario
				.getJSONArray(JSON_TS_TIPOS_SERVICIO);
		guardarTipoServicioLocal(tiposServicioJson);

		// Tipos comida
		JSONArray tiposComidaJson = datosUsuario
				.getJSONArray(JSON_TC_TIPOS_COMIDA);
		guardarTipoComida(tiposComidaJson);

	}

	private void guardarDireccionesUsuario(JSONArray direcciones)
			throws JSONException {
		GestionDireccion gd = new GestionDireccion(this);
		gd.borrarDirecciones();
		for (int i = 0; i < direcciones.length(); i++) {
			JSONObject direccionJson = direcciones.getJSONObject(i);

			Direccion direccion = GestionDireccion
					.direccionJson2Direccion(direccionJson);

			gd.guardarDireccion(direccion);
		}
		gd.cerrarDatabase();
	}

	private void guardarUltimosPedidos(JSONArray pedidosJson)
			throws JSONException {
		GestionPedidoUsuario gpu = new GestionPedidoUsuario(this);
		for (int i = 0; i < pedidosJson.length(); i++) {
			JSONObject pedidoJson = pedidosJson.getJSONObject(i);
			PedidoUsuario pedido = GestionPedidoUsuario
					.pedidoJson2PedidoUsuario(pedidoJson);
			gpu.guardarPedido(pedido);
		}
		gpu.cerrarDatabase();
	}

	private void guardarLocalesFavoritos(JSONArray localesJson)
			throws JSONException {
		GestionLocalFavorito glf = new GestionLocalFavorito(this);
		glf.borrarLocalesFavoritos();
		for (int i = 0; i < localesJson.length(); i++) {
			JSONObject localJson = localesJson.getJSONObject(i);

			LocalFavorito lf = GestionLocalFavorito
					.favoritoJson2LocalFavorito(localJson);

			glf.guardarLocalFavorito(lf);
		}
		glf.cerrarDatabase();
	}

	private void guardarUltimasReservas(JSONArray reservasJson)
			throws JSONException {
		GestionReservaUsuario gru = new GestionReservaUsuario(this);
		gru.borrarReservas();
		for (int i = 0; i < reservasJson.length(); i++) {
			JSONObject reservaJson = reservasJson.getJSONObject(i);
			ReservaUsuario ru = GestionReservaUsuario
					.reservaJson2ReservaUsuario(reservaJson);

			gru.guardarReserva(ru);
		}
		gru.cerrarDatabase();
	}

	private void guardarTipoServicioLocal(JSONArray servicios)
			throws JSONException {
		GestionTipoServicio gsl = new GestionTipoServicio(this);
		gsl.borrarServicios();
		for (int i = 0; i < servicios.length(); i++) {
			JSONObject servicioJson = servicios.getJSONObject(i);
			// Solo se guardan los servicios que se muestran en el buscador.
			if (servicioJson.getInt(JSON_TS_MOSTRAR_BUSCADOR) != 0) {
				TipoServicio sl = new TipoServicio(
						servicioJson.getInt(JSON_TS_ID_TIPO_SERVICIO),
						servicioJson.getString(JSON_TS_SERVICIO));
				gsl.guardarServicio(sl);
			}

		}
		gsl.cerrarDatabase();
	}

	private void guardarTipoComida(JSONArray tiposComidaJson)
			throws JSONException {

		GestionTipoComida gtc = new GestionTipoComida(this);
		gtc.borrarTiposComida();
		for (int i = 0; i < tiposComidaJson.length(); i++) {
			JSONObject tipoComidaJson = tiposComidaJson.getJSONObject(i);
			TipoComida tc = new TipoComida(
					tipoComidaJson.getInt(JSON_TC_ID_TIPO_COMIDA),
					tipoComidaJson.getString(JSON_TC_TIPO_COMIDA));

			gtc.guardarTipoComida(tc);
		}

		gtc.cerrarDatabase();
	}

	public boolean isDatosLocalCargados() {
		return datosLocalCargados;
	}

	public class DatosUsuario extends AsyncTask<Integer, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Integer... params) {
			cargaInicial(params[0]);
			return null;
		}

	}

	public class DatosLocal extends AsyncTask<Integer, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Integer... params) {
			obtenerDatosLocal(params[0]);
			return null;
		}

	}

}
