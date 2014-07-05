package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Mesa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionMesa {

	public static final String JSON_MESAS = "mesas";
	public static final String JSON_MESA = "mesa";
	public static final String JSON_ID_MESA = "idMesa";
	public static final String JSON_NOMBRE = "nombre";
	public static final String JSON_CAPACIDAD = "capacidad";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionMesa(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarMesas() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_MESAS, null, null);
	}

	public void borrarMesa(int idMesa) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_MESAS, LocalSQLite.COLUMN_MS_ID_MESA
				+ " = ?", new String[] { Integer.toString(idMesa) });
	}

	public long guardarMesa(Mesa mesa) {

		long resultado = -1;

		Cursor cursor = database.query(LocalSQLite.TABLE_MESAS, null,
				LocalSQLite.COLUMN_MS_ID_MESA + " = ?",
				new String[] { Integer.toString(mesa.getIdMesa()) }, null,
				null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_MS_CAPACIDAD, mesa.getCapacidad());
		content.put(LocalSQLite.COLUMN_MS_NOMBRE, mesa.getNombre());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_MS_ID_MESA, mesa.getIdMesa());

			resultado = database.insert(LocalSQLite.TABLE_MESAS, null, content);
		} else {

			resultado = database.update(LocalSQLite.TABLE_MESAS, content,
					LocalSQLite.COLUMN_MS_ID_MESA + " = ?",
					new String[] { Integer.toString(mesa.getIdMesa()) });
		}

		cursor.close();

		return resultado;
	}

	public List<Mesa> obtenerMesas() {
		List<Mesa> mesas = new ArrayList<Mesa>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_MESAS, null, null,
				null, null, null, null);

		while (cursor.moveToNext()) {

			Mesa mesa = obtenerMesa(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MS_ID_MESA)));

			mesas.add(mesa);
		}

		cursor.close();
		return mesas;
	}

	public Mesa obtenerMesa(int idMesa) {
		Mesa mesa = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_MESAS, null,
				LocalSQLite.COLUMN_MS_ID_MESA + " = ?",
				new String[] { Integer.toString(idMesa) }, null, null, null);

		while (cursor.moveToNext()) {

			mesa = new Mesa(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MS_ID_MESA)),
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_MS_CAPACIDAD)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_MS_NOMBRE)));

		}

		cursor.close();
		return mesa;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Mesa mesaJson2Mesa(JSONObject mesaJson)
			throws JSONException {

		Mesa mesa = new Mesa(
				mesaJson.getInt(JSON_ID_MESA),
				mesaJson.getInt(JSON_CAPACIDAD),
				mesaJson.getString(JSON_NOMBRE));

		return mesa;
	}

}
