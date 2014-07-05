package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.IngredienteLocal;
import com.brymm.brymmapp.usuario.pojo.TipoArticulo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionTiposArticulo {

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionTiposArticulo(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarTiposArticulo() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_TIPOS_ARTICULO_LOCAL,
				null, null);
	}

	public boolean hayArticuloPersonalizable() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_TIPOS_ARTICULO_LOCAL, null,
				UsuarioSQLiteOpenHelper.COLUMN_TAL_PERSONALIZAR + " = ?",
				new String[] { "1" }, null, null, null);

		if (cursor.moveToFirst()) {
			if (!cursor.isClosed())
				cursor.close();
			return true;
		}

		if (!cursor.isClosed())
			cursor.close();

		return false;
	}

	public List<TipoArticulo> obtenerTiposArticuloPersonalizables() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		List<TipoArticulo> tiposArticulo = new ArrayList<TipoArticulo>();

		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_TIPOS_ARTICULO_LOCAL, null,
				UsuarioSQLiteOpenHelper.COLUMN_TAL_PERSONALIZAR + " = ?",
				new String[] { "1" }, null, null, null);

		while (cursor.moveToNext()) {
			TipoArticulo tipoArticulo = new TipoArticulo(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TAL_ID_TIPO_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TAL_TIPO_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TAL_DESCRIPCION)),
					1,
					cursor.getFloat(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_TAL_PRECIO)));

			tiposArticulo.add(tipoArticulo);
		}

		if (!cursor.isClosed())
			cursor.close();

		return tiposArticulo;
	}

	public void guardarTipoArticulo(TipoArticulo tipoArticulo) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL,
				tipoArticulo.getIdTipoArticuloLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_TAL_ID_TIPO_ARTICULO,
				tipoArticulo.getIdTipoArticuloLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_TAL_TIPO_ARTICULO,
				tipoArticulo.getNombre());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_TAL_DESCRIPCION,
				tipoArticulo.getDescripcion());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_TAL_PERSONALIZAR,
				tipoArticulo.getPersonalizar());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_TAL_PRECIO,
				tipoArticulo.getPrecio());

		database.insert(UsuarioSQLiteOpenHelper.TABLE_TIPOS_ARTICULO_LOCAL,
				null, content);

	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

}
