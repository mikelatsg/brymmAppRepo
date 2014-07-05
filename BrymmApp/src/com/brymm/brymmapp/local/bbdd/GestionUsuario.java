package com.brymm.brymmapp.local.bbdd;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Usuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionUsuario {

	public static final String JSON_USUARIO = "usuario";
	public static final String JSON_NICK = "nick";
	public static final String JSON_NOMBRE = "nombre";
	public static final String JSON_APELLIDO = "apellido";
	public static final String JSON_EMAIL = "email";
	public static final String JSON_LOCALIDAD = "localidad";
	public static final String JSON_PROVINCIA = "provincia";
	public static final String JSON_ID_USUARIO = "idUsuario";
	public static final String JSON_COD_POSTAL = "codPostal";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;

	public GestionUsuario(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
	}

	public GestionUsuario(SQLiteDatabase database) {
		this.database = database;
	}

	public void borrarUsuarios() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_USUARIOS, null, null);
	}

	public long guardarUsuario(Usuario usuario) {

		/*
		 * if (!database.isOpen()) { database =
		 * openHelper.getWritableDatabase(); }
		 */

		long resultado = 1;
		// Si el id del usuario es 0 no se guarda
		if (usuario.getIdUsuario() != 0) {

			Cursor cursor = database.query(LocalSQLite.TABLE_USUARIOS, null,
					LocalSQLite.COLUMN_US_ID_USUARIO + " = ?",
					new String[] { Integer.toString(usuario.getIdUsuario()) },
					null, null, null);

			ContentValues content = new ContentValues();

			content.put(LocalSQLite.COLUMN_US_APELLIDO, usuario.getApellido());
			content.put(LocalSQLite.COLUMN_US_COD_POSTAL,
					usuario.getCodPostal());
			content.put(LocalSQLite.COLUMN_US_EMAIL, usuario.getEmail());
			content.put(LocalSQLite.COLUMN_US_LOCALIDAD, usuario.getLocalidad());
			content.put(LocalSQLite.COLUMN_US_NICK, usuario.getNick());
			content.put(LocalSQLite.COLUMN_US_NOMBRE, usuario.getNombre());
			content.put(LocalSQLite.COLUMN_US_PROVINCIA, usuario.getProvincia());

			resultado = -1;

			if (!cursor.moveToFirst()) {
				content.put(LocalSQLite.COLUMN_US_ID_USUARIO,
						usuario.getIdUsuario());

				resultado = database.insert(LocalSQLite.TABLE_USUARIOS, null,
						content);
			} else {

				resultado = database
						.update(LocalSQLite.TABLE_USUARIOS, content,
								LocalSQLite.COLUMN_US_ID_USUARIO + " = ?",
								new String[] { Integer.toString(usuario
										.getIdUsuario()) });
			}

			cursor.close();
		}

		return resultado;
	}

	public Usuario obtenerUsuario(int idUsuario) {
		Usuario usuario = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_USUARIOS, null,
				LocalSQLite.COLUMN_US_ID_USUARIO + " = ?",
				new String[] { Integer.toString(idUsuario) }, null, null, null);

		while (cursor.moveToNext()) {

			usuario = new Usuario(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_US_ID_USUARIO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_US_NICK)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_US_NOMBRE)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_US_APELLIDO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_US_EMAIL)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_US_LOCALIDAD)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_US_PROVINCIA)),
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_US_COD_POSTAL)));

		}

		cursor.close();
		return usuario;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static Usuario usuarioJson2Usuario(JSONObject usuarioJson)
			throws JSONException {

		Usuario usuario = new Usuario(usuarioJson.getInt(JSON_ID_USUARIO),
				usuarioJson.getString(JSON_NICK),
				usuarioJson.getString(JSON_NOMBRE),
				usuarioJson.getString(JSON_APELLIDO),
				usuarioJson.getString(JSON_EMAIL),
				usuarioJson.getString(JSON_LOCALIDAD),
				usuarioJson.getString(JSON_PROVINCIA),
				usuarioJson.getInt(JSON_COD_POSTAL));

		return usuario;
	}

}
