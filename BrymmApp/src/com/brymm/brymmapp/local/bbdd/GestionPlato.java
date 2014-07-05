package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Plato;
import com.brymm.brymmapp.local.pojo.TipoPlato;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionPlato {

	public static final String JSON_PLATOS = "platos";
	public static final String JSON_PLATO = "plato";
	public static final String JSON_ID_PLATO = "idPlato";
	public static final String JSON_TIPO_PLATO = "tipoPlato";
	public static final String JSON_NOMBRE = "nombre";
	public static final String JSON_PRECIO = "precio";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionPlato(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarPlatos() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_PLATOS, null, null);
	}

	public void borrarPlato(int idPlato) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_PLATOS,
				LocalSQLite.COLUMN_PL_ID_PLATO + " = ? ",
				new String[] { Integer.toString(idPlato) });
	}

	public void guardarPlato(Plato plato) {

		Cursor cursor = database.query(LocalSQLite.TABLE_PLATOS, null,
				LocalSQLite.COLUMN_PL_ID_PLATO + " = ?",
				new String[] { Integer.toString(plato.getIdPlato()) }, null,
				null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_PL_ID_TIPO_PLATO, plato.getTipoPlato()
				.getIdTipoPlato());
		content.put(LocalSQLite.COLUMN_PL_NOMBRE, plato.getNombre());
		content.put(LocalSQLite.COLUMN_PL_PRECIO, plato.getPrecio());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_PL_ID_PLATO, plato.getIdPlato());

			database.insert(LocalSQLite.TABLE_PLATOS, null, content);

		} else {

			database.update(LocalSQLite.TABLE_PLATOS, content,
					LocalSQLite.COLUMN_PL_ID_PLATO + " = ?",
					new String[] { Integer.toString(plato.getIdPlato()) });

		}

		cursor.close();

	}

	public List<Plato> obtenerPlatos() {
		List<Plato> platos = new ArrayList<Plato>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_PLATOS, null, null,
				null, null, null, null);

		while (cursor.moveToNext()) {

			Plato plato = obtenerPlato(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_PL_ID_PLATO)));

			platos.add(plato);
		}

		cursor.close();
		return platos;
	}

	public Plato obtenerPlato(int idPlato) {
		Plato plato = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_PLATOS, null,
				LocalSQLite.COLUMN_PL_ID_PLATO + " = ?",
				new String[] { Integer.toString(idPlato) }, null, null, null);

		while (cursor.moveToNext()) {

			GestionTipoPlato gtp = new GestionTipoPlato(context);
			TipoPlato tipoPlato = gtp.obtenerTipoPlato(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_PL_ID_TIPO_PLATO)));
			gtp.cerrarDatabase();

			plato = new Plato(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_PL_ID_PLATO)),
					tipoPlato, cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_PL_NOMBRE)),
					(float) cursor.getDouble(cursor
							.getColumnIndex(LocalSQLite.COLUMN_AR_PRECIO)));

		}

		cursor.close();
		return plato;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Plato platoJson2Plato(JSONObject platoJson)
			throws JSONException {

		TipoPlato tipoPlato = GestionTipoPlato
				.tipoPlatoJson2TipoPlato(platoJson
						.getJSONObject(JSON_TIPO_PLATO));		

		Plato plato = new Plato(platoJson.getInt(JSON_ID_PLATO),
				tipoPlato, platoJson.getString(JSON_NOMBRE),
				(float) platoJson.getDouble(JSON_PRECIO));

		return plato;
	}
	
}
