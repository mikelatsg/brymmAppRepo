package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.DiaCierre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionDiaCierre {

	public static final String JSON_DIAS_CIERRE = "diasCierre";
	public static final String JSON_DIA_CIERRE = "diaCierre";
	public static final String JSON_ID_DIA_CIERRE = "idDiaCierre";
	public static final String JSON_FECHA = "fecha";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionDiaCierre(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public GestionDiaCierre(SQLiteDatabase database) {
		this.database = database;
	}

	public void borrarDiasCierre() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_DIAS_CIERRE, null, null);
	}

	public void borrarDiaCierre(int idDiaCierre) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_DIAS_CIERRE,
				LocalSQLite.COLUMN_DC_ID_DIA_CIERRE + " = ?",
				new String[] { Integer.toString(idDiaCierre) });
	}

	public long guardarDiaCierre(DiaCierre diaCierre) {

		long resultado = -1;

		Cursor cursor = database.query(LocalSQLite.TABLE_DIAS_CIERRE, null,
				LocalSQLite.COLUMN_DC_ID_DIA_CIERRE + " = ?",
				new String[] { Integer.toString(diaCierre.getIdDiaCierre()) },
				null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_DC_FECHA, diaCierre.getFecha());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_DC_ID_DIA_CIERRE,
					diaCierre.getIdDiaCierre());

			resultado = database.insert(LocalSQLite.TABLE_DIAS_CIERRE, null,
					content);
		} else {

			resultado = database
					.update(LocalSQLite.TABLE_DIAS_CIERRE, content,
							LocalSQLite.COLUMN_DC_ID_DIA_CIERRE + " = ?",
							new String[] { Integer.toString(diaCierre
									.getIdDiaCierre()) });
		}

		cursor.close();

		return resultado;
	}

	public List<DiaCierre> obtenerDiasCierre() {
		List<DiaCierre> diasCierre = new ArrayList<DiaCierre>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_DIAS_CIERRE, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			DiaCierre diaCierre = obtenerDiaCierre(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_DC_ID_DIA_CIERRE)));

			diasCierre.add(diaCierre);
		}

		cursor.close();
		return diasCierre;
	}

	public DiaCierre obtenerDiaCierre(int idDiaCierre) {
		DiaCierre diaCierre = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_DIAS_CIERRE, null,
				LocalSQLite.COLUMN_DC_ID_DIA_CIERRE + " = ?",
				new String[] { Integer.toString(idDiaCierre) }, null, null,
				null);

		while (cursor.moveToNext()) {

			diaCierre = new DiaCierre(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_DC_ID_DIA_CIERRE)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DC_FECHA)));

		}

		cursor.close();
		return diaCierre;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static DiaCierre diaCierreJson2DiaCierre(JSONObject diaCierreJson)
			throws JSONException {

		DiaCierre diaCierre = new DiaCierre(
				diaCierreJson.getInt(JSON_ID_DIA_CIERRE),
				diaCierreJson.getString(JSON_FECHA));

		return diaCierre;
	}

}
