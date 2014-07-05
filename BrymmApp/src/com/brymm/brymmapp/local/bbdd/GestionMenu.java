package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.local.pojo.TipoMenu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionMenu {

	public static final String JSON_MENUS = "menus";
	public static final String JSON_MENU = "menu";
	public static final String JSON_ID_MENU = "idMenu";
	public static final String JSON_TIPO_MENU = "tipoMenu";
	public static final String JSON_NOMBRE = "nombre";
	public static final String JSON_PRECIO = "precio";
	public static final String JSON_CARTA = "carta";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionMenu(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarMenus() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_MENUS, null, null);				
	}

	public void borrarMenu(int idMenu) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_MENUS, LocalSQLite.COLUMN_MN_ID_MENU
				+ " = ? ", new String[] { Integer.toString(idMenu) });
				
		Cursor cursor = database.query(LocalSQLite.TABLE_MENUS_DIA, null,
				LocalSQLite.COLUMN_MD_ID_MENU + " = ?",
				new String[] { Integer.toString(idMenu) }, null,
				null, null);
		
		//Se borran los platos asociados al menu en algun menu dia
		while (cursor.moveToNext()) {
			database.delete(LocalSQLite.TABLE_PLATOS_MENU_DIA,
					LocalSQLite.COLUMN_PMD_ID_MENU_DIA + " = ? ",
					new String[] { Integer.toString(cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_MD_ID_MENU_DIA))) });
		}
		
		//Se borran los menus dia del menu
		database.delete(LocalSQLite.TABLE_MENUS_DIA,
				LocalSQLite.COLUMN_MD_ID_MENU + " = ? ",
				new String[] { Integer.toString(idMenu) });
		
	}

	public void guardarMenu(MenuLocal menu) {

		Cursor cursor = database.query(LocalSQLite.TABLE_MENUS, null,
				LocalSQLite.COLUMN_MN_ID_MENU + " = ?",
				new String[] { Integer.toString(menu.getIdMenu()) }, null,
				null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_MN_ID_TIPO_MENU, menu.getTipoMenu()
				.getIdTipoMenu());
		content.put(LocalSQLite.COLUMN_MN_NOMBRE, menu.getNombre());
		content.put(LocalSQLite.COLUMN_MN_PRECIO, menu.getPrecio());
		content.put(LocalSQLite.COLUMN_MN_CARTA, menu.isCarta() ? 1 : 0);

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_MN_ID_MENU, menu.getIdMenu());

			database.insert(LocalSQLite.TABLE_MENUS, null, content);

		} else {

			database.update(LocalSQLite.TABLE_MENUS, content,
					LocalSQLite.COLUMN_MN_ID_MENU + " = ?",
					new String[] { Integer.toString(menu.getIdMenu()) });

		}

		cursor.close();

	}

	public List<MenuLocal> obtenerMenus() {
		List<MenuLocal> menus = new ArrayList<MenuLocal>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_MENUS, null, null,
				null, null, null, null);

		while (cursor.moveToNext()) {

			MenuLocal menu = obtenerMenu(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MN_ID_MENU)));

			menus.add(menu);
		}

		cursor.close();
		return menus;
	}

	public MenuLocal obtenerMenu(int idMenu) {
		MenuLocal menu = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_MENUS, null,
				LocalSQLite.COLUMN_MN_ID_MENU + " = ?",
				new String[] { Integer.toString(idMenu) }, null, null, null);

		while (cursor.moveToNext()) {

			GestionTipoMenu gtp = new GestionTipoMenu(context);
			TipoMenu tipoMenu = gtp.obtenerTipoMenu(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MN_ID_TIPO_MENU)));
			gtp.cerrarDatabase();

			menu = new MenuLocal(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MN_ID_MENU)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_MN_NOMBRE)),
					(float) cursor.getDouble(cursor
							.getColumnIndex(LocalSQLite.COLUMN_MN_PRECIO)),
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_MN_CARTA)) == 1,
					tipoMenu);

		}

		cursor.close();
		return menu;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static MenuLocal menuJson2Menu(JSONObject menuJson)
			throws JSONException {

		TipoMenu tipoMenu = GestionTipoMenu.tipoMenuJson2TipoMenu(menuJson
				.getJSONObject(JSON_TIPO_MENU));

		MenuLocal menu = new MenuLocal(menuJson.getInt(JSON_ID_MENU),
				menuJson.getString(JSON_NOMBRE),
				(float) menuJson.getDouble(JSON_PRECIO),
				menuJson.getInt(JSON_CARTA) == 1, tipoMenu);

		return menu;
	}

}
