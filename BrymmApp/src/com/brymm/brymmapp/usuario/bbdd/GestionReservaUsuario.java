package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.ReservaUsuario;

public class GestionReservaUsuario {
	
	public static final String JSON_ULTIMAS_RESERVAS = "ultimasReservas";
	public static final String JSON_ID_RESERVA = "id_reserva";
	public static final String JSON_ID_LOCAL = "id_local";
	public static final String JSON_NUMERO_PERSONAS = "numero_personas";
	public static final String JSON_FECHA = "fecha";
	public static final String JSON_HORA_INICIO = "hora_inicio";
	public static final String JSON_ESTADO = "estado";
	public static final String JSON_MOTIVO = "motivo";
	public static final String JSON_OBSERVACIONES = "observaciones";
	public static final String JSON_NOMBRE_LOCAL = "nombreLocal";
	public static final String JSON_TIPO_MENU = "tipoMenu";
	public static final String JSON_RESERVA = "reserva";
	public static final String JSON_ID_USUARIO = "idUsuario";
	
	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionReservaUsuario(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarReservas() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_RESERVAS, null, null);
	}

	public void guardarReserva(ReservaUsuario ru) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_ID_LOCAL, ru.getIdLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_NUMERO_PERSONAS,
				ru.getNumeroPersonas());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_FECHA, ru.getFecha());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_HORA, ru.getHora());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_ESTADO, ru.getEstado());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_MOTIVO, ru.getMotivo());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_OBSERVACIONES,
				ru.getObservaciones());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_NOMBRE_LOCAL,
				ru.getNombreLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_R_TIPO_MENU,
				ru.getTipoMenu());

		// Se comprueba si existe el registro, si existe se modifica, sino se
		// inserta
		Cursor cursor = database.query(UsuarioSQLiteOpenHelper.TABLE_RESERVAS,
				null, UsuarioSQLiteOpenHelper.COLUMN_R_ID_RESERVA + " = ?",
				new String[] { Integer.toString(ru.getIdReserva()) }, null,
				null, null);

		if (!cursor.moveToFirst()) {
			content.put(UsuarioSQLiteOpenHelper.COLUMN_R_ID_RESERVA,
					ru.getIdReserva());

			database.insert(UsuarioSQLiteOpenHelper.TABLE_RESERVAS, null,
					content);
		} else {

			database.update(UsuarioSQLiteOpenHelper.TABLE_RESERVAS, content,
					UsuarioSQLiteOpenHelper.COLUMN_R_ID_RESERVA + " = ?",
					new String[] { Integer.toString(ru.getIdReserva()) });
		}

		cursor.close();

	}

	public List<ReservaUsuario> obtenerReservasUsuario() {
		List<ReservaUsuario> reservas = new ArrayList<ReservaUsuario>();
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(UsuarioSQLiteOpenHelper.TABLE_RESERVAS,
				null, null, null, null, null,
				UsuarioSQLiteOpenHelper.COLUMN_R_ID_RESERVA + " DESC");

		while (cursor.moveToNext()) {

			ReservaUsuario reserva = new ReservaUsuario(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_ID_RESERVA)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_ID_LOCAL)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_NUMERO_PERSONAS)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_FECHA)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_HORA)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_ESTADO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_MOTIVO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_OBSERVACIONES)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_NOMBRE_LOCAL)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_R_TIPO_MENU)));

			reservas.add(reserva);
		}

		cursor.close();
		return reservas;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}
	
	public static ReservaUsuario reservaJson2ReservaUsuario(JSONObject reservaJson)
			throws JSONException {

		ReservaUsuario reserva = new ReservaUsuario(
				reservaJson.getInt(JSON_ID_RESERVA),
				reservaJson.getInt(JSON_ID_LOCAL),
				reservaJson.getInt(JSON_NUMERO_PERSONAS),
				reservaJson.getString(JSON_FECHA),
				reservaJson.getString(JSON_HORA_INICIO),
				reservaJson.getString(JSON_ESTADO),
				reservaJson.getString(JSON_MOTIVO),
				reservaJson.getString(JSON_OBSERVACIONES),
				reservaJson.getString(JSON_NOMBRE_LOCAL),
				reservaJson.getString(JSON_TIPO_MENU));

		return reserva;
	}

}
