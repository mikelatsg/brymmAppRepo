package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.ServicioLocal;
import com.brymm.brymmapp.local.pojo.TipoServicio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionServicioLocal {

	public static final String JSON_SERVICIOS_LOCAL = "serviciosLocal";
	public static final String JSON_SERVICIO_LOCAL = "servicioLocal";
	public static final String JSON_SERVICIO = "servicio";
	public static final String JSON_ID_SERVICIO = "idServicio";
	public static final String JSON_TIPO_SERVICIO = "tipoServicio";
	public static final String JSON_ACTIVO = "activo";
	public static final String JSON_IMPORTE_MINIMO = "importeMinimo";
	public static final String JSON_PRECIO = "precio";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionServicioLocal(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarServiciosLocal() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_SERVICIOS_LOCAL, null, null);
	}

	public void borrarServicioLocal(int idServicio) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_SERVICIOS_LOCAL,
				LocalSQLite.COLUMN_SL_ID_SERVICIO + " = ? ",
				new String[] { Integer.toString(idServicio) });
	}

	public void guardarServicioLocal(ServicioLocal servicioLocal) {

		/*
		 * if (!database.isOpen()) { database =
		 * openHelper.getWritableDatabase(); }
		 */

		Cursor cursor = database
				.query(LocalSQLite.TABLE_SERVICIOS_LOCAL, null,
						LocalSQLite.COLUMN_SL_ID_SERVICIO + " = ?",
						new String[] { Integer.toString(servicioLocal
								.getIdServicio()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_SL_ID_TIPO_SERVICIO, servicioLocal
				.getTipoServicio().getIdTipoServicio());
		content.put(LocalSQLite.COLUMN_SL_ACTIVO,
				(servicioLocal.isActivo()) ? 1 : 0);
		content.put(LocalSQLite.COLUMN_SL_IMPORTE_MINIMO,
				servicioLocal.getImporteMinimo());
		content.put(LocalSQLite.COLUMN_SL_PRECIO, servicioLocal.getPrecio());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_SL_ID_SERVICIO,
					servicioLocal.getIdServicio());

			database.insert(LocalSQLite.TABLE_SERVICIOS_LOCAL, null, content);

		} else {

			database.update(LocalSQLite.TABLE_SERVICIOS_LOCAL, content,
					LocalSQLite.COLUMN_SL_ID_SERVICIO + " = ?",
					new String[] { Integer.toString(servicioLocal
							.getIdServicio()) });

		}

		cursor.close();

	}

	public List<ServicioLocal> obtenerServiciosLocal() {
		List<ServicioLocal> servicios = new ArrayList<ServicioLocal>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_SERVICIOS_LOCAL, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			ServicioLocal servicio = obtenerServicioLocal(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_SL_ID_SERVICIO)));

			servicios.add(servicio);
		}

		cursor.close();
		return servicios;
	}

	public ServicioLocal obtenerServicioLocal(int idServicio) {
		ServicioLocal servicio = null;
		Cursor cursor = database
				.query(LocalSQLite.TABLE_SERVICIOS_LOCAL, null,
						LocalSQLite.COLUMN_SL_ID_SERVICIO + " = ?",
						new String[] { Integer.toString(idServicio) }, null,
						null, null);

		while (cursor.moveToNext()) {

			GestionTipoServicio gts = new GestionTipoServicio(context);
			TipoServicio tipoServicio = gts
					.obtenerTipoServicio(cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_SL_ID_TIPO_SERVICIO)));
			gts.cerrarDatabase();

			servicio = new ServicioLocal(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_SL_ID_SERVICIO)),
					tipoServicio,
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_SL_ACTIVO)) == 1,
					cursor.getFloat(cursor
							.getColumnIndex(LocalSQLite.COLUMN_SL_IMPORTE_MINIMO)),
					cursor.getFloat(cursor
							.getColumnIndex(LocalSQLite.COLUMN_SL_PRECIO)));

		}

		cursor.close();
		return servicio;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static ServicioLocal servicioLocalJson2ServicioLocal(
			JSONObject servicioJson) throws JSONException {

		TipoServicio tipoServicio = GestionTipoServicio
				.tipoServicioJson2TipoServicio(servicioJson
						.getJSONObject(JSON_TIPO_SERVICIO));

		ServicioLocal servicio = new ServicioLocal(
				servicioJson.getInt(JSON_ID_SERVICIO), tipoServicio,
				servicioJson.getInt(JSON_ACTIVO) == 1,
				(float) servicioJson.getDouble(JSON_IMPORTE_MINIMO),
				(float) servicioJson.getDouble(JSON_PRECIO));

		return servicio;
	}

}
