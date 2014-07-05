package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.DiaCierreReserva;
import com.brymm.brymmapp.local.pojo.TipoMenu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionDiaCierreReserva {

	public static final String JSON_DIAS_CIERRE_RESERVA = "diasCierreReserva";
	public static final String JSON_DIA_CIERRE_RESERVA = "diaCierreReserva";
	public static final String JSON_ID_DIA_CIERRE_RESERVA = "idDiaCierreReserva";
	public static final String JSON_FECHA = "fecha";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionDiaCierreReserva(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public GestionDiaCierreReserva(SQLiteDatabase database) {
		this.database = database;
	}

	public void borrarDiasCierreReserva() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_DIAS_CIERRE_RESERVA, null, null);
	}

	public void borrarDiaCierre(int idDiaCierreReserva) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_DIAS_CIERRE_RESERVA,
				LocalSQLite.COLUMN_DCR_ID_DIA_CIERRE_RESERVA + " = ?",
				new String[] { Integer.toString(idDiaCierreReserva) });
	}

	public void borrarDiaCierre(int idTipoMenu, String fecha) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_DIAS_CIERRE_RESERVA,
				LocalSQLite.COLUMN_DCR_ID_TIPO_MENU + " = ? AND "
						+ LocalSQLite.COLUMN_DCR_FECHA + " = ? ", new String[] {
						Integer.toString(idTipoMenu), fecha });
	}

	public long guardarDiaCierreReserva(DiaCierreReserva diaCierreReserva) {

		long resultado = -1;

		Cursor cursor = database.query(LocalSQLite.TABLE_DIAS_CIERRE_RESERVA,
				null, LocalSQLite.COLUMN_DCR_ID_DIA_CIERRE_RESERVA + " = ?",
				new String[] { Integer.toString(diaCierreReserva
						.getIdDiaCierreReserva()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_DCR_FECHA, diaCierreReserva.getFecha());
		content.put(LocalSQLite.COLUMN_DCR_ID_TIPO_MENU, diaCierreReserva
				.getTipoMenu().getIdTipoMenu());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_DCR_ID_DIA_CIERRE_RESERVA,
					diaCierreReserva.getIdDiaCierreReserva());

			resultado = database.insert(LocalSQLite.TABLE_DIAS_CIERRE_RESERVA,
					null, content);
		} else {

			resultado = database.update(LocalSQLite.TABLE_DIAS_CIERRE_RESERVA,
					content, LocalSQLite.COLUMN_DCR_ID_DIA_CIERRE_RESERVA
							+ " = ?",
					new String[] { Integer.toString(diaCierreReserva
							.getIdDiaCierreReserva()) });
		}

		cursor.close();

		return resultado;
	}

	public List<DiaCierreReserva> obtenerDiasCierreReserva() {
		List<DiaCierreReserva> diasCierreReserva = new ArrayList<DiaCierreReserva>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.query(LocalSQLite.TABLE_DIAS_CIERRE, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			DiaCierreReserva diaCierreReserva = obtenerDiaCierreReserva(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DCR_ID_DIA_CIERRE_RESERVA)));

			diasCierreReserva.add(diaCierreReserva);
		}

		cursor.close();
		return diasCierreReserva;
	}

	public DiaCierreReserva obtenerDiaCierreReserva(int idDiaCierreReserva) {
		DiaCierreReserva diaCierreReserva = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_DIAS_CIERRE_RESERVA,
				null, LocalSQLite.COLUMN_DCR_ID_DIA_CIERRE_RESERVA + " = ?",
				new String[] { Integer.toString(idDiaCierreReserva) }, null,
				null, null);

		while (cursor.moveToNext()) {

			GestionTipoMenu gestor = new GestionTipoMenu(this.context);
			TipoMenu tipoMenu = gestor.obtenerTipoMenu(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_DCR_ID_TIPO_MENU)));
			gestor.cerrarDatabase();

			diaCierreReserva = new DiaCierreReserva(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DCR_ID_DIA_CIERRE_RESERVA)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DCR_FECHA)),
					tipoMenu);

		}

		cursor.close();
		return diaCierreReserva;
	}

	public boolean comprobarReservasCerradas(String fecha, int idTipoMenu) {
		Cursor cursor = database
				.query(LocalSQLite.TABLE_DIAS_CIERRE_RESERVA, null,
						LocalSQLite.COLUMN_DCR_ID_TIPO_MENU + " = ? AND "
								+ LocalSQLite.COLUMN_DCR_FECHA + " = ?",
						new String[] { Integer.toString(idTipoMenu), fecha },
						null, null, null);

		if (cursor.moveToNext()) {
			return true;
		}
		return false;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static DiaCierreReserva diaCierreReservaJson2DiaCierreReserva(
			JSONObject diaCierreReservaJson) throws JSONException {

		TipoMenu tipoMenu = GestionTipoMenu
				.tipoMenuJson2TipoMenu(diaCierreReservaJson
						.getJSONObject(GestionTipoMenu.JSON_TIPO_MENU));

		DiaCierreReserva diaCierreReserva = new DiaCierreReserva(
				diaCierreReservaJson.getInt(JSON_ID_DIA_CIERRE_RESERVA),
				diaCierreReservaJson.getString(JSON_FECHA), tipoMenu);

		return diaCierreReserva;
	}

}
