package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.TipoPlato;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTipoPlato {

	public static final String JSON_TIPOS_PLATO = "tiposPlato";
	public static final String JSON_TIPO_PLATO = "tipoPlato";
	public static final String JSON_ID_TIPO_PLATO = "idTipoPlato";
	public static final String JSON_DESCRIPCION = "descripcion";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionTipoPlato(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarTiposPlato() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_TIPOS_PLATO, null, null);
	}

	public long guardarTipoPlato(TipoPlato tipoPlato) {

		long resultado = -1;

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_TP_DESCRIPCION,
				tipoPlato.getDescripcion());
		content.put(LocalSQLite.COLUMN_TP_ID_TIPO_PLATO,
				tipoPlato.getIdTipoPlato());

		resultado = database.insert(LocalSQLite.TABLE_TIPOS_PLATO, null, content);

		return resultado;
	}

	public List<TipoPlato> obtenerTiposPlato() {
		List<TipoPlato> tiposPlato = new ArrayList<TipoPlato>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_TIPOS_PLATO, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			TipoPlato tipoMenu = obtenerTipoPlato(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_TP_ID_TIPO_PLATO)));

			tiposPlato.add(tipoMenu);
		}

		cursor.close();
		return tiposPlato;
	}

	public TipoPlato obtenerTipoPlato(int idTipoPlato) {
		TipoPlato tipoPlato = null;
		Cursor cursor = database
				.query(LocalSQLite.TABLE_TIPOS_PLATO, null,
						LocalSQLite.COLUMN_TP_ID_TIPO_PLATO + " = ?",
						new String[] { Integer.toString(idTipoPlato) }, null,
						null, null);

		while (cursor.moveToNext()) {

			tipoPlato = new TipoPlato(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_TP_ID_TIPO_PLATO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TP_DESCRIPCION)));

		}

		cursor.close();
		return tipoPlato;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static TipoPlato tipoPlatoJson2TipoPlato(JSONObject tipoPlatoJson)
			throws JSONException {

		TipoPlato tipoPlato = new TipoPlato(
				tipoPlatoJson.getInt(JSON_ID_TIPO_PLATO),
				tipoPlatoJson.getString(JSON_DESCRIPCION));

		return tipoPlato;
	}

}
