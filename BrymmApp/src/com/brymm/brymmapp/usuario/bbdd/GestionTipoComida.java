package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;

import com.brymm.brymmapp.usuario.pojo.TipoComida;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTipoComida {

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionTipoComida(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarTiposComida() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_TIPOS_COMIDA, null, null);
	}

	public void guardarTipoComida(TipoComida tipoComida) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_TC_ID_TIPO_COMIDA,
				tipoComida.getIdTipoComida());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_TC_TIPO_COMIDA,
				tipoComida.getNombre());

		database.insert(UsuarioSQLiteOpenHelper.TABLE_TIPOS_COMIDA, null,
				content);

	}

	public ArrayList<TipoComida> obtenerTiposComida() {
		ArrayList<TipoComida> tiposComida = new ArrayList<TipoComida>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		
		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_TIPOS_COMIDA, null, null, null,
				null, null, null);

		while (cursor.moveToNext()) {
			tiposComida
					.add(new TipoComida(
							cursor.getInt(cursor
									.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TC_ID_TIPO_COMIDA)),
							cursor.getString(cursor
									.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TC_TIPO_COMIDA))));
		}

		cursor.close();
		cerrarDatabase();
		return tiposComida;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

}
