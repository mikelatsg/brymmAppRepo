package com.brymm.brymmapp.local.bbdd;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract.Columns;

public class GestionActualizaciones {

	public static final String JSON_FECHA = "fecha";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionActualizaciones(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarActualizaciones() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_ACTUALIZACION, null, null);
	}

	public void guardarActualizacion() {

		String fecha = GestionActualizaciones.obtenerFechaActual();
		
		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_ACT_FECHA, fecha);

		database.insert(LocalSQLite.TABLE_ACTUALIZACION, null, content);

	}

	public String obtenerUltimaFechaActualizacion() {
		String fecha = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_ACTUALIZACION, null,
				null, null, null, null, LocalSQLite.COLUMN_ACT_FECHA + " DESC");

		while (cursor.moveToFirst()) {

			fecha = cursor.getString(cursor
					.getColumnIndex(LocalSQLite.COLUMN_ACT_FECHA));
			
			break;
		}

		if (fecha == null) {
			fecha = "2014-01-01 01:01:01";
		}

		return fecha;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static String obtenerFechaActual() {
		Calendar c = Calendar.getInstance();

		String month = null;
		if (c.get(Calendar.MONTH) + 1 < 10) {
			month = "0" + Integer.toString(c.get(Calendar.MONTH) + 1);
		} else {
			month = Integer.toString(c.get(Calendar.MONTH) + 1);
		}

		String day = null;
		if (c.get(Calendar.DAY_OF_MONTH) < 10) {
			day = "0" + Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		} else {
			day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		}

		String hour = null;
		if (c.get(Calendar.HOUR_OF_DAY) < 10) {
			hour = "0" + Integer.toString(c.get(Calendar.HOUR_OF_DAY));
		} else {
			hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
		}

		String minute = null;
		if (c.get(Calendar.MINUTE) < 10) {
			minute = "0" + Integer.toString(c.get(Calendar.MINUTE));
		} else {
			minute = Integer.toString(c.get(Calendar.MINUTE));
		}

		String second = null;
		if (c.get(Calendar.SECOND) < 10) {
			second = "0" + Integer.toString(c.get(Calendar.SECOND));
		} else {
			second = Integer.toString(c.get(Calendar.SECOND));
		}

		String fecha = Integer.toString(c.get(Calendar.YEAR)) + "-" + month
				+ "-" + day + " " + hour + ":" + minute + ":" + second;

		return fecha;
	}

}
