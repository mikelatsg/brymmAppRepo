package com.brymm.brymmapp.usuario.bbdd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.servicios.ServicioDatosUsuario;
import com.brymm.brymmapp.usuario.pojo.ArticuloPedido;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.IngredienteLocal;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionDireccion {

	public static final String JSON_ID_DIRECCION_ENVIO = "id_direccion_envio";
	public static final String JSON_NOMBRE_DIRECCION = "nombre";
	public static final String JSON_DIRECCION = "direccion";
	public static final String JSON_POBLACION = "poblacion";
	public static final String JSON_PROVINCIA = "provincia";	
	public static final String JSON_DIRECCIONES = "direcciones";

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionDireccion(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarDirecciones() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_DIRECCIONES, null, null);
	}

	public void borrarDireccion(int idDireccion) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_DIRECCIONES,
				UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION + " = ? ",
				new String[] { Integer.toString(idDireccion) });
	}

	public void guardarDireccion(Direccion direccion) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		// Se comprueba si existe el registro, si existe se modifica, sino se
		// inserta
		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_DIRECCIONES, null,
				UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION + " = ?",
				new String[] { Integer.toString(direccion.getIdDireccion()) },
				null, null, null);
		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_DIRECCION,
				direccion.getDireccion());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_NOMBRE,
				direccion.getNombre());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_POBLACION,
				direccion.getPoblacion());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_PROVINCIA,
				direccion.getProvincia());

		if (!cursor.moveToFirst()) {
			content.put(UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION,
					direccion.getIdDireccion());

			database.insert(UsuarioSQLiteOpenHelper.TABLE_DIRECCIONES, null,
					content);
		} else {

			database.update(
					UsuarioSQLiteOpenHelper.TABLE_DIRECCIONES,
					content,
					UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION + " = ?",
					new String[] { Integer.toString(direccion.getIdDireccion()) });
		}

		cursor.close();
	}	

	public List<Direccion> obtenerDireccionesUsuario() {
		List<Direccion> direcciones = new ArrayList<Direccion>();
		String sql = "SELECT d.* FROM "
				+ UsuarioSQLiteOpenHelper.TABLE_DIRECCIONES + " d "
				+ "ORDER BY d." + UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION;
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		Cursor cursor = database.rawQuery(sql, null);

		while (cursor.moveToNext()) {

			Direccion direccion = new Direccion(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_NOMBRE)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_DIRECCION)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_POBLACION)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_PROVINCIA)));

			direcciones.add(direccion);
		}

		cursor.close();
		return direcciones;
	}

	public Direccion obtenerDireccionUsuario(int idDireccion) {
		Direccion direccion = null;
		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_DIRECCIONES, null,
				UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION + " = ?",
				new String[] { Integer.toString(idDireccion) }, null, null,
				null);

		while (cursor.moveToNext()) {

			direccion = new Direccion(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_ID_DIRECCION)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_NOMBRE)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_DIRECCION)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_POBLACION)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_PROVINCIA)));

		}

		cursor.close();
		return direccion;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Direccion direccionJson2Direccion(JSONObject direccionJson)
			throws JSONException {

		Direccion direccion = new Direccion(
				direccionJson.getInt(JSON_ID_DIRECCION_ENVIO),
				direccionJson.getString(JSON_NOMBRE_DIRECCION),
				direccionJson.getString(JSON_DIRECCION),
				direccionJson.getString(JSON_POBLACION),
				direccionJson.getString(JSON_PROVINCIA));

		return direccion;
	}

}
