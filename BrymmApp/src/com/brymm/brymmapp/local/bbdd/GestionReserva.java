package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.local.pojo.Reserva;
import com.brymm.brymmapp.local.pojo.TipoMenu;
import com.brymm.brymmapp.local.pojo.Usuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GestionReserva {

	public static final String JSON_RESERVAS = "reservas";
	public static final String JSON_RESERVA = "reserva";
	public static final String JSON_ID_RESERVA = "idReserva";
	public static final String JSON_USUARIO = "usuario";
	public static final String JSON_NUMERO_PERSONAS = "numeroPersonas";
	public static final String JSON_FECHA = "fecha";
	public static final String JSON_TIPO_MENU = "tipoMenu";
	public static final String JSON_HORA_INICIO = "horaInicio";
	public static final String JSON_HORA_FIN = "horaFin";
	public static final String JSON_ESTADO = "estado";
	public static final String JSON_MOTIVO = "motivo";
	public static final String JSON_OBSERVACIONES = "observaciones";
	public static final String JSON_NOMBRE_EMISOR = "nombreEmisor";
	public static final String JSON_MESAS = "mesas";

	public static final String ESTADO_PENDIENTE = "P";
	public static final String ESTADO_ANULADO_USUARIO = "AU";
	public static final String ESTADO_ACEPTADA_LOCAL = "AL";
	public static final String ESTADO_RECHAZADA_LOCAL = "RL";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionReserva(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarReservas() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_RESERVAS, null, null);
		database.delete(LocalSQLite.TABLE_MESAS_RESERVA, null, null);
	}

	public void guardarReserva(Reserva reserva) {

		/*
		 * if (!database.isOpen()) { database =
		 * openHelper.getWritableDatabase(); }
		 */

		boolean hayError = false;

		database.beginTransaction();

		Cursor cursor = database.query(LocalSQLite.TABLE_RESERVAS, null,
				LocalSQLite.COLUMN_RS_ID_RESERVA + " = ?",
				new String[] { Integer.toString(reserva.getIdReserva()) },
				null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_RS_ESTADO, reserva.getEstado());
		content.put(LocalSQLite.COLUMN_RS_FECHA, reserva.getFecha());
		content.put(LocalSQLite.COLUMN_RS_HORA_FIN, reserva.getHoraFin());
		content.put(LocalSQLite.COLUMN_RS_HORA_INICIO, reserva.getHoraInicio());
		content.put(LocalSQLite.COLUMN_RS_ID_TIPO_MENU, reserva.getTipoMenu()
				.getIdTipoMenu());
		content.put(LocalSQLite.COLUMN_RS_ID_USUARIO, reserva.getUsuario()
				.getIdUsuario());
		content.put(LocalSQLite.COLUMN_RS_MOTIVO, reserva.getMotivo());
		content.put(LocalSQLite.COLUMN_RS_OBSERVACIONES,
				reserva.getObservaciones());
		content.put(LocalSQLite.COLUMN_RS_NOMBRE_EMISOR,
				reserva.getNombreEmisor());
		content.put(LocalSQLite.COLUMN_RS_NUMERO_PERSONAS,
				reserva.getNumeroPersonas());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_RS_ID_RESERVA,
					reserva.getIdReserva());

			if (database.insert(LocalSQLite.TABLE_RESERVAS, null, content) < 0) {
				hayError = true;
			}
			// Se insertan las mesas en la tabla mesas_reserva
			for (Mesa mesa : reserva.getMesas()) {
				ContentValues contentMesa = new ContentValues();

				contentMesa
						.put(LocalSQLite.COLUMN_MR_ID_MESA, mesa.getIdMesa());
				contentMesa.put(LocalSQLite.COLUMN_MR_ID_RESERVA,
						reserva.getIdReserva());

				if (database.insert(LocalSQLite.TABLE_MESAS_RESERVA, null,
						contentMesa) < 0) {
					hayError = true;
				}
			}

			// Se inserta el usuario
			GestionUsuario gu = new GestionUsuario(this.database);
			if (gu.guardarUsuario(reserva.getUsuario()) < 0) {
				hayError = true;
			}

		} else {

			if (database.update(LocalSQLite.TABLE_RESERVAS, content,
					LocalSQLite.COLUMN_RS_ID_RESERVA + " = ?",
					new String[] { Integer.toString(reserva.getIdReserva()) }) < 0) {
				hayError = true;
			}

			// Se borran los mesas
			if (database.delete(LocalSQLite.TABLE_MESAS_RESERVA,
					LocalSQLite.COLUMN_MR_ID_RESERVA + " = ? ",
					new String[] { Integer.toString(reserva.getIdReserva()) }) < 0) {
				hayError = true;
			}

			// Se insertan las mesas en la tabla mesas_reserva
			for (Mesa mesa : reserva.getMesas()) {
				ContentValues contentMesa = new ContentValues();

				contentMesa
						.put(LocalSQLite.COLUMN_MR_ID_MESA, mesa.getIdMesa());
				contentMesa.put(LocalSQLite.COLUMN_MR_ID_RESERVA,
						reserva.getIdReserva());

				if (database.insert(LocalSQLite.TABLE_MESAS_RESERVA, null,
						contentMesa) < 0) {
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

	public List<Reserva> obtenerReservas(String estado) {
		List<Reserva> reservas = new ArrayList<Reserva>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_RESERVAS, null,
				LocalSQLite.COLUMN_RS_ESTADO + " = ?", new String[] { estado },
				null, null, null);

		while (cursor.moveToNext()) {

			Reserva reserva = obtenerReserva(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_RS_ID_RESERVA)));

			reservas.add(reserva);
		}

		cursor.close();
		return reservas;
	}
	
	public List<Reserva> obtenerReservasDia(String fecha) {
		List<Reserva> reservas = new ArrayList<Reserva>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_RESERVAS, null,
				LocalSQLite.COLUMN_RS_FECHA + " = ?", new String[] { fecha },
				null, null, null);

		while (cursor.moveToNext()) {

			Reserva reserva = obtenerReserva(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_RS_ID_RESERVA)));

			reservas.add(reserva);
		}

		cursor.close();
		return reservas;
	}

	public List<Mesa> obtenerMesasReserva(int idReserva) {
		List<Mesa> mesas = new ArrayList<Mesa>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_MESAS_RESERVA, null,
				LocalSQLite.COLUMN_MR_ID_RESERVA + " = ?",
				new String[] { Integer.toString(idReserva) }, null, null, null);

		GestionMesa gestor = new GestionMesa(context);

		while (cursor.moveToNext()) {
			Mesa mesa = gestor.obtenerMesa(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MR_ID_MESA)));

			mesas.add(mesa);
		}

		gestor.cerrarDatabase();

		return mesas;
	}

	public List<Mesa> obtenerMesasLibres(String fecha, int idTipoMenu) {
		List<Mesa> mesas = new ArrayList<Mesa>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.rawQuery(" SELECT * FROM "
				+ LocalSQLite.TABLE_MESAS + " WHERE "
				+ LocalSQLite.COLUMN_MS_ID_MESA + " NOT IN " + "(SELECT "
				+ LocalSQLite.COLUMN_MR_ID_MESA + " FROM "
				+ LocalSQLite.TABLE_MESAS_RESERVA + " WHERE "
				+ LocalSQLite.COLUMN_MR_ID_RESERVA + " in " + "(SELECT "
				+ LocalSQLite.COLUMN_RS_ID_RESERVA + " FROM "
				+ LocalSQLite.TABLE_RESERVAS + " WHERE "
				+ LocalSQLite.COLUMN_RS_FECHA + " = ?" + " AND "
				+ LocalSQLite.COLUMN_RS_ID_TIPO_MENU + " = ?))", new String[] {
				fecha, Integer.toString(idTipoMenu) });

		GestionMesa gestor = new GestionMesa(context);

		while (cursor.moveToNext()) {
			Mesa mesa = gestor.obtenerMesa(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_MS_ID_MESA)));

			mesas.add(mesa);
		}

		gestor.cerrarDatabase();

		return mesas;
	}

	public Reserva obtenerReserva(int idReserva) {
		Reserva reserva = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_RESERVAS, null,
				LocalSQLite.COLUMN_RS_ID_RESERVA + " = ?",
				new String[] { Integer.toString(idReserva) }, null, null, null);

		while (cursor.moveToNext()) {

			GestionTipoMenu gtm = new GestionTipoMenu(context);
			TipoMenu tipoMenu = gtm.obtenerTipoMenu(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_RS_ID_TIPO_MENU)));
			gtm.cerrarDatabase();

			Usuario usuario = null;
			if (cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_RS_ID_USUARIO)) != 0) {
				GestionUsuario gu = new GestionUsuario(context);
				usuario = gu.obtenerUsuario(cursor.getInt(cursor
						.getColumnIndex(LocalSQLite.COLUMN_RS_ID_USUARIO)));
				gu.cerrarDatabase();
			}

			List<Mesa> mesas = obtenerMesasReserva(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_RS_ID_RESERVA)));

			reserva = new Reserva(
					idReserva,
					usuario,
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_RS_NUMERO_PERSONAS)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_RS_FECHA)),
					tipoMenu,
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_RS_HORA_INICIO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_RS_HORA_FIN)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_RS_ESTADO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_RS_MOTIVO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_RS_OBSERVACIONES)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_RS_NOMBRE_EMISOR)),
					mesas);

		}

		cursor.close();
		return reserva;
	}

	public List<String> obtenerDiasReservaMes(String anoMes) {
		List<String> diasConReserva = new ArrayList<String>();

		/*Cursor cursor = database.query(LocalSQLite.TABLE_RESERVAS, null,
				LocalSQLite.COLUMN_RS_FECHA + " LIKE ? AND "
						+ LocalSQLite.COLUMN_RS_ESTADO + " IN ( ? , ? ) ",
				new String[] { anoMes + "%",
						GestionReserva.ESTADO_ACEPTADA_LOCAL,
						GestionReserva.ESTADO_PENDIENTE }, null, null, null);*/

		Cursor cursor = database.rawQuery("SELECT DISTINCT "
				+ LocalSQLite.COLUMN_RS_FECHA + " FROM "
				+ LocalSQLite.TABLE_RESERVAS + " WHERE "
				+ LocalSQLite.COLUMN_RS_FECHA + " LIKE ? AND "
				+ LocalSQLite.COLUMN_RS_ESTADO + " IN ( ? , ? ) ",
				new String[] { anoMes + "%",
						GestionReserva.ESTADO_ACEPTADA_LOCAL,
						GestionReserva.ESTADO_PENDIENTE });

		while (cursor.moveToNext()) {
			String dia = cursor.getString(
					cursor.getColumnIndex(LocalSQLite.COLUMN_RS_FECHA)).split(
					"-")[2];

			diasConReserva.add(dia);
		}

		return diasConReserva;

	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Reserva reservaJson2Reserva(JSONObject reservaJson)
			throws JSONException {

		TipoMenu tipoMenu = GestionTipoMenu.tipoMenuJson2TipoMenu(reservaJson
				.getJSONObject(JSON_TIPO_MENU));

		Usuario usuario = GestionUsuario.usuarioJson2Usuario(reservaJson
				.getJSONObject(JSON_USUARIO));

		List<Mesa> mesas = new ArrayList<Mesa>();

		for (int i = 0; i < reservaJson.getJSONArray(JSON_MESAS).length(); i++) {
			JSONObject mesaJson = reservaJson.getJSONArray(
					GestionMesa.JSON_MESAS).getJSONObject(i);

			Mesa mesa = GestionMesa.mesaJson2Mesa(mesaJson);

			mesas.add(mesa);

		}

		Reserva reserva = new Reserva(reservaJson.getInt(JSON_ID_RESERVA),
				usuario, reservaJson.getInt(JSON_NUMERO_PERSONAS),
				reservaJson.getString(JSON_FECHA), tipoMenu,
				reservaJson.getString(JSON_HORA_INICIO),
				reservaJson.getString(JSON_HORA_FIN),
				reservaJson.getString(JSON_ESTADO),
				reservaJson.getString(JSON_MOTIVO),
				reservaJson.getString(JSON_OBSERVACIONES),
				reservaJson.getString(JSON_NOMBRE_EMISOR), mesas);

		return reserva;
	}

}
