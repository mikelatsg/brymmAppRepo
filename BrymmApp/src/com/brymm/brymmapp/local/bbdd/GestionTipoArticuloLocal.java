package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTipoArticuloLocal {

	public static final String JSON_TIPOS_ARTICULO_LOCAL = "tiposArticuloLocal";
	public static final String JSON_TIPO_ARTICULO_LOCAL = "tipoArticuloLocal";
	public static final String JSON_ID_TIPO_ARTICULO = "idTipoArticulo";
	public static final String JSON_TIPO_ARTICULO = "tipoArticulo";
	public static final String JSON_DESCRIPCION = "descripcion";
	public static final String JSON_PRECIO = "precio";
	public static final String JSON_PERSONALIZAR = "personalizar";
	public static final String JSON_ID_TIPO_ARTICULO_LOCAL = "idTipoArticuloLocal";
	public static final String JSON_ID_LOCAL = "idLocal";

	SQLiteDatabase database;
	LocalSQLite openHelper;

	public GestionTipoArticuloLocal(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarTiposArticuloLocal() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_TIPOS_ARTICULO_LOCAL, null, null);
	}

	public void borrarTipoArticuloLocal(int idTipoArticuloLocal) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_TIPOS_ARTICULO_LOCAL,
				LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL + " = ?",
				new String[] { Integer.toString(idTipoArticuloLocal) });
	}

	public void guardarTipoArticuloLocal(TipoArticuloLocal tipoArticuloLocal) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_TIPOS_ARTICULO_LOCAL,
				null, LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL + " = ?",
				new String[] { Integer.toString(tipoArticuloLocal
						.getIdTipoArticuloLocal()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO,
				tipoArticuloLocal.getIdTipoArticulo());
		content.put(LocalSQLite.COLUMN_TAL_PERSONALIZAR,
				tipoArticuloLocal.isPersonalizar());
		content.put(LocalSQLite.COLUMN_TAL_PRECIO,
				tipoArticuloLocal.getPrecio());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL,
					tipoArticuloLocal.getIdTipoArticuloLocal());

			database.insert(LocalSQLite.TABLE_TIPOS_ARTICULO_LOCAL, null,
					content);
		} else {

			database.update(LocalSQLite.TABLE_TIPOS_ARTICULO_LOCAL, content,
					LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL + " = ?",
					new String[] { Integer.toString(tipoArticuloLocal
							.getIdTipoArticuloLocal()) });
		}

		cursor.close();

	}

	public List<TipoArticuloLocal> obtenerTiposArticulo() {
		List<TipoArticuloLocal> tiposArticuloLocal = new ArrayList<TipoArticuloLocal>();

		String sql = "SELECT ta.*,tal." + LocalSQLite.COLUMN_TAL_PRECIO + " ,"
				+ " tal." + LocalSQLite.COLUMN_TAL_PERSONALIZAR + " ,"
				+ " tal." + LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL
				+ " FROM " + LocalSQLite.TABLE_TIPOS_ARTICULO + " ta ,"
				+ LocalSQLite.TABLE_TIPOS_ARTICULO_LOCAL + " tal "
				+ " WHERE ta." + LocalSQLite.COLUMN_TA_ID_TIPO_ARTICULO
				+ " = tal." + LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO
				+ " ORDER BY ta." + LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO;
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.rawQuery(sql, null);

		while (cursor.moveToNext()) {

			TipoArticuloLocal tipoArticuloLocal = obtenerTipoArticuloLocal(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO)));

			tiposArticuloLocal.add(tipoArticuloLocal);
		}

		cursor.close();
		return tiposArticuloLocal;
	}

	public List<TipoArticuloLocal> obtenerTiposArticuloLocalPersonalizables() {
		List<TipoArticuloLocal> tiposArticuloLocal = new ArrayList<TipoArticuloLocal>();

		String sql = "SELECT ta.*,tal." + LocalSQLite.COLUMN_TAL_PRECIO + " ,"
				+ " tal." + LocalSQLite.COLUMN_TAL_PERSONALIZAR + " ,"
				+ " tal." + LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL
				+ " FROM " + LocalSQLite.TABLE_TIPOS_ARTICULO + " ta ,"
				+ LocalSQLite.TABLE_TIPOS_ARTICULO_LOCAL + " tal "
				+ " WHERE ta." + LocalSQLite.COLUMN_TA_ID_TIPO_ARTICULO
				+ " = tal." + LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO
				+ " AND tal." + LocalSQLite.COLUMN_TAL_PERSONALIZAR + " = ?"
				+ " ORDER BY ta." + LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO;
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.rawQuery(sql,
				new String[] { Integer.toString(1) });

		while (cursor.moveToNext()) {

			TipoArticuloLocal tipoArticuloLocal = obtenerTipoArticuloLocal(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL)));

			tiposArticuloLocal.add(tipoArticuloLocal);
		}

		cursor.close();
		return tiposArticuloLocal;
	}

	public TipoArticuloLocal obtenerTipoArticuloLocal(int idTipoArticuloLocal) {
		TipoArticuloLocal tipoArticuloLocal = null;

		String sql = "SELECT ta.*,tal." + LocalSQLite.COLUMN_TAL_PRECIO
				+ " ,tal." + LocalSQLite.COLUMN_TAL_PERSONALIZAR + " ,tal."
				+ LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL + " FROM "
				+ LocalSQLite.TABLE_TIPOS_ARTICULO + " ta ,"
				+ LocalSQLite.TABLE_TIPOS_ARTICULO_LOCAL + " tal "
				+ " WHERE ta." + LocalSQLite.COLUMN_TA_ID_TIPO_ARTICULO
				+ " = tal." + LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO
				+ " AND tal." + LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL
				+ " = ?";
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.rawQuery(sql,
				new String[] { Integer.toString(idTipoArticuloLocal) });

		while (cursor.moveToNext()) {

			tipoArticuloLocal = new TipoArticuloLocal(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TA_TIPO_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TA_DESCRIPCION)),
					(float) cursor.getDouble(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TAL_PRECIO)),
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TAL_PERSONALIZAR)) == 1,
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL)));

		}

		cursor.close();
		return tipoArticuloLocal;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static TipoArticuloLocal tipoArticuloLocalJson2TipoArticuloLocal(
			JSONObject tipoArticuloLocalJson) throws JSONException {

		TipoArticuloLocal tipoArticuloLocal = new TipoArticuloLocal(
				tipoArticuloLocalJson.getInt(JSON_ID_TIPO_ARTICULO),
				tipoArticuloLocalJson.getString(JSON_TIPO_ARTICULO),
				tipoArticuloLocalJson.getString(JSON_DESCRIPCION),
				(float) tipoArticuloLocalJson.getDouble(JSON_PRECIO),
				tipoArticuloLocalJson.getInt(JSON_PERSONALIZAR) == 1,
				tipoArticuloLocalJson.getInt(JSON_ID_TIPO_ARTICULO_LOCAL));

		return tipoArticuloLocal;
	}

}
