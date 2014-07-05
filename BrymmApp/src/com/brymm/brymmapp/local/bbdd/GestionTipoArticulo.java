package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.TipoArticulo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTipoArticulo {

	public static final String JSON_TIPOS_ARTICULO = "tiposArticulo";
	public static final String JSON_ID_TIPO_ARTICULO = "idTipoArticulo";
	public static final String JSON_TIPO_ARTICULO = "tipoArticulo";
	public static final String JSON_DESCRIPCION = "descripcion";

	SQLiteDatabase database;
	LocalSQLite openHelper;

	public GestionTipoArticulo(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarTiposArticulo() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_TIPOS_ARTICULO, null, null);
	}

	public void guardarTipoArticulo(TipoArticulo tipoArticulo) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_TA_ID_TIPO_ARTICULO,
				tipoArticulo.getIdTipoArticulo());
		content.put(LocalSQLite.COLUMN_TA_TIPO_ARTICULO,
				tipoArticulo.getTipoArticulo());
		content.put(LocalSQLite.COLUMN_TA_DESCRIPCION,
				tipoArticulo.getDescripcion());

		database.insert(LocalSQLite.TABLE_TIPOS_ARTICULO, null, content);

	}

	public List<TipoArticulo> obtenerTiposArticulo() {
		List<TipoArticulo> tiposArticulo = new ArrayList<TipoArticulo>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_TIPOS_ARTICULO, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			TipoArticulo tipoArticulo = obtenerTipoArticulo(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TA_ID_TIPO_ARTICULO)));

			tiposArticulo.add(tipoArticulo);
		}

		cursor.close();
		return tiposArticulo;
	}

	public TipoArticulo obtenerTipoArticulo(int idTipoArticulo) {
		TipoArticulo tipoArticulo = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_TIPOS_ARTICULO, null,
				LocalSQLite.COLUMN_TA_ID_TIPO_ARTICULO + " = ?",
				new String[] { Integer.toString(idTipoArticulo) }, null, null,
				null);

		while (cursor.moveToNext()) {

			tipoArticulo = new TipoArticulo(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TA_ID_TIPO_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TA_TIPO_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TA_DESCRIPCION)));

		}

		cursor.close();
		return tipoArticulo;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static TipoArticulo tipoArticuloJson2TipoArticulo(
			JSONObject tipoArticuloJson) throws JSONException {

		TipoArticulo tipoArticulo = new TipoArticulo(
				tipoArticuloJson.getInt(JSON_ID_TIPO_ARTICULO),
				tipoArticuloJson.getString(JSON_TIPO_ARTICULO),
				tipoArticuloJson.getString(JSON_DESCRIPCION));

		return tipoArticulo;
	}

}
