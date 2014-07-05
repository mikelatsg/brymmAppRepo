package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.usuario.pojo.ArticuloLocal;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.IngredienteLocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionIngredientesLocal {

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionIngredientesLocal(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarIngredientesLocal() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_INGREDIENTES_LOCAL, null,
				null);
	}

	public void guardarIngredienteLocal(IngredienteLocal ingrediente) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_IL_ID_INGREDIENTE,
				ingrediente.getIdIngrediente());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_IL_ID_LOCAL,
				ingrediente.getIdLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_IL_INGREDIENTE,
				ingrediente.getNombre());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_IL_DESCRIPCION,
				ingrediente.getDescripcion());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_IL_PRECIO,
				ingrediente.getPrecio());

		database.insert(UsuarioSQLiteOpenHelper.TABLE_INGREDIENTES_LOCAL, null,
				content);

	}

	public List<IngredienteLocal> obtenerIngredientesLocal() {
		List<IngredienteLocal> ingredientes = new ArrayList<IngredienteLocal>();
		String sql = "SELECT il.* FROM "
				+ UsuarioSQLiteOpenHelper.TABLE_INGREDIENTES_LOCAL + " il "
				+ "ORDER BY il."
				+ UsuarioSQLiteOpenHelper.COLUMN_IL_INGREDIENTE;
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.rawQuery(sql, null);

		while (cursor.moveToNext()) {

			IngredienteLocal ingrediente = new IngredienteLocal(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_IL_ID_INGREDIENTE)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_IL_ID_LOCAL)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_IL_INGREDIENTE)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_IL_DESCRIPCION)),
					cursor.getFloat((cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_IL_PRECIO))));

			ingredientes.add(ingrediente);
		}

		cursor.close();
		return ingredientes;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

}
