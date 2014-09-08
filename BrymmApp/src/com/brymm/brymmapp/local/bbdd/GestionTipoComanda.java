package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.TipoComanda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTipoComanda {

	public static final String JSON_TIPOS_COMANDA = "tiposComanda";
	public static final String JSON_TIPO_COMANDA = "tipoComanda";
	public static final String JSON_ID_TIPO_COMANDA = "idTipoComanda";
	public static final String JSON_DESCRIPCION = "descripcion";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionTipoComanda(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarTiposComanda() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_TIPOS_COMANDA, null, null);
	}

	public long guardarTipoComanda(TipoComanda tipoComanda) {

		long resultado = -1;

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_TC_DESCRIPCION,
				tipoComanda.getDescripcion());
		content.put(LocalSQLite.COLUMN_TC_ID_TIPO_COMANDA,
				tipoComanda.getIdTipoComanda());

		resultado = database.insert(LocalSQLite.TABLE_TIPOS_COMANDA, null, content);

		return resultado;
	}

	public List<TipoComanda> obtenerTiposComanda() {
		List<TipoComanda> tiposComanda = new ArrayList<TipoComanda>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_TIPOS_COMANDA, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			TipoComanda tipoComanda = obtenerTipoComanda(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_TC_ID_TIPO_COMANDA)));

			tiposComanda.add(tipoComanda);
		}

		cursor.close();
		return tiposComanda;
	}

	public TipoComanda obtenerTipoComanda(int idTipoComanda) {
		TipoComanda tipoComanda = null;
		Cursor cursor = database
				.query(LocalSQLite.TABLE_TIPOS_COMANDA, null,
						LocalSQLite.COLUMN_TC_ID_TIPO_COMANDA + " = ?",
						new String[] { Integer.toString(idTipoComanda) }, null,
						null, null);

		while (cursor.moveToNext()) {

			tipoComanda = new TipoComanda(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_TC_ID_TIPO_COMANDA)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TC_DESCRIPCION)));

		}

		cursor.close();
		return tipoComanda;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static TipoComanda tipoComandaJson2TipoComanda(JSONObject tipoComandaJson)
			throws JSONException {

		TipoComanda tipoComanda = new TipoComanda(
				tipoComandaJson.getInt(JSON_ID_TIPO_COMANDA),
				tipoComandaJson.getString(JSON_DESCRIPCION));

		return tipoComanda;
	}

}
