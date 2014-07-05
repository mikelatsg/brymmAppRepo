package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Direccion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionDireccion {

	public static final String JSON_DIRECCION = "direccion";
	public static final String JSON_ID_DIRECCION = "id_direccion_envio";
	public static final String JSON_NOMBRE = "nombre";
	public static final String JSON_POBLACION = "poblacion";
	public static final String JSON_PROVINCIA = "provincia";
	public static final String JSON_ID_USUARIO = "idUsuario";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionDireccion(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}
	
	public GestionDireccion(SQLiteDatabase database){
		this.database = database;
	}

	public void borrarDirecciones() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_DIRECCIONES, null, null);
	}

	public long guardarDireccion(Direccion direccion) {

		long resultado = -1;

		Cursor cursor = database.query(LocalSQLite.TABLE_DIRECCIONES, null,
				LocalSQLite.COLUMN_DR_ID_DIRECCION + " = ?",
				new String[] { Integer.toString(direccion.getIdDireccion()) },
				null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_DR_DIRECCION, direccion.getDireccion());
		content.put(LocalSQLite.COLUMN_DR_NOMBRE, direccion.getNombre());
		content.put(LocalSQLite.COLUMN_DR_POBLACION, direccion.getLocalidad());
		content.put(LocalSQLite.COLUMN_DR_PROVINCIA, direccion.getProvincia());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_DR_ID_DIRECCION,
					direccion.getIdDireccion());

			resultado = database.insert(LocalSQLite.TABLE_DIRECCIONES, null,
					content);
		} else {

			resultado = database
					.update(LocalSQLite.TABLE_DIRECCIONES, content,
							LocalSQLite.COLUMN_DR_ID_DIRECCION + " = ?",
							new String[] { Integer.toString(direccion
									.getIdDireccion()) });
		}

		cursor.close();

		return resultado;
	}

	public List<Direccion> obtenerDirecciones() {
		List<Direccion> direcciones = new ArrayList<Direccion>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_DIRECCIONES, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			Direccion direccion = obtenerDireccion(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_DR_ID_DIRECCION)));

			direcciones.add(direccion);
		}

		cursor.close();
		return direcciones;
	}

	public Direccion obtenerDireccion(int idDireccion) {
		Direccion direccion = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_DIRECCIONES, null,
				LocalSQLite.COLUMN_DR_ID_DIRECCION + " = ?",
				new String[] { Integer.toString(idDireccion) }, null, null,
				null);

		while (cursor.moveToNext()) {

			direccion = new Direccion(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_DR_ID_DIRECCION)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DR_NOMBRE)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DR_DIRECCION)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DR_POBLACION)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DR_PROVINCIA)));

		}

		cursor.close();
		return direccion;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Direccion direccionJson2Direccion(JSONObject direccionJson)
			throws JSONException {

		Direccion direccion = new Direccion(
				direccionJson.getInt(JSON_ID_DIRECCION),
				direccionJson.getString(JSON_NOMBRE),
				direccionJson.getString(JSON_DIRECCION),
				direccionJson.getString(JSON_POBLACION),
				direccionJson.getString(JSON_PROVINCIA));

		return direccion;
	}

}
