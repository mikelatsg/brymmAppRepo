package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.usuario.pojo.ServicioLocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionServicioLocal {

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionServicioLocal(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarServiciosLocal() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_SERVICIOS_LOCAL, null,
				null);
	}

	public void guardarServicioLocal(ServicioLocal servicio) {				

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_SL_ID_SERVICIO_LOCAL,
				servicio.getIdServicioLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_SL_ID_TIPO_SERVICIO,
				servicio.getIdTipoServicio());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_SL_ID_LOCAL,
				servicio.getIdLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_SL_IMPORTE_MINIMO,
				servicio.getImporteMinimo());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_SL_PRECIO,
				servicio.getPrecio());

		database.insert(UsuarioSQLiteOpenHelper.TABLE_SERVICIOS_LOCAL, null,
				content);

	}

	public float hayEnvioPedidos() {

		float precio = -1;
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_SERVICIOS_LOCAL, null,
				UsuarioSQLiteOpenHelper.COLUMN_SL_ID_TIPO_SERVICIO + " = ?",
				new String[] { "2" }, null, null, null);

		if (cursor.moveToFirst()) {
			precio = cursor.getFloat(cursor.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_SL_PRECIO));
			if (!cursor.isClosed())
				cursor.close();
			return precio;
		}

		if (!cursor.isClosed())
			cursor.close();

		return precio;
	}

	public List<ServicioLocal> obtenerServiciosLocal() {
		List<ServicioLocal> servicios = new ArrayList<ServicioLocal>();
		String sql = "SELECT sl.*, ts."
				+ UsuarioSQLiteOpenHelper.COLUMN_TS_SERVICIO + " FROM "
				+ UsuarioSQLiteOpenHelper.TABLE_SERVICIOS_LOCAL + " sl , "
				+ UsuarioSQLiteOpenHelper.TABLE_TIPOS_SERVICIOS + " ts "
				+ "WHERE ts."
				+ UsuarioSQLiteOpenHelper.COLUMN_TS_ID_TIPO_SERVICIO + " = sl."
				+ UsuarioSQLiteOpenHelper.COLUMN_SL_ID_TIPO_SERVICIO;
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.rawQuery(sql, null);

		while (cursor.moveToNext()) {
			ServicioLocal servicio = new ServicioLocal(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_SL_ID_SERVICIO_LOCAL)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_SL_ID_TIPO_SERVICIO)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_SL_ID_LOCAL)),
					cursor.getFloat(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_SL_IMPORTE_MINIMO)),
					cursor.getFloat(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_SL_PRECIO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TS_SERVICIO)));

			servicios.add(servicio);
		}

		cursor.close();
		return servicios;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

}
