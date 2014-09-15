package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Camarero;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionCamarero {

	public static final String JSON_CAMAREROS = "camareros";
	public static final String JSON_CAMARERO = "camarero";
	public static final String JSON_ID_CAMARERO = "idCamarero";
	public static final String JSON_NOMBRE = "nombre";
	public static final String JSON_ACTIVO = "activo";
	public static final String JSON_CONTROL_TOTAL = "controlTotal";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionCamarero(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarCamareros() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_CAMARERO, null, null);
	}

	public void borrarCamarero(int idCamarero) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_CAMARERO,
				LocalSQLite.COLUMN_CMR_ID_CAMARERO + " = ?",
				new String[] { Integer.toString(idCamarero) });
	}

	public void guardarCamarero(Camarero camarero) {

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_CMR_NOMBRE, camarero.getNombre());
		content.put(LocalSQLite.COLUMN_CMR_ACTIVO, camarero.isActivo() ? 1 : 0);
		content.put(LocalSQLite.COLUMN_CMR_CONTROL_TOTAL,
				camarero.isControlTotal() ? 1 : 0);

		Cursor cursor = database.query(LocalSQLite.TABLE_CAMARERO, null,
				LocalSQLite.COLUMN_CMR_ID_CAMARERO + " = ?",
				new String[] { Integer.toString(camarero.getIdCamarero()) },
				null, null, null);

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_CMR_ID_CAMARERO,
					camarero.getIdCamarero());

			database.insert(LocalSQLite.TABLE_CAMARERO, null, content);
		} else {

			database.update(LocalSQLite.TABLE_CAMARERO, content,
					LocalSQLite.COLUMN_CMR_ID_CAMARERO + " = ?",
					new String[] { Integer.toString(camarero.getIdCamarero()) });
		}

		cursor.close();
	}

	public List<Camarero> obtenerCamareros() {
		List<Camarero> camareros = new ArrayList<Camarero>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_CAMARERO, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			Camarero camarero = obtenerCamarero(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_CMR_ID_CAMARERO)));

			camareros.add(camarero);
		}

		cursor.close();
		return camareros;
	}

	public Camarero obtenerCamarero(int idCamarero) {
		Camarero camarero = null;
		Cursor cursor = database
				.query(LocalSQLite.TABLE_CAMARERO, null,
						LocalSQLite.COLUMN_CMR_ID_CAMARERO + " = ?",
						new String[] { Integer.toString(idCamarero) }, null,
						null, null);

		while (cursor.moveToNext()) {

			camarero = new Camarero(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_CMR_ID_CAMARERO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_CMR_NOMBRE)),
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_CMR_ACTIVO)) == 1,
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_CMR_CONTROL_TOTAL)) == 1);

		}

		cursor.close();
		return camarero;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Camarero camareroJson2Camarero(JSONObject camareroJson)
			throws JSONException {

		Camarero camarero = new Camarero(
				camareroJson.getInt(JSON_ID_CAMARERO),
				camareroJson.getString(JSON_NOMBRE),
				camareroJson.getInt(JSON_ACTIVO) == 1,
				camareroJson.getInt(JSON_CONTROL_TOTAL) == 1);

		return camarero;
	}

}
