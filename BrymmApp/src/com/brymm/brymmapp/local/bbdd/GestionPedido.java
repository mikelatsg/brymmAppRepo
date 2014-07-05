package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.Direccion;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.Pedido;
import com.brymm.brymmapp.local.pojo.Usuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionPedido {

	public static final String JSON_PEDIDOS_PENDIENTES = "pedidosPendientes";
	public static final String JSON_PEDIDOS_ACEPTADOS = "pedidosAceptados";
	public static final String JSON_PEDIDOS_RECHAZADOS = "pedidosRechazados";
	public static final String JSON_PEDIDOS_TERMINADOS = "pedidosTerminados";
	public static final String JSON_ID_PEDIDO = "idPedido";
	public static final String JSON_USUARIO = "usuario";
	public static final String JSON_FECHA = "fecha";
	public static final String JSON_FECHA_ENTREGA = "fechaEntrega";
	public static final String JSON_ESTADO = "estado";
	public static final String JSON_PRECIO = "precio";
	public static final String JSON_OBSERVACIONES = "observaciones";
	public static final String JSON_DIRECCION = "direccion";
	public static final String JSON_MOTIVO_RECHAZO = "motivoRechazo";
	public static final String JSON_ARTICULOS = "articulos";
	public static final String JSON_PEDIDO = "pedido";

	public static final String ESTADO_PENDIENTE = "P";
	public static final String ESTADO_ACEPTADO = "A";
	public static final String ESTADO_RECHAZADO = "R";
	public static final String ESTADO_TERMINADO = "T";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionPedido(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarPedidos() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_PEDIDOS, null, null);
		database.delete(LocalSQLite.TABLE_ARTICULOS_PEDIDOS, null, null);
		database.delete(LocalSQLite.TABLE_DIRECCIONES, null, null);
		database.delete(LocalSQLite.TABLE_DETALLE_ARTICULOS_PERSONALIZADOS, null, null);
		database.delete(LocalSQLite.TABLE_ARTICULOS_PERSONALIZADOS, null, null);
	}

	public void guardarPedido(Pedido pedido) {

		boolean hayError = false;

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		database.beginTransaction();

		Cursor cursor = database.query(LocalSQLite.TABLE_PEDIDOS, null,
				LocalSQLite.COLUMN_PD_ID_PEDIDO + " = ?",
				new String[] { Integer.toString(pedido.getIdPedido()) }, null,
				null, null);

		ContentValues content = new ContentValues();

		// Se comprueba si hay direccion
		int idDireccion = 0;
		if (pedido.getDireccion() != null) {
			idDireccion = pedido.getDireccion().getIdDireccion();
		}

		content.put(LocalSQLite.COLUMN_PD_ESTADO, pedido.getEstado());
		content.put(LocalSQLite.COLUMN_PD_FECHA, pedido.getFecha());
		content.put(LocalSQLite.COLUMN_PD_FECHA_ENTREGA,
				pedido.getFechaEntrega());
		content.put(LocalSQLite.COLUMN_PD_ID_DIRECCION, idDireccion);
		content.put(LocalSQLite.COLUMN_PD_ID_USUARIO, pedido.getUsuario()
				.getIdUsuario());
		content.put(LocalSQLite.COLUMN_PD_MOTIVO_RECHAZO,
				pedido.getMotivoRechazo());
		content.put(LocalSQLite.COLUMN_PD_OBSERVACIONES,
				pedido.getObservaciones());
		content.put(LocalSQLite.COLUMN_PD_PRECIO, pedido.getPrecio());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_PD_ID_PEDIDO, pedido.getIdPedido());

			if (database.insert(LocalSQLite.TABLE_PEDIDOS, null, content) < 0) {
				hayError = true;
			}

			// Se insertan los ingredientes en la tabla ingredientes_articulo
			for (ArticuloCantidad articulo : pedido.getArticulos()) {
				ContentValues contentArt = new ContentValues();

				contentArt.put(LocalSQLite.COLUMN_AP_ID_ARTICULO,
						articulo.getIdArticulo());
				contentArt.put(LocalSQLite.COLUMN_AP_ID_PEDIDO,
						pedido.getIdPedido());
				contentArt.put(LocalSQLite.COLUMN_AP_CANTIDAD,
						articulo.getCantidad());

				long $idArticuloPedido;

				$idArticuloPedido = database.insert(
						LocalSQLite.TABLE_ARTICULOS_PEDIDOS, null, contentArt);

				// Se comprueba si se trata de un articulo personalizado
				if (articulo.getIdArticulo() == 0) {

					ContentValues contentArtPer = new ContentValues();

					contentArtPer.put(
							LocalSQLite.COLUMN_APR_ID_ARTICULO_PEDIDO,
							$idArticuloPedido);
					contentArtPer.put(LocalSQLite.COLUMN_APR_NOMBRE,
							articulo.getNombre());
					contentArtPer.put(LocalSQLite.COLUMN_APR_PRECIO,
							articulo.getPrecio());
					contentArtPer.put(LocalSQLite.COLUMN_APR_ID_TIPO_ARTICULO,
							articulo.getTipoArticulo().getIdTipoArticulo());

					database.insert(LocalSQLite.TABLE_ARTICULOS_PERSONALIZADOS,
							null, contentArtPer);

					for (Ingrediente ingrediente : articulo.getIngredientes()) {
						ContentValues contentDetArtPer = new ContentValues();

						contentDetArtPer.put(
								LocalSQLite.COLUMN_DAP_ID_ARTICULO_PEDIDO,
								$idArticuloPedido);
						contentDetArtPer.put(
								LocalSQLite.COLUMN_DAP_ID_INGREDIENTE,
								ingrediente.getIdIngrediente());

						database.insert(
								LocalSQLite.TABLE_DETALLE_ARTICULOS_PERSONALIZADOS,
								null, contentDetArtPer);
					}

				}

			}

			// Se inserta el usuario
			Usuario usuario = pedido.getUsuario();

			// GestionUsuario gu = new GestionUsuario(this.context);
			GestionUsuario gu = new GestionUsuario(this.database);
			if (gu.guardarUsuario(usuario) < 0) {
				hayError = true;
			}
			// gu.cerrarDatabase();

			// Se inserta la direccion si existe
			Direccion direccion = pedido.getDireccion();
			if (direccion != null) {
				GestionDireccion gd = new GestionDireccion(this.database);
				if (gd.guardarDireccion(direccion) < 0) {
					hayError = true;
				}
				gd.cerrarDatabase();
			}

		} else {

			// Si existe el pedido se hace update
			database.update(LocalSQLite.TABLE_PEDIDOS, content,
					LocalSQLite.COLUMN_PD_ID_PEDIDO + " = ?",
					new String[] { Integer.toString(pedido.getIdPedido()) });

		}

		if (!hayError) {
			database.setTransactionSuccessful();
		}

		database.endTransaction();

		cursor.close();

	}

	public List<Pedido> obtenerPedidos(String estado) {
		List<Pedido> pedidos = new ArrayList<Pedido>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_PEDIDOS, null,
				LocalSQLite.COLUMN_PD_ESTADO + " = ?", new String[] { estado },
				null, null, null);

		while (cursor.moveToNext()) {

			Pedido pedido = obtenerPedido(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_PD_ID_PEDIDO)));

			pedidos.add(pedido);
		}

		cursor.close();
		return pedidos;
	}

	public List<ArticuloCantidad> obtenerArticulosPedido(int idPedido) {
		List<ArticuloCantidad> articulos = new ArrayList<ArticuloCantidad>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_ARTICULOS_PEDIDOS,
				null, LocalSQLite.COLUMN_AP_ID_PEDIDO + " = ?",
				new String[] { Integer.toString(idPedido) }, null, null, null);

		GestionArticulo ga = new GestionArticulo(context);

		while (cursor.moveToNext()) {
			// Se crean los articulos del pedido (articuloCantidad)
			ArticuloCantidad articuloCantidad = ga
					.obtenerArticuloCantidad(cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AP_ID_ARTICULO_PEDIDO)));

			articulos.add(articuloCantidad);
		}

		ga.cerrarDatabase();

		return articulos;
	}

	public Pedido obtenerPedido(int idPedido) {
		Pedido pedido = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_PEDIDOS, null,
				LocalSQLite.COLUMN_PD_ID_PEDIDO + " = ?",
				new String[] { Integer.toString(idPedido) }, null, null, null);

		while (cursor.moveToNext()) {

			// Se obtiene el usuario
			GestionUsuario gu = new GestionUsuario(context);
			Usuario usuario = gu.obtenerUsuario(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_PD_ID_USUARIO)));
			gu.cerrarDatabase();

			// Se obtiene la direccion
			GestionDireccion gd = new GestionDireccion(context);
			Direccion direccion = gd.obtenerDireccion(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_PD_ID_DIRECCION)));
			gd.cerrarDatabase();

			// Se obtienen los articulos del pedido
			List<ArticuloCantidad> articulos = obtenerArticulosPedido(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PD_ID_PEDIDO)));

			pedido = new Pedido(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PD_ID_PEDIDO)),
					usuario,
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PD_FECHA)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PD_FECHA_ENTREGA)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PD_ESTADO)),
					(float) cursor.getDouble(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PD_PRECIO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PD_OBSERVACIONES)),
					direccion,
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PD_MOTIVO_RECHAZO)),
					articulos);

		}

		cursor.close();
		return pedido;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Pedido pedidoJson2Pedido(JSONObject pedidoJson)
			throws JSONException {

		// Se obtiene el usuario a partir de su json
		Usuario usuario = GestionUsuario.usuarioJson2Usuario(pedidoJson
				.getJSONObject(JSON_USUARIO));

		// Se obtiene la direccion a partir de su json
		Direccion direccion = null;

		if (!pedidoJson.getString(JSON_DIRECCION).equals("null")) {

			direccion = GestionDireccion.direccionJson2Direccion(pedidoJson
					.getJSONObject(JSON_DIRECCION));
		}

		List<ArticuloCantidad> articulos = new ArrayList<ArticuloCantidad>();

		for (int i = 0; i < pedidoJson.getJSONArray(JSON_ARTICULOS).length(); i++) {
			JSONObject articuloCantidadJson = pedidoJson.getJSONArray(
					JSON_ARTICULOS).getJSONObject(i);

			ArticuloCantidad articuloCantidad = GestionArticulo
					.articuloCantidadJson2ArticuloCantidad(articuloCantidadJson);

			articulos.add(articuloCantidad);

		}

		Pedido pedido = new Pedido(pedidoJson.getInt(JSON_ID_PEDIDO), usuario,
				pedidoJson.getString(JSON_FECHA),
				pedidoJson.getString(JSON_FECHA_ENTREGA),
				pedidoJson.getString(JSON_ESTADO),
				(float) pedidoJson.getDouble(JSON_PRECIO),
				pedidoJson.getString(JSON_OBSERVACIONES), direccion,
				pedidoJson.getString(JSON_MOTIVO_RECHAZO), articulos);

		return pedido;
	}

}
