package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.DiaSemana;
import com.brymm.brymmapp.local.pojo.HorarioPedido;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionHorarioPedido {

	public static final String JSON_HORARIOS_PEDIDO = "horariosPedido";
	public static final String JSON_HORARIO_PEDIDO = "horarioPedido";
	public static final String JSON_ID_HORARIO_PEDIDO = "idHorarioPedido";
	public static final String JSON_HORA_INICIO = "horaInicio";
	public static final String JSON_HORA_FIN = "horaFin";
	public static final String JSON_DIA = "dia";
	public static final String JSON_ID_LOCAL = "idLocal";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionHorarioPedido(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarHorariosPedido() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_HORARIOS_PEDIDO, null, null);
	}

	public void borrarHorarioPedido(int idHorarioPedido) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_HORARIOS_PEDIDO,
				LocalSQLite.COLUMN_HP_ID_HORARIO_PEDIDO + " = ? ",
				new String[] { Integer.toString(idHorarioPedido) });
	}

	public void guardarHorarioPedido(HorarioPedido horarioPedido) {

		Cursor cursor = database.query(LocalSQLite.TABLE_HORARIOS_PEDIDO, null,
				LocalSQLite.COLUMN_HP_ID_HORARIO_PEDIDO + " = ?",
				new String[] { Integer.toString(horarioPedido
						.getIdHorarioPedido()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_HP_HORA_FIN, horarioPedido.getHoraFin());
		content.put(LocalSQLite.COLUMN_HP_HORA_INICIO,
				horarioPedido.getHoraInicio());
		content.put(LocalSQLite.COLUMN_HP_ID_DIA, horarioPedido.getDia()
				.getIdDia());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_HP_ID_HORARIO_PEDIDO,
					horarioPedido.getIdHorarioPedido());

			database.insert(LocalSQLite.TABLE_HORARIOS_PEDIDO, null, content);

		} else {

			database.update(LocalSQLite.TABLE_HORARIOS_PEDIDO, content,
					LocalSQLite.COLUMN_HP_ID_HORARIO_PEDIDO+ " = ?",
					new String[] { Integer.toString(horarioPedido
							.getIdHorarioPedido()) });
		}

		cursor.close();

	}

	public List<HorarioPedido> obtenerHorariosPedido() {
		List<HorarioPedido> horariosPedido = new ArrayList<HorarioPedido>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_HORARIOS_PEDIDO, null,
				null, null, null, null, null);

		while (cursor.moveToNext()) {

			HorarioPedido horarioPedido = obtenerHorarioPedido(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_HP_ID_HORARIO_PEDIDO)));

			horariosPedido.add(horarioPedido);
		}

		cursor.close();
		return horariosPedido;
	}

	public HorarioPedido obtenerHorarioPedido(int idHorarioPedido) {
		HorarioPedido horarioPedido = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_HORARIOS_PEDIDO, null,
				LocalSQLite.COLUMN_HP_ID_HORARIO_PEDIDO + " = ?",
				new String[] { Integer.toString(idHorarioPedido) }, null, null,
				null);

		while (cursor.moveToNext()) {

			GestionDiaSemana gds = new GestionDiaSemana(context);
			DiaSemana diaSemana = gds.obtenerDiaSemana(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_HP_ID_DIA)));

			horarioPedido = new HorarioPedido(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_HP_ID_HORARIO_PEDIDO)),
					diaSemana,
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_HP_HORA_INICIO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_HP_HORA_FIN)));

		}

		cursor.close();
		return horarioPedido;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static HorarioPedido horarioPedidoJson2HorarioPedido(
			JSONObject horarioPedidoJson) throws JSONException {

		DiaSemana diaSemana = GestionDiaSemana
				.diaSemanaJson2DiaSemana(horarioPedidoJson
						.getJSONObject(JSON_DIA));

		HorarioPedido horarioPedido = new HorarioPedido(
				horarioPedidoJson.getInt(JSON_ID_HORARIO_PEDIDO), diaSemana,
				horarioPedidoJson.getString(JSON_HORA_INICIO),
				horarioPedidoJson.getString(JSON_HORA_FIN));

		return horarioPedido;
	}

}
