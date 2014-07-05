package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;

import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.TipoServicio;
import com.brymm.brymmapp.usuario.pojo.TipoComida;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTipoServicio {

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionTipoServicio(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarServicios() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_TIPOS_SERVICIOS, null,
				null);
	}

	public void guardarServicio(TipoServicio servicio) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_TS_ID_TIPO_SERVICIO,
				servicio.getIdServicioLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_TS_SERVICIO,
				servicio.getNombre());

		database.insert(UsuarioSQLiteOpenHelper.TABLE_TIPOS_SERVICIOS, null,
				content);

	}
	
	public ArrayList<TipoServicio> obtenerServicios() {
		ArrayList<TipoServicio> serviciosLocal = new ArrayList<TipoServicio>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		
		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_TIPOS_SERVICIOS, null, null, null,
				null, null, null);

		while (cursor.moveToNext()) {
			serviciosLocal
					.add(new TipoServicio(
							cursor.getInt(cursor
									.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TS_ID_TIPO_SERVICIO)),
							cursor.getString(cursor
									.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TS_SERVICIO))));
		}

		cursor.close();
		cerrarDatabase();
		return serviciosLocal;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

}
