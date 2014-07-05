package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.local.pojo.Plato;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionMenuDia {

	public static final String JSON_MENUS_DIA = "menusDia";
	public static final String JSON_MENU_DIA = "menuDia";
	public static final String JSON_ID_MENU_DIA = "idMenuDia";
	public static final String JSON_FECHA = "fecha";
	public static final String JSON_PLATOS = "platos";
	public static final String JSON_MENU = "menu";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionMenuDia(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarMenusDia() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_MENUS_DIA, null, null);
		database.delete(LocalSQLite.TABLE_PLATOS_MENU_DIA, null, null);
	}

	public void borrarMenuDia(int idMenuDia) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_MENUS_DIA,
				LocalSQLite.COLUMN_MD_ID_MENU_DIA + " = ? ",
				new String[] { Integer.toString(idMenuDia) });
		database.delete(LocalSQLite.TABLE_PLATOS_MENU_DIA,
				LocalSQLite.COLUMN_PMD_ID_MENU_DIA + " = ? ",
				new String[] { Integer.toString(idMenuDia) });
	}

	public void guardarMenuDia(MenuDia menuDia) {

		/*
		 * if (!database.isOpen()) { database =
		 * openHelper.getWritableDatabase(); }
		 */

		boolean hayError = false;

		database.beginTransaction();

		Cursor cursor = database.query(LocalSQLite.TABLE_MENUS_DIA, null,
				LocalSQLite.COLUMN_MD_ID_MENU_DIA + " = ?",
				new String[] { Integer.toString(menuDia.getIdMenuDia()) },
				null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_MD_ID_MENU, menuDia.getMenu()
				.getIdMenu());
		content.put(LocalSQLite.COLUMN_MD_FECHA, menuDia.getFecha());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_MD_ID_MENU_DIA,
					menuDia.getIdMenuDia());

			if (database.insert(LocalSQLite.TABLE_MENUS_DIA, null, content) < 0) {
				hayError = true;
			}
			// Se insertan los platos en la tabla platos_menu_dia
			for (Plato plato : menuDia.getPlatos()) {
				ContentValues contentPlato = new ContentValues();

				contentPlato.put(LocalSQLite.COLUMN_PMD_ID_MENU_DIA,
						menuDia.getIdMenuDia());
				contentPlato.put(LocalSQLite.COLUMN_PMD_ID_PLATO,
						plato.getIdPlato());

				if (database.insert(LocalSQLite.TABLE_PLATOS_MENU_DIA, null,
						contentPlato) < 0) {
					hayError = true;
				}
			}

		} else {

			if (database.update(LocalSQLite.TABLE_MENUS_DIA, content,
					LocalSQLite.COLUMN_MD_ID_MENU_DIA + " = ?",
					new String[] { Integer.toString(menuDia.getIdMenuDia()) }) < 0) {
				hayError = true;
			}

			// Se borran los platos
			if (database.delete(LocalSQLite.TABLE_PLATOS_MENU_DIA,
					LocalSQLite.COLUMN_PMD_ID_MENU_DIA + " = ? ",
					new String[] { Integer.toString(menuDia.getIdMenuDia()) }) < 0) {
				hayError = true;
			}

			// Se insertan los platos en la tabla platos_menu_dia
			for (Plato plato : menuDia.getPlatos()) {
				ContentValues contentPlato = new ContentValues();

				contentPlato.put(LocalSQLite.COLUMN_PMD_ID_PLATO,
						plato.getIdPlato());
				contentPlato.put(LocalSQLite.COLUMN_PMD_ID_MENU_DIA,
						menuDia.getIdMenuDia());

				if (database.insert(LocalSQLite.TABLE_PLATOS_MENU_DIA, null,
						contentPlato) < 0) {
					hayError = true;
				}
			}
		}

		cursor.close();

		if (!hayError) {
			database.setTransactionSuccessful();
		}

		database.endTransaction();

	}

	public List<MenuDia> obtenerMenusDia() {
		List<MenuDia> menusDia = new ArrayList<MenuDia>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_MENUS_DIA, null, null,
				null, null, null, null);

		while (cursor.moveToNext()) {

			MenuDia menuDia = obtenerMenuDia(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MD_ID_MENU_DIA)));

			menusDia.add(menuDia);
		}

		cursor.close();
		return menusDia;
	}

	public List<MenuDia> obtenerMenusDia(String fecha) {
		List<MenuDia> menusDia = new ArrayList<MenuDia>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_MENUS_DIA, null,
				LocalSQLite.COLUMN_MD_FECHA + " = ?", new String[] { fecha },
				null, null, null);

		while (cursor.moveToNext()) {

			MenuDia menuDia = obtenerMenuDia(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MD_ID_MENU_DIA)));

			menusDia.add(menuDia);
		}

		cursor.close();
		return menusDia;
	}

	public List<Plato> obtenerPlatosMenuDia(int idMenuDia) {
		List<Plato> platos = new ArrayList<Plato>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_PLATOS_MENU_DIA, null,
				LocalSQLite.COLUMN_PMD_ID_MENU_DIA + " = ?",
				new String[] { Integer.toString(idMenuDia) }, null, null, null);

		GestionPlato gestor = new GestionPlato(context);

		while (cursor.moveToNext()) {
			Plato plato = gestor.obtenerPlato(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_PMD_ID_PLATO)));

			platos.add(plato);
		}

		gestor.cerrarDatabase();

		return platos;
	}
	
	public List<Plato> obtenerPlatosNoMenuDia(int idMenuDia) {
		List<Plato> platos = new ArrayList<Plato>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.rawQuery(" SELECT * FROM "
				+ LocalSQLite.TABLE_PLATOS + " WHERE "
				+ LocalSQLite.COLUMN_PL_ID_PLATO + " NOT IN " + "(SELECT "
				+ LocalSQLite.COLUMN_PMD_ID_PLATO + " FROM "
				+ LocalSQLite.TABLE_PLATOS_MENU_DIA + " WHERE "
				+ LocalSQLite.COLUMN_PMD_ID_MENU_DIA + " = ? )", new String[] {
				Integer.toString(idMenuDia) });

		GestionPlato gestor = new GestionPlato(context);

		while (cursor.moveToNext()) {
			Plato plato = gestor.obtenerPlato(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_PL_ID_PLATO)));

			platos.add(plato);
		}

		gestor.cerrarDatabase();

		return platos;
	}

	public MenuDia obtenerMenuDia(int idMenuDia) {
		MenuDia menuDia = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_MENUS_DIA, null,
				LocalSQLite.COLUMN_MD_ID_MENU_DIA + " = ?",
				new String[] { Integer.toString(idMenuDia) }, null, null, null);

		while (cursor.moveToNext()) {

			GestionMenu gestor = new GestionMenu(context);
			MenuLocal menu = gestor.obtenerMenu(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MD_ID_MENU)));
			gestor.cerrarDatabase();

			List<Plato> platos = obtenerPlatosMenuDia(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MD_ID_MENU_DIA)));

			menuDia = new MenuDia(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MD_ID_MENU_DIA)), menu,
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_MD_FECHA)),
					platos);

		}

		cursor.close();
		return menuDia;
	}
	
	public List<String> obtenerDiasMenuMes(String anoMes) {
		List<String> diasConMenu = new ArrayList<String>();

		Cursor cursor = database.rawQuery("SELECT DISTINCT "
				+ LocalSQLite.COLUMN_MD_FECHA + " FROM "
				+ LocalSQLite.TABLE_MENUS_DIA + " WHERE "
				+ LocalSQLite.COLUMN_MD_FECHA + " LIKE ? ",
				new String[] { anoMes + "%"});

		while (cursor.moveToNext()) {
			String dia = cursor.getString(
					cursor.getColumnIndex(LocalSQLite.COLUMN_MD_FECHA)).split(
					"-")[2];

			diasConMenu.add(dia);
		}

		return diasConMenu;

	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static MenuDia menuDiaJson2MenuDia(JSONObject menuDiaJson)
			throws JSONException {

		MenuLocal menu = GestionMenu.menuJson2Menu(menuDiaJson
				.getJSONObject(JSON_MENU));

		List<Plato> platos = new ArrayList<Plato>();

		for (int i = 0; i < menuDiaJson.getJSONArray(JSON_PLATOS).length(); i++) {
			JSONObject platoJson = menuDiaJson.getJSONArray(JSON_PLATOS)
					.getJSONObject(i);

			Plato plato = GestionPlato.platoJson2Plato(platoJson);

			platos.add(plato);

		}

		MenuDia menuDia = new MenuDia(menuDiaJson.getInt(JSON_ID_MENU_DIA),
				menu, menuDiaJson.getString(JSON_FECHA), platos);

		return menuDia;
	}

}
