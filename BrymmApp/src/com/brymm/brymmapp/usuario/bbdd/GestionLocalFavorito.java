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
import com.brymm.brymmapp.usuario.pojo.IngredienteLocal;
import com.brymm.brymmapp.usuario.pojo.Local;
import com.brymm.brymmapp.usuario.pojo.LocalFavorito;

public class GestionLocalFavorito {

	public static final String JSON_LF_ID_LOCAL_FAVORITO = "id_locales_favoritos";
	public static final String JSON_LF_ID_LOCAL = "id_local";
	public static final String JSON_LF_NOMBRE_LOCAL = "nombre";
	public static final String JSON_LF_LOCALIDAD = "localidad";
	public static final String JSON_LF_PROVINCIA = "provincia";
	public static final String JSON_LF_DIRECCION = "direccion";
	public static final String JSON_LF_CODIGO_POSTAL = "cod_postal";
	public static final String JSON_LF_EMAIL = "email";
	public static final String JSON_LF_TELEFONO = "telefono";
	public static final String JSON_LF_TIPO_COMIDA = "tipo_comida";
	public static final String JSON_LOCAL_FAVORITO = "localFavorito";
	public static final String JSON_LOCALES_FAVORITOS = "localesFavoritos";

	SQLiteDatabase database;
	UsuarioSQLiteOpenHelper openHelper;

	public GestionLocalFavorito(Context context) {
		openHelper = new UsuarioSQLiteOpenHelper(context);
		database = openHelper.getWritableDatabase();
	}

	public void borrarLocalesFavoritos() {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_LOCALES_FAVORITOS, null,
				null);
	}

	public void borrarLocalFavorito(int idLocal) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(UsuarioSQLiteOpenHelper.TABLE_LOCALES_FAVORITOS,
				UsuarioSQLiteOpenHelper.COLUMN_LF_ID_LOCAL + " = ?",
				new String[] { Integer.toString(idLocal) });
	}

	public boolean comprobarEsFavorito(int idLocal) {
		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_LOCALES_FAVORITOS, null,
				UsuarioSQLiteOpenHelper.COLUMN_LF_ID_LOCAL + " = ?",
				new String[] { Integer.toString(idLocal) }, null, null, null);

		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	public List<LocalFavorito> obtenerLocalesFavoritos() {
		List<LocalFavorito> locales = new ArrayList<LocalFavorito>();
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(
				UsuarioSQLiteOpenHelper.TABLE_LOCALES_FAVORITOS, null, null,
				null, null, null, null);

		while (cursor.moveToNext()) {

			LocalFavorito local = new LocalFavorito(
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_ID_LOCAL_FAVORITO)),
					cursor.getInt(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_ID_LOCAL)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_NOMBRE)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_LOCALIDAD)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_PROVINCIA)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_DIRECCION)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_CODIGO_POSTAL)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_TELEFONO)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_EMAIL)),
					cursor.getString(cursor
							.getColumnIndex(UsuarioSQLiteOpenHelper.COLUMN_LF_TIPO_COMIDA)));

			locales.add(local);
		}

		cursor.close();
		return locales;
	}

	public void guardarLocalFavorito(LocalFavorito lf) {

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		ContentValues content = new ContentValues();

		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_ID_LOCAL_FAVORITO,
				lf.getIdLocalFavorito());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_ID_LOCAL, lf.getIdLocal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_NOMBRE, lf.getNombre());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_LOCALIDAD,
				lf.getLocalidad());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_PROVINCIA,
				lf.getProvincia());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_DIRECCION,
				lf.getDireccion());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_CODIGO_POSTAL,
				lf.getCodigoPostal());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_TELEFONO,
				lf.getTelefono());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_EMAIL, lf.getEmail());
		content.put(UsuarioSQLiteOpenHelper.COLUMN_LF_TIPO_COMIDA,
				lf.getTipoComida());

		database.insert(UsuarioSQLiteOpenHelper.TABLE_LOCALES_FAVORITOS, null,
				content);

	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static LocalFavorito favoritoJson2LocalFavorito(
			JSONObject localFavoritoJson) throws JSONException {

		LocalFavorito lf = new LocalFavorito(
				localFavoritoJson.getInt(JSON_LF_ID_LOCAL_FAVORITO),
				localFavoritoJson.getInt(JSON_LF_ID_LOCAL),
				localFavoritoJson.getString(JSON_LF_NOMBRE_LOCAL),
				localFavoritoJson.getString(JSON_LF_LOCALIDAD),
				localFavoritoJson.getString(JSON_LF_PROVINCIA),
				localFavoritoJson.getString(JSON_LF_DIRECCION),
				localFavoritoJson.getString(JSON_LF_CODIGO_POSTAL),
				localFavoritoJson.getString(JSON_LF_TELEFONO),
				localFavoritoJson.getString(JSON_LF_EMAIL),
				localFavoritoJson.getString(JSON_LF_TIPO_COMIDA));

		return lf;
	}
}
