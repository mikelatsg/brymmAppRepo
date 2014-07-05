package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.DiaSemana;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionDiaSemana {

	public static final String JSON_DIAS_SEMANA = "diasSemana";
	public static final String JSON_DIA_SEMANA = "diaSemana";
	public static final String JSON_ID_DIA = "idDia";
	public static final String JSON_DIA = "dia";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionDiaSemana(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public GestionDiaSemana(SQLiteDatabase database) {
		this.database = database;
	}

	public void borrarDiasSemana() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_DIAS_SEMANA, null, null);
	}

	public long guardarDiaSemana(DiaSemana diaSemana) {

		/*
		 * if (!database.isOpen()) { database =
		 * openHelper.getWritableDatabase(); }
		 */

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_DS_ID_DIA, diaSemana.getIdDia());
		content.put(LocalSQLite.COLUMN_DS_DIA, diaSemana.getDia());

		long resultado = -1;

		resultado = database.insert(LocalSQLite.TABLE_DIAS_SEMANA, null,
				content);

		return resultado;
	}

	public DiaSemana obtenerDiaSemana(int idDia) {
		DiaSemana diaSemana = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_DIAS_SEMANA, null,
				LocalSQLite.COLUMN_DS_ID_DIA + " = ?",
				new String[] { Integer.toString(idDia) }, null, null, null);

		while (cursor.moveToNext()) {

			diaSemana = new DiaSemana(idDia, cursor.getString(cursor
					.getColumnIndex(LocalSQLite.COLUMN_DS_DIA)));

		}

		cursor.close();
		return diaSemana;
	}

	public List<DiaSemana> obtenerDiaSemana() {
		List<DiaSemana> diasSemana = new ArrayList<DiaSemana>();
		Cursor cursor = database.query(LocalSQLite.TABLE_DIAS_SEMANA, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			DiaSemana diaSemana = new DiaSemana(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_DS_ID_DIA)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DS_DIA)));

			diasSemana.add(diaSemana);

		}

		cursor.close();
		return diasSemana;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static DiaSemana diaSemanaJson2DiaSemana(JSONObject diaSemanaJson)
			throws JSONException {

		DiaSemana diaSemana = new DiaSemana(diaSemanaJson.getInt(JSON_ID_DIA),
				diaSemanaJson.getString(JSON_DIA));

		return diaSemana;
	}

}
