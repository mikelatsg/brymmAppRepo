package com.brymm.brymmapp.local.bbdd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.IngredientePerComanda;
import com.brymm.brymmapp.local.pojo.MenuComanda;
import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.local.pojo.Plato;
import com.brymm.brymmapp.local.pojo.PlatoComanda;
import com.brymm.brymmapp.local.pojo.TipoArticulo;
import com.brymm.brymmapp.local.pojo.TipoComanda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestionComanda {

	public static final String JSON_COMANDAS = "comandas";
	public static final String JSON_COMANDA = "comanda";
	public static final String JSON_PLATO_ESTADO = "platoEstado";
	public static final String JSON_PLATOS_ESTADO = "platosEstado";
	public static final String JSON_PLATO_COMANDA = "platoComanda";
	public static final String JSON_PLATOS_COMANDA = "platosComanda";
	public static final String JSON_ESTADO = "estado";
	public static final String JSON_PRECIO = "precio";
	public static final String JSON_CANTIDAD = "cantidad";
	public static final String JSON_ID_COMANDA_MENU = "idComandaMenu";
	public static final String JSON_ID_DETALLE_COMANDA = "idDetalleComanda";
	public static final String JSON_MENU_COMANDA = "menuComanda";

	private SQLiteDatabase database;
	private LocalSQLite openHelper;
	private Context context;

	public GestionComanda(Context context) {
		openHelper = new LocalSQLite(context);
		database = openHelper.getWritableDatabase();
		this.context = context;
	}

	public void borrarPlatoComanda(int idComandaMenu) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_COMANDA_MENU,
				LocalSQLite.COLUMN_CM_ID_COMANDA_MENU + " = ? ",
				new String[] { Integer.toString(idComandaMenu) });
	}

	public void borrarArticuloPerComanda(int idComandaArticuloPer) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}
		database.delete(LocalSQLite.TABLE_COMANDA_ARTICULO_PER,
				LocalSQLite.COLUMN_CAM_ID_COMANDA_ARTICULO_PER + " = ? ",
				new String[] { Integer.toString(idComandaArticuloPer) });
	}

	public void borrarDetalleComanda(int idDetalleComanda) {
		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		database.delete(LocalSQLite.TABLE_COMANDA_ARTICULO_PER,
				LocalSQLite.COLUMN_CAM_ID_DETALLE_COMANDA + " = ? ",
				new String[] { Integer.toString(idDetalleComanda) });
		database.delete(LocalSQLite.TABLE_COMANDA_MENU,
				LocalSQLite.COLUMN_CM_ID_DETALLE_COMANDA + " = ? ",
				new String[] { Integer.toString(idDetalleComanda) });
		database.delete(LocalSQLite.TABLE_DETALLE_COMANDA,
				LocalSQLite.COLUMN_DC_ID_DETALLE_COMANDA + " = ? ",
				new String[] { Integer.toString(idDetalleComanda) });
	}

	public int guardarDetalleComanda(DetalleComanda detalleComanda,
			int idComanda) {
		int resultado = 1;

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_DETALLE_COMANDA, null,
				LocalSQLite.COLUMN_DC_ID_DETALLE_COMANDA + " = ?",
				new String[] { Integer.toString(detalleComanda
						.getIdDetalleComanda()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_DC_CANTIDAD,
				detalleComanda.getCantidad());
		content.put(LocalSQLite.COLUMN_DC_ESTADO, detalleComanda.getEstado());
		content.put(LocalSQLite.COLUMN_DC_ID_TIPO_COMANDA, detalleComanda
				.getTipoComanda().getIdTipoComanda());
		content.put(LocalSQLite.COLUMN_DC_PRECIO, detalleComanda.getPrecio());
		content.put(LocalSQLite.COLUMN_DC_ID_COMANDA, idComanda);

		// Asigno el id articulo dependiendo el tipo de comanda
		if (detalleComanda.getTipoComanda().getIdTipoComanda() == 1) {
			content.put(LocalSQLite.COLUMN_DC_ID_ARTICULO, detalleComanda
					.getArticulo().getIdArticulo());
		} else if (detalleComanda.getTipoComanda().getIdTipoComanda() == 3
				|| detalleComanda.getTipoComanda().getIdTipoComanda() == 4) {
			content.put(LocalSQLite.COLUMN_DC_ID_ARTICULO, detalleComanda
					.getMenuComanda().getMenu().getIdMenu());
		} else {
			content.put(LocalSQLite.COLUMN_DC_ID_ARTICULO, 0);
		}

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_DC_ID_DETALLE_COMANDA,
					detalleComanda.getIdDetalleComanda());

			if (database.insert(LocalSQLite.TABLE_DETALLE_COMANDA, null,
					content) < 0) {
				resultado = -1;
			}
		} else {

			if (database.update(LocalSQLite.TABLE_DETALLE_COMANDA, content,
					LocalSQLite.COLUMN_DC_ID_DETALLE_COMANDA + " = ?",
					new String[] { Integer.toString(detalleComanda
							.getIdDetalleComanda()) }) < 0) {
				resultado = -1;
			}

		}

		if (resultado > 0) {
			// Inserto el menu
			if (detalleComanda.getTipoComanda().getIdTipoComanda() == 3
					|| detalleComanda.getTipoComanda().getIdTipoComanda() == 4) {
				Iterator<PlatoComanda> iterator = detalleComanda
						.getMenuComanda().getPlatos().iterator();
				while (iterator.hasNext()) {
					PlatoComanda platoComanda = (PlatoComanda) iterator.next();
					guardarPlatoComanda(platoComanda,
							detalleComanda.getIdDetalleComanda());
				}

			}

			// Inserto el ariculo per
			if (detalleComanda.getTipoComanda().getIdTipoComanda() == 2) {
				Iterator<Ingrediente> iterator = detalleComanda.getArticulo()
						.getIngredientes().iterator();
				while (iterator.hasNext()) {
					IngredientePerComanda ingrediente = (IngredientePerComanda) iterator
							.next();
					guardarArticuloPerComanda(ingrediente,
							detalleComanda.getIdDetalleComanda());
				}

			}
		}

		cursor.close();

		return resultado;
	}

	public int guardarPlatoComanda(PlatoComanda platoComanda,
			int idDetalleComanda) {

		int resultado = 1;

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database
				.query(LocalSQLite.TABLE_COMANDA_MENU, null,
						LocalSQLite.COLUMN_CM_ID_COMANDA_MENU + " = ?",
						new String[] { Integer.toString(platoComanda
								.getidComandaMenu()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_CM_ID_DETALLE_COMANDA, idDetalleComanda);
		content.put(LocalSQLite.COLUMN_CM_ID_PLATO, platoComanda.getIdPlato());
		content.put(LocalSQLite.COLUMN_CM_CANTIDAD, platoComanda.getCantidad());
		content.put(LocalSQLite.COLUMN_CM_ESTADO, platoComanda.getEstado());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_CM_ID_COMANDA_MENU,
					platoComanda.getidComandaMenu());

			if (database.insert(LocalSQLite.TABLE_COMANDA_MENU, null, content) < 0) {
				resultado = -1;
			}

		} else {

			if (database.update(LocalSQLite.TABLE_COMANDA_MENU, content,
					LocalSQLite.COLUMN_CM_ID_COMANDA_MENU + " = ?",
					new String[] { Integer.toString(platoComanda
							.getidComandaMenu()) }) < 0) {
				resultado = -1;
			}

		}

		cursor.close();

		return resultado;

	}

	public int guardarArticuloPerComanda(IngredientePerComanda ingrediente,
			int idDetalleComanda) {

		int resultado = 1;

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_COMANDA_ARTICULO_PER,
				null, LocalSQLite.COLUMN_CAM_ID_COMANDA_ARTICULO_PER + " = ?",
				new String[] { Integer.toString(ingrediente
						.getIdComandaArticuloPer()) }, null, null, null);

		ContentValues content = new ContentValues();

		content.put(LocalSQLite.COLUMN_CAM_ID_DETALLE_COMANDA, idDetalleComanda);
		content.put(LocalSQLite.COLUMN_CAM_ID_INGREDIENTE,
				ingrediente.getIdIngrediente());
		content.put(LocalSQLite.COLUMN_CAM_PRECIO, ingrediente.getPrecio());

		if (!cursor.moveToFirst()) {
			content.put(LocalSQLite.COLUMN_CAM_ID_COMANDA_ARTICULO_PER,
					ingrediente.getIdComandaArticuloPer());

			if (database.insert(LocalSQLite.TABLE_COMANDA_ARTICULO_PER, null,
					content) < 0) {
				resultado = -1;
			}

		} else {

			if (database.update(LocalSQLite.TABLE_COMANDA_ARTICULO_PER,
					content, LocalSQLite.COLUMN_CAM_ID_COMANDA_ARTICULO_PER
							+ " = ?", new String[] { Integer
							.toString(ingrediente.getIdComandaArticuloPer()) }) < 0) {
				resultado = -1;
			}

		}

		cursor.close();

		return resultado;

	}

	public List<DetalleComanda> obtenerDetallesComanda(int idComanda) {
		List<DetalleComanda> detallesComanda = new ArrayList<DetalleComanda>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_DETALLE_COMANDA, null,
				LocalSQLite.COLUMN_DC_ID_COMANDA + " = ?",
				new String[] { Integer.toString(idComanda) }, null, null, null);

		while (cursor.moveToNext()) {
			GestionTipoComanda gestor = new GestionTipoComanda(context);
			TipoComanda tipoComanda = gestor
					.obtenerTipoComanda(cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DC_ID_TIPO_COMANDA)));
			gestor.cerrarDatabase();

			MenuComanda menuComanda = null;
			ArticuloCantidad articuloCantidad = null;
			// Si la comanda es de un articulo
			switch (tipoComanda.getIdTipoComanda()) {
			case 1:
				GestionArticulo gestorArticulo = new GestionArticulo(context);
				Articulo articulo = gestorArticulo
						.obtenerArticulo(cursor.getInt(cursor
								.getColumnIndex(LocalSQLite.COLUMN_DC_ID_ARTICULO)));
				gestorArticulo.cerrarDatabase();
				articuloCantidad = new ArticuloCantidad(
						articulo,
						cursor.getInt(cursor
								.getColumnIndex(LocalSQLite.COLUMN_DC_CANTIDAD)));
				break;
			// Articulo personalizado
			case 2:
				articuloCantidad = obtenerArticuloPerDetalleComanda(
						cursor.getInt(cursor
								.getColumnIndex(LocalSQLite.COLUMN_DC_ID_DETALLE_COMANDA)),
						cursor.getInt(cursor
								.getColumnIndex(LocalSQLite.COLUMN_DC_ID_ARTICULO)),
						cursor.getInt(cursor
								.getColumnIndex(LocalSQLite.COLUMN_DC_CANTIDAD)),
						cursor.getInt(cursor
								.getColumnIndex(LocalSQLite.COLUMN_DC_PRECIO)));
				break;
			case 3:
			case 4:
				GestionMenu gestionMenu = new GestionMenu(context);
				MenuLocal menu = gestionMenu.obtenerMenu(cursor.getInt(cursor
						.getColumnIndex(LocalSQLite.COLUMN_DC_ID_ARTICULO)));
				gestionMenu.cerrarDatabase();

				List<PlatoComanda> platosComanda = obtenerPlatosDetalleComanda(cursor
						.getInt(cursor
								.getColumnIndex(LocalSQLite.COLUMN_DC_ID_DETALLE_COMANDA)));

				menuComanda = new MenuComanda(menu, platosComanda);
				break;
			}

			DetalleComanda detalleComanda = new DetalleComanda(
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DC_ID_DETALLE_COMANDA)),
					tipoComanda, cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DC_CANTIDAD)),
					cursor.getFloat(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DC_PRECIO)),
					cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_DC_ESTADO)),
					articuloCantidad, menuComanda);

			detallesComanda.add(detalleComanda);
		}

		return detallesComanda;
	}

	public List<PlatoComanda> obtenerPlatosDetalleComanda(int idDetalleComanda) {
		List<PlatoComanda> platosComanda = new ArrayList<PlatoComanda>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_COMANDA_MENU, null,
				LocalSQLite.COLUMN_CM_ID_DETALLE_COMANDA + " = ?",
				new String[] { Integer.toString(idDetalleComanda) }, null,
				null, null);

		while (cursor.moveToNext()) {

			PlatoComanda platoComanda = obtenerPlatoComanda(cursor
					.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_CM_ID_COMANDA_MENU)));

			platosComanda.add(platoComanda);
		}

		cursor.close();
		return platosComanda;
	}

	public ArticuloCantidad obtenerArticuloPerDetalleComanda(
			int idDetalleComanda, int idTipoArticulo, int cantidad, float precio) {
		ArticuloCantidad articuloCantidad = null;

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_COMANDA_ARTICULO_PER,
				null, LocalSQLite.COLUMN_CAM_ID_DETALLE_COMANDA + " = ?",
				new String[] { Integer.toString(idDetalleComanda) }, null,
				null, null);

		while (cursor.moveToNext()) {

			List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();
			ingredientes = obtenerIngredientesDetalleComanda(idDetalleComanda);

			GestionTipoArticulo gestor = new GestionTipoArticulo(context);
			TipoArticulo tipoArticulo = gestor
					.obtenerTipoArticulo(idTipoArticulo);
			gestor.cerrarDatabase();

			Articulo articulo = new Articulo(0, tipoArticulo, "Articulo per",
					"articulo per", precio, false, ingredientes);

			articuloCantidad = new ArticuloCantidad(articulo, cantidad);

		}

		cursor.close();
		return articuloCantidad;
	}

	public List<Ingrediente> obtenerIngredientesDetalleComanda(
			int idDetalleComanda) {
		List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();

		if (!database.isOpen()) {
			database = openHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(LocalSQLite.TABLE_COMANDA_ARTICULO_PER,
				null, LocalSQLite.COLUMN_CAM_ID_DETALLE_COMANDA + " = ?",
				new String[] { Integer.toString(idDetalleComanda) }, null,
				null, null);

		while (cursor.moveToNext()) {

			GestionIngrediente gil = new GestionIngrediente(context);
			Ingrediente ingrediente = gil
					.obtenerIngrediente(cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_CAM_ID_INGREDIENTE)));
			gil.cerrarDatabase();

			ingredientes.add(ingrediente);
		}

		cursor.close();
		return ingredientes;
	}

	public PlatoComanda obtenerPlatoComanda(int idComandaMenu) {
		PlatoComanda platoComanda = null;
		Cursor cursor = database.query(LocalSQLite.TABLE_COMANDA_MENU, null,
				LocalSQLite.COLUMN_CM_ID_COMANDA_MENU + " = ?",
				new String[] { Integer.toString(idComandaMenu) }, null, null,
				null);

		while (cursor.moveToNext()) {
			Plato plato = null;

			GestionPlato gestionPlato = new GestionPlato(context);
			plato = gestionPlato.obtenerPlato(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_CM_ID_PLATO)));
			gestionPlato.cerrarDatabase();

			platoComanda = new PlatoComanda(cursor.getInt(cursor
					.getColumnIndex(LocalSQLite.COLUMN_CM_ID_COMANDA_MENU)),
					plato, cursor.getString(cursor
							.getColumnIndex(LocalSQLite.COLUMN_CM_ESTADO)),
					cursor.getInt(cursor
							.getColumnIndex(LocalSQLite.COLUMN_CM_CANTIDAD)));

		}

		cursor.close();
		return platoComanda;
	}

	public void cerrarDatabase() {
		if (database.isOpen()) {
			database.close();
		}
	}

	public static PlatoComanda platoComandaJson2PlatoComanda(
			JSONObject platoComandaJson) throws JSONException {

		Plato plato = GestionPlato.platoJson2Plato(platoComandaJson
				.getJSONObject(GestionPlato.JSON_ID_PLATO));

		PlatoComanda platoComanda = new PlatoComanda(
				platoComandaJson.getInt(JSON_ID_COMANDA_MENU), plato,
				platoComandaJson.getString(JSON_ESTADO),
				platoComandaJson.getInt(JSON_CANTIDAD));

		return platoComanda;
	}

	public static DetalleComanda detalleComandaJson2DetalleComanda(
			JSONObject detalleComandaJson) throws JSONException {

		TipoComanda tipoComanda = GestionTipoComanda
				.tipoComandaJson2TipoComanda(detalleComandaJson
						.getJSONObject(GestionTipoComanda.JSON_TIPO_COMANDA));

		ArticuloCantidad articulo = GestionArticulo
				.articuloCantidadJson2ArticuloCantidad(detalleComandaJson
						.getJSONObject(GestionArticulo.JSON_ARTICULO));

		MenuComanda menuComanda = GestionComanda
				.menuComandaJson2MenuComanda(detalleComandaJson
						.getJSONObject(JSON_MENU_COMANDA));

		DetalleComanda detalleComanda = new DetalleComanda(
				detalleComandaJson.getInt(JSON_ID_DETALLE_COMANDA),
				tipoComanda, detalleComandaJson.getInt(JSON_CANTIDAD),
				(float) detalleComandaJson.getDouble(JSON_PRECIO),
				detalleComandaJson.getString(JSON_ESTADO), articulo,
				menuComanda);

		return detalleComanda;
	}

	public static MenuComanda menuComandaJson2MenuComanda(
			JSONObject menuComandaJson) throws JSONException {

		MenuLocal menuLocal = GestionMenu.menuJson2Menu(menuComandaJson
				.getJSONObject(GestionMenu.JSON_MENU));

		List<PlatoComanda> platosComanda = new ArrayList<PlatoComanda>();

		for (int i = 0; i < menuComandaJson.getJSONArray(JSON_PLATOS_COMANDA)
				.length(); i++) {
			JSONObject platoComandaJson = menuComandaJson.getJSONArray(
					JSON_PLATO_COMANDA).getJSONObject(i);

			PlatoComanda platoComanda = platoComandaJson2PlatoComanda(platoComandaJson);

			platosComanda.add(platoComanda);

		}

		MenuComanda menuComanda = new MenuComanda(menuLocal, platosComanda);

		return menuComanda;
	}

}
