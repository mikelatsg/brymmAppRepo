package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.TipoMenu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTipoMenu {

	public static final String JSON_TIPOS_MENU = "tiposMenu";
	public static final String JSON_TIPO_MENU = "tipoMenu";
	public static final String JSON_ID_TIPO_MENU = "idTipoMenu";
	public static final String JSON_DESCRIPCION = "descripcion";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionTipoMenu(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarTiposMenu() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_TIPOS_MENU, null, null);
	}

	public long guardarTipoMenu(TipoMenu tipoMenu) {

		long resultado = -1;

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_TM_DESCRIPCION,
				tipoMenu.getDescripcion());
		content.put(LocalSQLite.COLUMN_TM_ID_TIPO_MENU,
				tipoMenu.getIdTipoMenu());

		resultado = database.insert(LocalSQLite.TABLE_TIPOS_MENU, null, content);

		return resultado;
	}

	public List<TipoMenu> obtenerTiposMenu() {
		List<TipoMenu> tiposMenu = new ArrayList<TipoMenu>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_TIPOS_MENU, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			TipoMenu tipoMenu = obtenerTipoMenu(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_TM_ID_TIPO_MENU)));

			tiposMenu.add(tipoMenu);
		}

		cursor.close();
		return tiposMenu;
	}

	public TipoMenu obtenerTipoMenu(int idTipoMenu) {
		TipoMenu tipoMenu = null;
		Cursor cursor = database
				.query(LocalSQLite.TABLE_TIPOS_MENU, null,
						LocalSQLite.COLUMN_TM_ID_TIPO_MENU + " = ?",
						new String[] { Integer.toString(idTipoMenu) }, null,
						null, null);

		while (cursor.moveToNext()) {

			tipoMenu = new TipoMenu(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_TM_ID_TIPO_MENU)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_TM_DESCRIPCION)));

		}

		cursor.close();
		return tipoMenu;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static TipoMenu tipoMenuJson2TipoMenu(JSONObject tipoMenuJson)
			throws JSONException {

		TipoMenu tipoMenu = new TipoMenu(
				tipoMenuJson.getInt(JSON_ID_TIPO_MENU),
				tipoMenuJson.getString(JSON_DESCRIPCION));

		return tipoMenu;
	}

}
