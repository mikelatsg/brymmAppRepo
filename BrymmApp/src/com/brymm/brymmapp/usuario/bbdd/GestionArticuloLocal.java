package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.usuario.pojo.ArticuloLocal;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalLista;
import com.brymm.brymmapp.usuario.pojo.ServicioLocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GestionArticuloLocal {

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionArticuloLocal(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarArticulosLocal() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_ARTICULOS_LOCAL, null,
				null);
		database.delete(UsuarioSQLiteOpenHelper.TABLE_DETALLE_ARTICULO, null,
				null);
	}

	public void guardarArticuloLocal(ArticuloLocal articulo) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_ARTICULO,
				articulo.getIdArticulo());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_LOCAL,
				articulo.getIdLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_TIPO_ARTICULO,
				articulo.getIdTipoArticulo());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AL_ARTICULO,
				articulo.getNombre());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AL_DESCRIPCION,
				articulo.getDescripcion());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AL_PRECIO,
				articulo.getPrecio());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_AL_TIPO_ARTICULO,
				articulo.getTipoArticulo());

		database.beginTransaction();

		long resultado = database.insert(
				UsuarioSQLiteOpenHelper.TABLE_ARTICULOS_LOCAL, null, content);

		boolean errorInsertandoDetalleArticulo = false;
		if (resultado > 0) {
			ArrayList<String> ingredientes = (ArrayList<String>) articulo
					.getIngredientes();
			for (String ingrediente : ingredientes) {
				long resultadoArticulo = guardarIngredientesArticulo(
						articulo.getIdArticulo(), ingrediente);
				if (resultadoArticulo < 0) {
					errorInsertandoDetalleArticulo = true;
				}
			}
		}

		if (resultado > 0 && !errorInsertandoDetalleArticulo) {
			database.setTransactionSuccessful();
		}

		database.endTransaction();

	}

	public List<ArticuloLocal> obtenerArticulosLocal() {
		List<ArticuloLocal> articulos = new ArrayList<ArticuloLocal>();
		String sql = "SELECT al.* FROM "
				+ UsuarioSQLiteOpenHelper.TABLE_ARTICULOS_LOCAL + " al "
				+ "ORDER BY al."
				+ UsuarioSQLiteOpenHelper.COLUMN_AL_ID_TIPO_ARTICULO;
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.rawQuery(sql, null);

		while (cursor.moveToNext()) {
			List<String> ingredientes = new ArrayList<String>();

			String sqlIngredientes = "SELECT da.* FROM "
					+ UsuarioSQLiteOpenHelper.TABLE_DETALLE_ARTICULO + " da "
					+ "WHERE da."
					+ UsuarioSQLiteOpenHelper.COLUMN_DA_ID_ARTICULO_LOCAL
					+ " = ?";

			Cursor cursorIng = database
					.rawQuery(
							sqlIngredientes,
							new String[] { Integer.toString(cursor.getInt(cursor
									.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_ARTICULO))) });

			while (cursorIng.moveToNext()) {
				ingredientes
						.add(cursorIng.getString(cursorIng
								.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_DA_INGREDIENTE)));
			}

			if (!cursorIng.isClosed()) {
				cursorIng.close();
			}

			ArticuloLocal articulo = new ArticuloLocal(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_ARTICULO)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_LOCAL)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_TIPO_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_DESCRIPCION)),
					cursor.getFloat(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_PRECIO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_TIPO_ARTICULO)),
					ingredientes);

			articulos.add(articulo);
		}

		cursor.close();
		return articulos;
	}

	public List<ArticuloLocalLista> obtenerArticulosLocalLista() {
		List<ArticuloLocalLista> articulos = new ArrayList<ArticuloLocalLista>();
		String sql = "SELECT al.* FROM "
				+ UsuarioSQLiteOpenHelper.TABLE_ARTICULOS_LOCAL + " al "
				+ "ORDER BY al."
				+ UsuarioSQLiteOpenHelper.COLUMN_AL_ID_TIPO_ARTICULO;
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.rawQuery(sql, null);

		int idTipoArticuloAnterior = -1;
		while (cursor.moveToNext()) {
			List<String> ingredientes = new ArrayList<String>();

			String sqlIngredientes = "SELECT da.* FROM "
					+ UsuarioSQLiteOpenHelper.TABLE_DETALLE_ARTICULO + " da "
					+ "WHERE da."
					+ UsuarioSQLiteOpenHelper.COLUMN_DA_ID_ARTICULO_LOCAL
					+ " = ?";

			Cursor cursorIng = database
					.rawQuery(
							sqlIngredientes,
							new String[] { Integer.toString(cursor.getInt(cursor
									.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_ARTICULO))) });

			while (cursorIng.moveToNext()) {
				ingredientes
						.add(cursorIng.getString(cursorIng
								.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_DA_INGREDIENTE)));
			}

			if (!cursorIng.isClosed()) {
				cursorIng.close();
			}

			ArticuloLocalLista articulo = new ArticuloLocalLista(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_ARTICULO)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_LOCAL)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ID_TIPO_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_ARTICULO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_DESCRIPCION)),
					cursor.getFloat(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_PRECIO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_AL_TIPO_ARTICULO)),
					ingredientes, false);

			if (idTipoArticuloAnterior != articulo.getIdTipoArticulo()) {
				ArticuloLocalLista articuloSeccion = new ArticuloLocalLista(
						articulo, true);
				articulos.add(articuloSeccion);
			}

			articulos.add(articulo);
		}

		cursor.close();
		return articulos;
	}

	private long guardarIngredientesArticulo(int idArticulo, String ingrediente) {
		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_DA_INGREDIENTE, ingrediente);
		content.put(UsuarioSQLiteOpenHelper.COLUMN_DA_ID_ARTICULO_LOCAL,
				idArticulo);

		long idDetalle = database.insert(
				UsuarioSQLiteOpenHelper.TABLE_DETALLE_ARTICULO, null, content);

		return idDetalle;

	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}
}
