package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.TipoServicio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTipoServicio {

	public static final String JSON_TIPOS_SERVICIO = "tiposServicio";
	public static final String JSON_TIPO_SERVICIO = "tipoServicio";
	public static final String JSON_ID_TIPO_SERVICIO = "idTipoServicio";
	public static final String JSON_DESCRIPCION = "descripcion";
	public static final String JSON_SERVICIO = "servicio";
	public static final String JSON_MOSTRAR_BUSCADOR = "mostrarBuscador";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionTipoServicio(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public GestionTipoServicio(SQLiteDatabase database) {
		this.database = database;
	}

	public void borrarTiposServicio() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_TIPOS_SERVICIO, null, null);
	}

	public long guardarTipoServicio(TipoServicio tipoServicio) {

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_TS_DESCRIPCION,
				tipoServicio.getDescripcion());
		content.put(LocalSQLite.COLUMN_TS_ID_TIPO_SERVICIO,
				tipoServicio.getIdTipoServicio());
		content.put(LocalSQLite.COLUMN_TS_MOSTRAR_BUSCADOR,
				(tipoServicio.isMostrarBuscador()) ? 1 : 0);
		content.put(LocalSQLite.COLUMN_TS_SERVICIO, tipoServicio.getServicio());

		long resultado = -1;

		resultado = database.insert(LocalSQLite.TABLE_TIPOS_SERVICIO, null,
				content);

		return resultado;
	}

	public TipoServicio obtenerTipoServicio(int idTipoServicio) {
		TipoServicio tipoServicio = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_TIPOS_SERVICIO, null,
				LocalSQLite.COLUMN_TS_ID_TIPO_SERVICIO + " = ?",
				new String[] { Integer.toString(idTipoServicio) }, null, null,
				null);

		while (cursor.moveToNext()) {

			tipoServicio = new TipoServicio(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TS_ID_TIPO_SERVICIO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TS_SERVICIO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TS_DESCRIPCION)),
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TS_MOSTRAR_BUSCADOR)) == 1);

		}

		cursor.close();
		return tipoServicio;
	}

	public List<TipoServicio> obtenerTiposServicio() {
		List<TipoServicio> tiposServicio = new ArrayList<TipoServicio>();
		Cursor cursor = database.query(LocalSQLite.TABLE_TIPOS_SERVICIO, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			TipoServicio tipoServicio = 
					obtenerTipoServicio(cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TS_ID_TIPO_SERVICIO)));
			
			tiposServicio.add(tipoServicio);

		}

		cursor.close();
		return tiposServicio;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static TipoServicio tipoServicioJson2TipoServicio(
			JSONObject tipoServicioJson) throws JSONException {

		TipoServicio tipoServicio = new TipoServicio(
				tipoServicioJson.getInt(JSON_ID_TIPO_SERVICIO),
				tipoServicioJson.getString(JSON_SERVICIO),
				tipoServicioJson.getString(JSON_DESCRIPCION),
				tipoServicioJson.getInt(JSON_MOSTRAR_BUSCADOR) == 1);

		return tipoServicio;
	}

}
