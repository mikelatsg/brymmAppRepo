package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.TipoArticulo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionArticulo {

	public static final String JSON_ARTICULOS = "articulos";
	public static final String JSON_ARTICULO = "articulo";
	public static final String JSON_ID_ARTICULO = "idArticulo";
	public static final String JSON_TIPO_ARTICULO = "tipoArticulo";
	public static final String JSON_NOMBRE = "nombre";
	public static final String JSON_DESCRIPCION = "descripcion";
	public static final String JSON_PRECIO = "precio";
	public static final String JSON_VALIDO_PEDIDOS = "validoPedidos";
	public static final String JSON_INGREDIENTES = "ingredientes";
	public static final String JSON_CANTIDAD = "cantidad";
	public static final String JSON_ID_LOCAL = "idLocal";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionArticulo(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarArticulos() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_ARTICULOS, null, null);
		database.delete(LocalSQLite.TABLE_INGREDIENTES_ARTICULOS, null, null);
	}

	public void borrarArticulo(int idArticulo) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_ARTICULOS,
				LocalSQLite.COLUMN_IA_ID_ARTICULO + " = ? ",
				new String[] { Integer.toString(idArticulo) });
		database.delete(LocalSQLite.TABLE_INGREDIENTES_ARTICULOS,
				LocalSQLite.COLUMN_IA_ID_ARTICULO + " = ? ",
				new String[] { Integer.toString(idArticulo) });
	}

	public void guardarArticulo(Articulo articulo) {

		/*
		 * if (!database.isOpen()) { database =
		 * openHelper.getWritableDatabase(); }
		 */

		boolean hayError = false;

		database.beginTransaction();

		Cursor cursor = database.query(LocalSQLite.TABLE_ARTICULOS, null,
				LocalSQLite.COLUMN_AR_ID_ARTICULO + " = ?",
				new String[] { Integer.toString(articulo.getIdArticulo()) },
				null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_AR_DESCRIPCION,
				articulo.getDescripcion());
		content.put(LocalSQLite.COLUMN_AR_ID_TIPO_ARTICULO, articulo
				.getTipoArticulo().getIdTipoArticulo());
		content.put(LocalSQLite.COLUMN_AR_NOMBRE, articulo.getNombre());
		content.put(LocalSQLite.COLUMN_AR_PRECIO, articulo.getPrecio());
		content.put(LocalSQLite.COLUMN_AR_VALIDO_PEDIDOS,
				(articulo.isValidoPedidos()) ? 1 : 0);

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_AR_ID_ARTICULO,
					articulo.getIdArticulo());

			if (database.insert(LocalSQLite.TABLE_ARTICULOS, null, content) < 0) {
				hayError = true;
			}
			// Se insertan los ingredientes en la tabla ingredientes_articulo
			for (Ingrediente ingrediente : articulo.getIngredientes()) {
				ContentValues contentIng = new ContentValues();

				contentIng.put(LocalSQLite.COLUMN_IA_ID_ARTICULO,
						articulo.getIdArticulo());
				contentIng.put(LocalSQLite.COLUMN_IA_ID_INGREDIENTE,
						ingrediente.getIdIngrediente());

				if (database.insert(LocalSQLite.TABLE_INGREDIENTES_ARTICULOS,
						null, contentIng) < 0) {
					hayError = true;
				}
			}

		} else {

			if (database
					.update(LocalSQLite.TABLE_ARTICULOS, content,
							LocalSQLite.COLUMN_AR_ID_ARTICULO + " = ?",
							new String[] { Integer.toString(articulo
									.getIdArticulo()) }) < 0) {
				hayError = true;
			}

			// Se borran los ingredientes
			if (database
					.delete(LocalSQLite.TABLE_INGREDIENTES_ARTICULOS,
							LocalSQLite.COLUMN_IA_ID_ARTICULO + " = ? ",
							new String[] { Integer.toString(articulo
									.getIdArticulo()) }) < 0) {
				hayError = true;
			}

			// Se insertan los ingredientes en la tabla ingredientes_articulo
			for (Ingrediente ingrediente : articulo.getIngredientes()) {
				ContentValues contentIng = new ContentValues();

				contentIng.put(LocalSQLite.COLUMN_IA_ID_ARTICULO,
						articulo.getIdArticulo());
				contentIng.put(LocalSQLite.COLUMN_IA_ID_INGREDIENTE,
						ingrediente.getIdIngrediente());

				if (database.insert(LocalSQLite.TABLE_INGREDIENTES_ARTICULOS,
						null, contentIng) < 0) {
					hayError = true;
				}
			}
		}

		cursor.close();

		if (!hayError) {
			database.setTransactionSuccessful();
		}

		database.endTransaction();

	}

	public List<Articulo> obtenerArticulos() {
		List<Articulo> articulos = new ArrayList<Articulo>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_ARTICULOS, null, null,
				null, null, null, null);

		while (cursor.moveToNext()) {

			Articulo articulo = obtenerArticulo(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_AR_ID_ARTICULO)));

			articulos.add(articulo);
		}

		cursor.close();
		return articulos;
	}

	public List<Ingrediente> obtenerIngredientesArticulo(int idArticulo) {
		List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database
				.query(LocalSQLite.TABLE_INGREDIENTES_ARTICULOS, null,
						LocalSQLite.COLUMN_AR_ID_ARTICULO + " = ?",
						new String[] { Integer.toString(idArticulo) }, null,
						null, null);

		GestionIngrediente gi = new GestionIngrediente(context);

		while (cursor.moveToNext()) {
			Ingrediente ingrediente = gi
					.obtenerIngrediente(cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_IA_ID_INGREDIENTE)));

			ingredientes.add(ingrediente);
		}

		gi.cerrarDatabase();

		return ingredientes;
	}

	public Articulo obtenerArticulo(int idArticulo) {
		Articulo articulo = null;
		Cursor cursor = database
				.query(LocalSQLite.TABLE_ARTICULOS, null,
						LocalSQLite.COLUMN_AR_ID_ARTICULO + " = ?",
						new String[] { Integer.toString(idArticulo) }, null,
						null, null);

		while (cursor.moveToNext()) {

			GestionTipoArticulo gta = new GestionTipoArticulo(context);
			TipoArticulo tipoArticulo = gta
					.obtenerTipoArticulo(cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AR_ID_TIPO_ARTICULO)));
			gta.cerrarDatabase();

			List<Ingrediente> ingredientes = obtenerIngredientesArticulo(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AR_ID_ARTICULO)));

			articulo = new Articulo(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AR_ID_ARTICULO)),
					tipoArticulo,
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AR_NOMBRE)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AR_DESCRIPCION)),
					(float) cursor.getDouble(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AR_PRECIO)),
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AR_VALIDO_PEDIDOS)) == 1,
					ingredientes);

		}

		cursor.close();
		return articulo;
	}

	public ArticuloCantidad obtenerArticuloCantidad(int idArticuloPedido) {
		ArticuloCantidad articuloCantidad = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_ARTICULOS_PEDIDOS,
				null, LocalSQLite.COLUMN_AP_ID_ARTICULO_PEDIDO + " = ?",
				new String[] { Integer.toString(idArticuloPedido) }, null,
				null, null);

		while (cursor.moveToNext()) {

			// Se comprueba si se trata de un articulo personalizado
			if (cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_AP_ID_ARTICULO)) != 0) {
				Articulo articulo = obtenerArticulo(cursor.getInt(cursor
						.getColumnIndex(LocalSQLite.COLUMN_AP_ID_ARTICULO)));

				articuloCantidad = new ArticuloCantidad(
						articulo,
						cursor.getInt(cursor
								.getColumnIndex(LocalSQLite.COLUMN_AP_CANTIDAD)));
			} else {

				Cursor cursorArtPer = database.query(
						LocalSQLite.TABLE_ARTICULOS_PERSONALIZADOS, null,
						LocalSQLite.COLUMN_APR_ID_ARTICULO_PEDIDO + " = ?",
						new String[] { Integer.toString(idArticuloPedido) },
						null, null, null);

				while (cursorArtPer.moveToNext()) {
					GestionTipoArticulo gta = new GestionTipoArticulo(context);
					TipoArticulo tipoArticulo = gta
							.obtenerTipoArticulo(cursorArtPer.getInt(cursorArtPer
									.getColumnIndex(LocalSQLite.COLUMN_APR_ID_TIPO_ARTICULO)));
					gta.cerrarDatabase();

					String nombre = cursorArtPer.getString(cursorArtPer
							.getColumnIndex(LocalSQLite.COLUMN_APR_NOMBRE));

					Float precio = cursorArtPer.getFloat(cursorArtPer
							.getColumnIndex(LocalSQLite.COLUMN_APR_PRECIO));

					cursorArtPer.close();

					Cursor cursorDetArtPer = database
							.query(LocalSQLite.TABLE_DETALLE_ARTICULOS_PERSONALIZADOS,
									null,
									LocalSQLite.COLUMN_DAP_ID_ARTICULO_PEDIDO
											+ " = ?", new String[] { Integer
											.toString(idArticuloPedido) },
									null, null, null);

					List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

					while (cursorDetArtPer.moveToNext()) {
						GestionIngrediente gestor = new GestionIngrediente(
								context);
						Ingrediente ingrediente = gestor
								.obtenerIngrediente(cursorDetArtPer.getInt(cursor
										.getColumnIndex(LocalSQLite.COLUMN_AP_ID_ARTICULO)));
						gestor.cerrarDatabase();

						ingredientes.add(ingrediente);
					}

					cursorDetArtPer.close();

					Articulo articulo = new Articulo(
							cursor.getInt(cursor
									.getColumnIndex(LocalSQLite.COLUMN_AP_ID_ARTICULO)),
							tipoArticulo, nombre, nombre, precio, false,
							ingredientes);

					articuloCantidad = new ArticuloCantidad(
							articulo,
							cursor.getInt(cursor
									.getColumnIndex(LocalSQLite.COLUMN_AP_CANTIDAD)));
				}
			}

		}

		cursor.close();
		return articuloCantidad;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Articulo articuloJson2Articulo(JSONObject articuloJson)
			throws JSONException {

		TipoArticulo tipoArticulo = GestionTipoArticulo
				.tipoArticuloJson2TipoArticulo(articuloJson
						.getJSONObject(JSON_TIPO_ARTICULO));

		List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

		for (int i = 0; i < articuloJson.getJSONArray(JSON_INGREDIENTES)
				.length(); i++) {
			JSONObject ingredienteJson = articuloJson.getJSONArray(
					JSON_INGREDIENTES).getJSONObject(i);

			Ingrediente ingrediente = GestionIngrediente
					.ingredienteJson2Ingrediente(ingredienteJson);

			ingredientes.add(ingrediente);

		}

		Articulo articulo = new Articulo(articuloJson.getInt(JSON_ID_ARTICULO),
				tipoArticulo, articuloJson.getString(JSON_NOMBRE),
				articuloJson.getString(JSON_DESCRIPCION),
				(float) articuloJson.getDouble(JSON_PRECIO),
				articuloJson.getInt(JSON_VALIDO_PEDIDOS) == 1, ingredientes);

		return articulo;
	}

	public static ArticuloCantidad articuloCantidadJson2ArticuloCantidad(
			JSONObject articuloCantidadJson) throws JSONException {

		TipoArticulo tipoArticulo = GestionTipoArticulo
				.tipoArticuloJson2TipoArticulo(articuloCantidadJson
						.getJSONObject(JSON_TIPO_ARTICULO));

		List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

		for (int i = 0; i < articuloCantidadJson
				.getJSONArray(JSON_INGREDIENTES).length(); i++) {
			JSONObject ingredienteJson = articuloCantidadJson.getJSONArray(
					JSON_INGREDIENTES).getJSONObject(i);

			Ingrediente ingrediente = GestionIngrediente
					.ingredienteJson2Ingrediente(ingredienteJson);

			ingredientes.add(ingrediente);

		}

		Articulo articulo = new Articulo(
				articuloCantidadJson.getInt(JSON_ID_ARTICULO), tipoArticulo,
				articuloCantidadJson.getString(JSON_NOMBRE),
				articuloCantidadJson.getString(JSON_DESCRIPCION),
				(float) articuloCantidadJson.getDouble(JSON_PRECIO),
				articuloCantidadJson.getInt(JSON_VALIDO_PEDIDOS) == 1,
				ingredientes);

		ArticuloCantidad articuloCantidad = new ArticuloCantidad(articulo,
				articuloCantidadJson.getInt(JSON_CANTIDAD));

		return articuloCantidad;
	}

}
