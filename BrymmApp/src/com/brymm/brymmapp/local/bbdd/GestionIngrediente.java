package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Ingrediente;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionIngrediente {

	public static final String JSON_INGREDIENTES = "ingredientes";
	public static final String JSON_INGREDIENTE = "ingrediente";
	public static final String JSON_ID_INGREDIENTE = "idIngrediente";
	public static final String JSON_NOMBRE = "nombre";
	public static final String JSON_DESCRIPCION = "descripcion";
	public static final String JSON_PRECIO = "precio";
	public static final String JSON_ID_LOCAL = "idLocal";

	SQLiteDatabase database;
	LocalSQLite openHelper;

	public GestionIngrediente(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarIngredientes() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_INGREDIENTES, null, null);
	}

	public void borrarIngrediente(int idIngrediente) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_INGREDIENTES,
				LocalSQLite.COLUMN_IN_ID_INGREDIENTE + " = ?",
				new String[] { Integer.toString(idIngrediente) });
	}

	public void guardarIngrediente(Ingrediente ingrediente) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database
				.query(LocalSQLite.TABLE_INGREDIENTES, null,
						LocalSQLite.COLUMN_IN_ID_INGREDIENTE + " = ?",
						new String[] { Integer.toString(ingrediente
								.getIdIngrediente()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_IN_DESCRIPCION,
				ingrediente.getDescripcion());
		content.put(LocalSQLite.COLUMN_IN_NOMBRE, ingrediente.getNombre());
		content.put(LocalSQLite.COLUMN_IN_PRECIO, ingrediente.getPrecio());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_IN_ID_INGREDIENTE,
					ingrediente.getIdIngrediente());

			database.insert(LocalSQLite.TABLE_INGREDIENTES, null, content);
		} else {

			database.update(LocalSQLite.TABLE_INGREDIENTES, content,
					LocalSQLite.COLUMN_IN_ID_INGREDIENTE + " = ?",
					new String[] { Integer.toString(ingrediente
							.getIdIngrediente()) });
		}

		cursor.close();

	}

	public List<Ingrediente> obtenerIngredientes() {
		List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_INGREDIENTES, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			Ingrediente ingrediente = obtenerIngrediente(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_IN_ID_INGREDIENTE)));

			ingredientes.add(ingrediente);
		}

		cursor.close();
		return ingredientes;
	}

	public Ingrediente obtenerIngrediente(int idIngrediente) {
		Ingrediente ingrediente = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_INGREDIENTES, null,
				LocalSQLite.COLUMN_IN_ID_INGREDIENTE + " = ?",
				new String[] { Integer.toString(idIngrediente) }, null, null,
				null);

		while (cursor.moveToNext()) {

			ingrediente = new Ingrediente(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_IN_ID_INGREDIENTE)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_IN_NOMBRE)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_IN_DESCRIPCION)),
					(float) cursor.getDouble(cursor
							.getColumnIndex(LocalSQLite.COLUMN_IN_PRECIO)));

		}

		cursor.close();
		return ingrediente;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Ingrediente ingredienteJson2Ingrediente(
			JSONObject ingredienteJson) throws JSONException {

		Ingrediente ingrediente = new Ingrediente(
				ingredienteJson.getInt(JSON_ID_INGREDIENTE),
				ingredienteJson.getString(JSON_NOMBRE),
				ingredienteJson.getString(JSON_DESCRIPCION),
				(float) ingredienteJson.getDouble(JSON_PRECIO));

		return ingrediente;
	}

}
