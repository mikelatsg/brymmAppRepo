package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.DiaSemana;
import com.brymm.brymmapp.local.pojo.HorarioLocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionHorarioLocal {

	public static final String JSON_HORARIOS_LOCAL = "horariosLocal";
	public static final String JSON_HORARIO_LOCAL = "horarioLocal";
	public static final String JSON_ID_HORARIO_LOCAL = "idHorarioLocal";
	public static final String JSON_HORA_INICIO = "horaInicio";
	public static final String JSON_HORA_FIN = "horaFin";
	public static final String JSON_DIA = "dia";
	public static final String JSON_ID_LOCAL = "idLocal";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionHorarioLocal(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarHorariosLocal() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_HORARIOS_LOCAL, null, null);
	}

	public void borrarHorarioLocal(int idHorarioLocal) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_HORARIOS_LOCAL,
				LocalSQLite.COLUMN_HL_ID_HORARIO_LOCAL + " = ? ",
				new String[] { Integer.toString(idHorarioLocal) });
	}

	public void guardarHorarioLocal(HorarioLocal horarioLocal) {

		/*
		 * if (!database.isOpen()) { database =
		 * openHelper.getWritableDatabase(); }
		 */

		Cursor cursor = database.query(LocalSQLite.TABLE_HORARIOS_LOCAL, null,
				LocalSQLite.COLUMN_HL_ID_HORARIO_LOCAL + " = ?",
				new String[] { Integer.toString(horarioLocal
						.getIdHorarioLocal()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_HL_HORA_FIN, horarioLocal.getHoraFin());
		content.put(LocalSQLite.COLUMN_HL_HORA_INICIO,
				horarioLocal.getHoraInicio());
		content.put(LocalSQLite.COLUMN_HL_ID_DIA, horarioLocal.getDia()
				.getIdDia());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_HL_ID_HORARIO_LOCAL,
					horarioLocal.getIdHorarioLocal());

			database.insert(LocalSQLite.TABLE_HORARIOS_LOCAL, null, content);

		} else {

			database.update(LocalSQLite.TABLE_HORARIOS_LOCAL, content,
					LocalSQLite.COLUMN_HL_ID_HORARIO_LOCAL + " = ?",
					new String[] { Integer.toString(horarioLocal
							.getIdHorarioLocal()) });
		}

		cursor.close();

	}

	public List<HorarioLocal> obtenerHorariosLocal() {
		List<HorarioLocal> horariosLocal = new ArrayList<HorarioLocal>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_HORARIOS_LOCAL, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			HorarioLocal horarioLocal = obtenerHorarioLocal(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_HL_ID_HORARIO_LOCAL)));

			horariosLocal.add(horarioLocal);
		}

		cursor.close();
		return horariosLocal;
	}

	public HorarioLocal obtenerHorarioLocal(int idHorarioLocal) {
		HorarioLocal horarioLocal = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_HORARIOS_LOCAL, null,
				LocalSQLite.COLUMN_HL_ID_HORARIO_LOCAL + " = ?",
				new String[] { Integer.toString(idHorarioLocal) }, null, null,
				null);

		while (cursor.moveToNext()) {

			GestionDiaSemana gds = new GestionDiaSemana(context);
			DiaSemana diaSemana = gds.obtenerDiaSemana(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_HL_ID_DIA)));

			horarioLocal = new HorarioLocal(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_HL_ID_HORARIO_LOCAL)),
					diaSemana,
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_HL_HORA_INICIO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_HL_HORA_FIN)));

		}

		cursor.close();
		return horarioLocal;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static HorarioLocal horarioLocalJson2HorarioLocal(
			JSONObject horarioLocalJson) throws JSONException {

		DiaSemana diaSemana = GestionDiaSemana
				.diaSemanaJson2DiaSemana(horarioLocalJson
						.getJSONObject(JSON_DIA));

		HorarioLocal horarioLocal = new HorarioLocal(
				horarioLocalJson.getInt(JSON_ID_HORARIO_LOCAL), diaSemana,
				horarioLocalJson.getString(JSON_HORA_INICIO),
				horarioLocalJson.getString(JSON_HORA_FIN));

		return horarioLocal;
	}

}
