package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.servicios.ServicioDatosUsuario;
import com.brymm.brymmapp.usuario.pojo.ArticuloPedido;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GestionPedidoUsuario {

	public static final String JSON_PEDIDO = "pedido";
	public static final String JSON_ID_PEDIDO = "idPedido";
	public static final String JSON_ESTADO = "estado";
	public static final String JSON_PRECIO = "precio";	
	public static final String JSON_FECHA_PEDIDO = "fechaPedido";
	public static final String JSON_OBSERVACIONES = "observaciones";
	public static final String JSON_NOMBRE_LOCAL = "nombreLocal";
	public static final String JSON_MOTIVO_RECHAZO = "motivoRechazo";
	public static final String JSON_FECHA_ENTREGA = "fechaEntrega";
	public static final String JSON_DETALLE_PEDIDO = "detallePedido";
	public static final String JSON_ENVIO_PEDIDO = "envioPedido";
	public static final String JSON_ULTIMOS_PEDIDOS = "ultimosPedidos";

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;
	Context context;

	public GestionPedidoUsuario(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void guardarPedido(PedidoUsuario pedido) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		// Se comprueba si existe el registro, si existe se modifica, sino se
		// inserta
		Cursor cursor = database.query(UsuarioSQLiteOpenHelper.TABLE_PEDIDOS,
				null, UsuarioSQLiteOpenHelper.COLUMN_ID_PEDIDO + " = ?",
				new String[] { Integer.toString(pedido.getIdPedido()) }, null,
				null, null);
		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_ESTADO, pedido.getEstado());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_PRECIO, pedido.getPrecio());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_FECHA_PEDIDO,
				pedido.getFechaPedido());
		int idDireccion = 0;
		if (pedido.getDireccionEnvio() != null) {
			idDireccion = pedido.getDireccionEnvio().getIdDireccion();
		}
		content.put(UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION_ENVIO,
				idDireccion);
		content.put(UsuarioSQLiteOpenHelper.COLUMN_OBSERVACIONES,
				pedido.getObservaciones());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_NOMBRE_LOCAL,
				pedido.getNombreLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_MOTIVO_RECHAZO,
				pedido.getMotivoRechazo());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_FECHA_ENTREGA,
				pedido.getFechaEntrega());

		if (!cursor.moveToFirst()) {
			content.put(UsuarioSQLiteOpenHelper.COLUMN_ID_PEDIDO,
					pedido.getIdPedido());

			database.beginTransaction();

			long resultado = database.insert(
					UsuarioSQLiteOpenHelper.TABLE_PEDIDOS, null, content);

			boolean errorInsertandoArticulo = false;
			if (resultado > 0) {
				ArrayList<ArticuloPedido> articulos = pedido.getArticulos();
				for (ArticuloPedido articulo : articulos) {
					long resultadoArticulo = guardarDetallePedido(articulo,
							pedido.getIdPedido());
					if (resultadoArticulo < 0) {
						errorInsertandoArticulo = true;
					}
				}
			}

			if (resultado > 0 && !errorInsertandoArticulo) {
				database.setTransactionSuccessful();
			}

			database.endTransaction();
		} else {

			database.update(UsuarioSQLiteOpenHelper.TABLE_PEDIDOS, content,
					UsuarioSQLiteOpenHelper.COLUMN_ID_PEDIDO + " = ?",
					new String[] { Integer.toString(pedido.getIdPedido()) });
		}

		cursor.close();

	}

	private long guardarDetallePedido(ArticuloPedido articulo, int idPedido) {
		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_AP_ID_PEDIDO, idPedido);
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AP_ARTICULO,
				articulo.getNombre());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AP_PRECIO_ARTICULO,
				articulo.getPrecio());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AP_CANTIDAD,
				articulo.getCantidad());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AP_TIPO_ARTICULO,
				articulo.getTipoArticulo());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AP_PERSONALIZADO,
				articulo.getPersonalizado());

		long idArticulo = database.insert(
				UsuarioSQLiteOpenHelper.TABLE_ARTICULOS_PEDIDO, null, content);

		boolean errorInsertandoIngrediente = false;
		if (idArticulo > 0) {
			content = new ContentValues();

			ArrayList<String> ingredientes = (ArrayList<String>) articulo
					.getIngredientes();

			for (String ingrediente : ingredientes) {

				content.put(
						UsuarioSQLiteOpenHelper.COLUMN_DAP_ID_ARTICULO_PEDIDO,
						idArticulo);
				content.put(UsuarioSQLiteOpenHelper.COLUMN_DAP_INGREDIENTE,
						ingrediente);

				long idIngrediente = database.insert(
						UsuarioSQLiteOpenHelper.TABLE_DETALLE_ARTICULO_PEDIDO,
						null, content);

				if (idIngrediente < 0) {
					errorInsertandoIngrediente = true;
				}
			}

		} else {
			return idArticulo;
		}

		if (!errorInsertandoIngrediente) {
			return idArticulo;
		}
		return -1;

	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public List<PedidoUsuario> obtenerPedidosUsuario() {
		List<PedidoUsuario> pedidos = new ArrayList<PedidoUsuario>();
		/*
		 * String sql = "SELECT p.*, d.direccion, d.poblacion FROM " +
		 * UsuarioSQLiteOpenHelper.TABLE_DIRECCIONES + " d ," +
		 * UsuarioSQLiteOpenHelper.TABLE_PEDIDOS + " p" + " WHERE p." +
		 * UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION_ENVIO + " = d." +
		 * UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION + " ORDER BY p." +
		 * UsuarioSQLiteOpenHelper.COLUMN_ID_PEDIDO + " DESC";
		 */

		Cursor cursor = database.query(UsuarioSQLiteOpenHelper.TABLE_PEDIDOS,
				null, null, null, null, null,
				UsuarioSQLiteOpenHelper.COLUMN_ID_PEDIDO);

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		// Cursor cursor = database.rawQuery(sql, null);

		while (cursor.moveToNext()) {
			/*
			 * String sqlArticulos = "SELECT ap.* FROM " +
			 * UsuarioSQLiteOpenHelper.TABLE_ARTICULOS_PEDIDO + " ap " +
			 * "WHERE ap." + UsuarioSQLiteOpenHelper.COLUMN_AP_ID_PEDIDO + " = "
			 * + cursor.getInt(cursor
			 * .getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_ID_PEDIDO)) +
			 * "ORDER BY ap." +
			 * UsuarioSQLiteOpenHelper.COLUMN_AP_ID_ARTICULO_PEDIDO;
			 */

			Cursor cursorArticulos = database
					.query(UsuarioSQLiteOpenHelper.TABLE_ARTICULOS_PEDIDO,
							null,
							UsuarioSQLiteOpenHelper.COLUMN_AP_ID_PEDIDO
									+ " = ?",
							new String[] { Integer.toString(cursor.getInt(cursor
									.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_ID_PEDIDO))) },
							null,
							null,
							UsuarioSQLiteOpenHelper.COLUMN_AP_ID_ARTICULO_PEDIDO);

			// Cursor cursorArticulos = database.rawQuery(sqlArticulos, null);
			List<ArticuloPedido> articulos = new ArrayList<ArticuloPedido>();
			while (cursorArticulos.moveToNext()) {

				String sqlIngredientes = "SELECT dap.* FROM "
						+ UsuarioSQLiteOpenHelper.TABLE_DETALLE_ARTICULO_PEDIDO
						+ " dap "
						+ "WHERE dap."
						+ UsuarioSQLiteOpenHelper.COLUMN_DAP_ID_ARTICULO_PEDIDO
						+ " = "
						+ cursorArticulos
								.getInt(cursorArticulos
										.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AP_ID_ARTICULO_PEDIDO))
						+ " ORDER BY dap."
						+ UsuarioSQLiteOpenHelper.COLUMN_DAP_ID_INGREDIENTE;

				Cursor cursorIngredientes = database.rawQuery(sqlIngredientes,
						null);
				List<String> ingredientes = new ArrayList<String>();
				while (cursorIngredientes.moveToNext()) {
					ingredientes
							.add(cursorIngredientes.getString(cursorIngredientes
									.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_DAP_INGREDIENTE)));
				}
				cursorIngredientes.close();

				ArticuloPedido articulo = new ArticuloPedido(
						cursorArticulos
								.getString(cursorArticulos
										.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AP_ARTICULO)),
						cursorArticulos.getFloat(cursorArticulos
								.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AP_PRECIO_ARTICULO)),
						cursorArticulos.getInt(cursorArticulos
								.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AP_CANTIDAD)),
						cursorArticulos.getString(cursorArticulos
								.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AP_TIPO_ARTICULO)),
						cursorArticulos.getInt(cursorArticulos
								.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AP_PERSONALIZADO)) != 0,
						(ArrayList<String>) ingredientes);

				articulos.add(articulo);
			}
			cursorArticulos.close();

			Direccion direccion = null;
			if (cursor
					.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION_ENVIO)) > 0) {
				GestionDireccion gd = new GestionDireccion(context);
				direccion = gd
						.obtenerDireccionUsuario(cursor.getInt(cursor
								.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION_ENVIO)));
				gd.cerrarDatabase();
			}

			PedidoUsuario pedido = new PedidoUsuario(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_ID_PEDIDO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_ESTADO)),
					cursor.getFloat(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_PRECIO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_FECHA_PEDIDO)),
					direccion,
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_OBSERVACIONES)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_NOMBRE_LOCAL)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_MOTIVO_RECHAZO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_FECHA_ENTREGA)),
					(ArrayList<ArticuloPedido>) articulos);

			pedidos.add(pedido);
		}

		cursor.close();
		return pedidos;
	}

	public static PedidoUsuario pedidoJson2PedidoUsuario(JSONObject pedidoJson)
			throws JSONException {

		JSONArray detallesPedido = pedidoJson.getJSONArray(JSON_DETALLE_PEDIDO);
		ArrayList<ArticuloPedido> articulos = new ArrayList<ArticuloPedido>();
		for (int j = 0; j < detallesPedido.length(); j++) {
			JSONObject detallePedido = detallesPedido.getJSONObject(j);
			JSONArray detallesArticulo = detallePedido
					.getJSONArray(ServicioDatosUsuario.JSON_DETALLE_ARTICULO);
			ArrayList<String> ingredientes = new ArrayList<String>();
			for (int k = 0; k < detallesArticulo.length(); k++) {
				JSONObject detalleArticulo = detallesArticulo.getJSONObject(k);
				String ingrediente = detalleArticulo
						.getString(ServicioDatosUsuario.JSON_INGREDIENTE);
				ingredientes.add(ingrediente);
			}

			ArticuloPedido articulo = new ArticuloPedido(
					detallePedido
							.getString(ServicioDatosUsuario.JSON_NOMBRE_ARTICULO),
					(float) detallePedido
							.getDouble(ServicioDatosUsuario.JSON_PRECIO_ARTICULO),
					detallePedido
							.getInt(ServicioDatosUsuario.JSON_CANTIDAD_ARTICULO),
					detallePedido
							.getString(ServicioDatosUsuario.JSON_TIPO_ARTICULO),
					detallePedido
							.getInt(ServicioDatosUsuario.JSON_ARTICULO_PERSONALIZADO) != 0,
					ingredientes);

			articulos.add(articulo);
		}

		JSONObject direccionJson = null;
		Direccion direccion = null;
		if (pedidoJson.getInt(JSON_ENVIO_PEDIDO) != 0) {
			direccionJson = new JSONObject(
					pedidoJson.getString(GestionDireccion.JSON_DIRECCION));
			direccion = new Direccion(
					direccionJson
							.getInt(GestionDireccion.JSON_ID_DIRECCION_ENVIO),
					direccionJson
							.getString(GestionDireccion.JSON_NOMBRE_DIRECCION),
					direccionJson.getString(GestionDireccion.JSON_DIRECCION),
					direccionJson.getString(GestionDireccion.JSON_POBLACION),
					GestionDireccion.JSON_PROVINCIA);
		}

		PedidoUsuario pedido = new PedidoUsuario(
				pedidoJson.getInt(JSON_ID_PEDIDO),
				pedidoJson.getString(JSON_ESTADO),
				(float) pedidoJson.getDouble(JSON_PRECIO),
				pedidoJson.getString(JSON_FECHA_PEDIDO), direccion,
				pedidoJson.getString(JSON_OBSERVACIONES),
				pedidoJson.getString(JSON_NOMBRE_LOCAL),
				pedidoJson.getString(JSON_MOTIVO_RECHAZO),
				pedidoJson.getString(JSON_FECHA_ENTREGA), articulos);
		return pedido;
	}
}
