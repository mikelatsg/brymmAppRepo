package com.brymm.brymmapp.usuario.bbdd;

import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.HorarioPedido;
import com.brymm.brymmapp.usuario.pojo.IngredienteLocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionHorariosPedido {

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionHorariosPedido(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarHorariosPedido() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_HORARIOS_PEDIDO, null,
				null);
	}

	public void guardarHorarioPedido(HorarioPedido horario) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_HP_ID_HORARIO_PEDIDO,
				horario.getIdHorarioPedido());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_HP_ID_LOCAL,
				horario.getIdLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_HP_ID_DIA,
				horario.getIdDia());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_HP_HORA_INICIO,
				horario.getHoraInicio());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_HP_HORA_FIN,
				horario.getHoraFin());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_HP_DIA,
				horario.getDia());

		database.insert(UsuarioSQLiteOpenHelper.TABLE_HORARIOS_PEDIDO, null,
				content);

	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

}
