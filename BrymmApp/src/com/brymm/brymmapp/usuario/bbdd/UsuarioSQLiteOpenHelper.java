package com.brymm.brymmapp.usuario.bbdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsuarioSQLiteOpenHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "usuarios.db";

	// Direcciones
	public static final String TABLE_DIRECCIONES = "direcciones";

	public static final String COLUMN_ID_DIRECCION = "id_direccion";
	public static final String COLUMN_NOMBRE = "nombre";
	public static final String COLUMN_DIRECCION = "direccion";
	public static final String COLUMN_POBLACION = "poblacion";
	public static final String COLUMN_PROVINCIA = "provincia";

	// Pedidos
	public static final String TABLE_PEDIDOS = "pedidos";

	public static final String COLUMN_ID_PEDIDO = "id_pedido";
	public static final String COLUMN_ESTADO = "estado";
	public static final String COLUMN_PRECIO = "precio";
	public static final String COLUMN_FECHA_PEDIDO = "fecha_pedido";
	public static final String COLUMN_ID_DIRECCION_ENVIO = "id_direccion_envio";
	public static final String COLUMN_OBSERVACIONES = "observaciones";
	public static final String COLUMN_NOMBRE_LOCAL = "nombre_local";
	public static final String COLUMN_MOTIVO_RECHAZO = "motivo_rechazo";
	public static final String COLUMN_FECHA_ENTREGA = "fecha_entrega";

	// Articulos pedido
	public static final String TABLE_ARTICULOS_PEDIDO = "articulos_pedido";
	public static final String COLUMN_AP_ID_PEDIDO = "id_pedido";
	public static final String COLUMN_AP_ID_ARTICULO_PEDIDO = "id_articulo_pedido";
	public static final String COLUMN_AP_ARTICULO = "articulo";
	public static final String COLUMN_AP_PRECIO_ARTICULO = "precio";
	public static final String COLUMN_AP_CANTIDAD = "cantidad";
	public static final String COLUMN_AP_TIPO_ARTICULO = "tipo_articulo";
	public static final String COLUMN_AP_PERSONALIZADO = "personalizado";

	// Detalle articulo pedido
	public static final String TABLE_DETALLE_ARTICULO_PEDIDO = "detalle_articulos_pedido";
	public static final String COLUMN_DAP_ID_INGREDIENTE = "id_ingrediente";
	public static final String COLUMN_DAP_INGREDIENTE = "ingrediente";
	public static final String COLUMN_DAP_ID_ARTICULO_PEDIDO = "id_articulo_pedido";

	// Locales favoritos
	public static final String TABLE_LOCALES_FAVORITOS = "locales_favoritos";
	public static final String COLUMN_LF_ID_LOCAL_FAVORITO = "id_locales_favoritos";
	public static final String COLUMN_LF_ID_LOCAL = "id_local";
	public static final String COLUMN_LF_NOMBRE = "nombre";
	public static final String COLUMN_LF_LOCALIDAD = "localidad";
	public static final String COLUMN_LF_PROVINCIA = "provincia";
	public static final String COLUMN_LF_DIRECCION = "direccion";
	public static final String COLUMN_LF_CODIGO_POSTAL = "codigo_postal";
	public static final String COLUMN_LF_TELEFONO = "telefono";
	public static final String COLUMN_LF_EMAIL = "email";
	public static final String COLUMN_LF_TIPO_COMIDA = "tipo_comida";

	// Reservas usuario
	public static final String TABLE_RESERVAS = "reservas";
	public static final String COLUMN_R_ID_RESERVA = "id_reserva";
	public static final String COLUMN_R_ID_LOCAL = "id_local";
	public static final String COLUMN_R_NUMERO_PERSONAS = "numero_personas";
	public static final String COLUMN_R_FECHA = "fecha";
	public static final String COLUMN_R_HORA = "hora";
	public static final String COLUMN_R_ESTADO = "estado";
	public static final String COLUMN_R_MOTIVO = "motivo";
	public static final String COLUMN_R_OBSERVACIONES = "observaciones";
	public static final String COLUMN_R_NOMBRE_LOCAL = "nombre_local";
	public static final String COLUMN_R_TIPO_MENU = "tipo_menu";

	// Tipos servicios
	public static final String TABLE_TIPOS_SERVICIOS = "tipos_servicios";
	public static final String COLUMN_TS_ID_TIPO_SERVICIO = "id_tipo_servicio";
	public static final String COLUMN_TS_SERVICIO = "servicio";

	// Tipos comida
	public static final String TABLE_TIPOS_COMIDA = "tipos_comida";
	public static final String COLUMN_TC_ID_TIPO_COMIDA = "id_tipo_comida";
	public static final String COLUMN_TC_TIPO_COMIDA = "tipo_comida";

	// Servicios local
	public static final String TABLE_SERVICIOS_LOCAL = "servicios_local";
	public static final String COLUMN_SL_ID_SERVICIO_LOCAL = "id_servicio_local";
	public static final String COLUMN_SL_ID_TIPO_SERVICIO = "id_tipo_servicio";
	public static final String COLUMN_SL_ID_LOCAL = "id_local";
	public static final String COLUMN_SL_IMPORTE_MINIMO = "importe_minimo";
	public static final String COLUMN_SL_PRECIO = "precio";

	// Tipos articulo local
	public static final String TABLE_TIPOS_ARTICULO_LOCAL = "tipos_articulo_local";
	public static final String COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL = "id_tipo_articulo_local";
	public static final String COLUMN_TAL_ID_TIPO_ARTICULO = "id_tipo_articulo";
	public static final String COLUMN_TAL_TIPO_ARTICULO = "tipo_articulo";
	public static final String COLUMN_TAL_DESCRIPCION = "descripcion";
	public static final String COLUMN_TAL_PERSONALIZAR = "personalizar";
	public static final String COLUMN_TAL_PRECIO = "precio";

	// Horario pedidos
	public static final String TABLE_HORARIOS_PEDIDO = "horarios_pedido";
	public static final String COLUMN_HP_ID_HORARIO_PEDIDO = "id_horario_pedido";
	public static final String COLUMN_HP_ID_LOCAL = "id_local";
	public static final String COLUMN_HP_ID_DIA = "id_dia";
	public static final String COLUMN_HP_HORA_INICIO = "hora_inicio";
	public static final String COLUMN_HP_HORA_FIN = "hora_fin";
	public static final String COLUMN_HP_DIA = "dia";

	// Ingredientes local
	public static final String TABLE_INGREDIENTES_LOCAL = "ingredientes_local";
	public static final String COLUMN_IL_ID_INGREDIENTE = "id_ingrediente";
	public static final String COLUMN_IL_ID_LOCAL = "id_local";
	public static final String COLUMN_IL_INGREDIENTE = "ingrediente";
	public static final String COLUMN_IL_DESCRIPCION = "descripcion";
	public static final String COLUMN_IL_PRECIO = "precio";

	// Articulos local
	public static final String TABLE_ARTICULOS_LOCAL = "articulos_local";
	public static final String COLUMN_AL_ID_ARTICULO = "id_articulo";
	public static final String COLUMN_AL_ID_LOCAL = "id_local";
	public static final String COLUMN_AL_ID_TIPO_ARTICULO = "id_tipo_articulo";
	public static final String COLUMN_AL_ARTICULO = "articulo";
	public static final String COLUMN_AL_DESCRIPCION = "descripcion";
	public static final String COLUMN_AL_PRECIO = "precio";
	public static final String COLUMN_AL_TIPO_ARTICULO = "tipo_articulo";

	// Detalle_articulo
	public static final String TABLE_DETALLE_ARTICULO = "detalle_articulo";
	public static final String COLUMN_DA_ID_DETALLE_ARTICULO = "id_detalle_articulo";
	public static final String COLUMN_DA_INGREDIENTE = "ingrediente";
	public static final String COLUMN_DA_ID_ARTICULO_LOCAL = "id_articulo_local";

	private static final String CREATE_TABLE_DETALLE_ARTICULO = "CREATE TABLE "
			+ TABLE_DETALLE_ARTICULO + "(" + COLUMN_DA_ID_DETALLE_ARTICULO
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_DA_INGREDIENTE
			+ " TEXT NOT NULL, " + COLUMN_DA_ID_ARTICULO_LOCAL
			+ " INTEGER NOT NULL)";

	private static final String CREATE_TABLE_ARTICULOS_LOCAL = "CREATE TABLE "
			+ TABLE_ARTICULOS_LOCAL + "(" + COLUMN_AL_ID_ARTICULO
			+ " INTEGER PRIMARY KEY," + COLUMN_AL_ID_LOCAL
			+ " INTEGER NOT NULL, " + COLUMN_AL_ID_TIPO_ARTICULO
			+ " INTEGER NOT NULL, " + COLUMN_AL_ARTICULO + " TEXT NOT NULL, "
			+ COLUMN_AL_DESCRIPCION + " TEXT NOT NULL, "
			+ COLUMN_AL_TIPO_ARTICULO + " TEXT NOT NULL, " + COLUMN_AL_PRECIO
			+ " REAL NOT NULL)";

	private static final String CREATE_TABLE_INGREDIENTES_LOCAL = "CREATE TABLE "
			+ TABLE_INGREDIENTES_LOCAL
			+ "("
			+ COLUMN_IL_ID_INGREDIENTE
			+ " INTEGER PRIMARY KEY,"
			+ COLUMN_IL_ID_LOCAL
			+ " INTEGER NOT NULL, "
			+ COLUMN_IL_INGREDIENTE
			+ " TEXT NOT NULL, "
			+ COLUMN_IL_DESCRIPCION
			+ " TEXT NOT NULL, "
			+ COLUMN_IL_PRECIO + " REAL NOT NULL)";

	private static final String CREATE_TABLE_HORARIOS_PEDIDO = "CREATE TABLE "
			+ TABLE_HORARIOS_PEDIDO + "(" + COLUMN_HP_ID_HORARIO_PEDIDO
			+ " INTEGER PRIMARY KEY," + COLUMN_HP_ID_LOCAL
			+ " INTEGER NOT NULL, " + COLUMN_HP_ID_DIA + " INTEGER NOT NULL, "
			+ COLUMN_HP_HORA_INICIO + " TEXT NOT NULL, " + COLUMN_HP_HORA_FIN
			+ " TEXT NOT NULL," + COLUMN_HP_DIA + " TEXT NOT NULL)";

	private static final String CREATE_TABLE_TIPOS_ARTICULOS_LOCAL = "CREATE TABLE "
			+ TABLE_TIPOS_ARTICULO_LOCAL
			+ "("
			+ COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL
			+ " INTEGER PRIMARY KEY,"
			+ COLUMN_TAL_ID_TIPO_ARTICULO
			+ " INTEGER NOT NULL, "
			+ COLUMN_TAL_TIPO_ARTICULO
			+ " TEXT NOT NULL, "
			+ COLUMN_TAL_DESCRIPCION
			+ " TEXT NOT NULL, "
			+ COLUMN_TAL_PERSONALIZAR
			+ " INTEGER NOT NULL,"
			+ COLUMN_TAL_PRECIO + " REAL NOT NULL)";

	private static final String CREATE_TABLE_SERVICIOS_LOCAL = "CREATE TABLE "
			+ TABLE_SERVICIOS_LOCAL + "(" + COLUMN_SL_ID_SERVICIO_LOCAL
			+ " INTEGER PRIMARY KEY," + COLUMN_SL_ID_LOCAL
			+ " INTEGER NOT NULL, " + COLUMN_SL_ID_TIPO_SERVICIO
			+ " INTEGER NOT NULL, " + COLUMN_SL_IMPORTE_MINIMO
			+ " REAL NOT NULL," + COLUMN_SL_PRECIO + " REAL NOT NULL)";

	private static final String CREATE_TABLE_TIPOS_COMIDA = "CREATE TABLE "
			+ TABLE_TIPOS_COMIDA + "(" + COLUMN_TC_ID_TIPO_COMIDA
			+ " INTEGER PRIMARY KEY," + COLUMN_TC_TIPO_COMIDA
			+ " TEXT NOT NULL)";

	private static final String CREATE_TABLE_TIPOS_SERVICIOS = "CREATE TABLE "
			+ TABLE_TIPOS_SERVICIOS + "(" + COLUMN_TS_ID_TIPO_SERVICIO
			+ " INTEGER PRIMARY KEY," + COLUMN_TS_SERVICIO + " TEXT NOT NULL)";

	private static final String CREATE_TABLE_RESERVAS = "CREATE TABLE "
			+ TABLE_RESERVAS + "(" + COLUMN_R_ID_RESERVA
			+ " INTEGER PRIMARY KEY," + COLUMN_R_ID_LOCAL
			+ " INTEGER NOT NULL, " + COLUMN_R_NUMERO_PERSONAS
			+ " INGETER NOT NULL, " + COLUMN_R_FECHA + " TEXT NOT NULL, "
			+ COLUMN_R_HORA + " TEXT NOT NULL," + COLUMN_R_ESTADO
			+ " TEXT NOT NULL," + COLUMN_R_MOTIVO + " TEXT NOT NULL,"
			+ COLUMN_R_OBSERVACIONES + " TEXT NOT NULL,"
			+ COLUMN_R_NOMBRE_LOCAL + " TEXT NOT NULL," + COLUMN_R_TIPO_MENU
			+ " TEXT NOT NULL" + ")";

	private static final String CREATE_TABLE_LOCALES_FAVORITOS = "CREATE TABLE "
			+ TABLE_LOCALES_FAVORITOS
			+ "("
			+ COLUMN_LF_ID_LOCAL_FAVORITO
			+ " INTEGER PRIMARY KEY,"
			+ COLUMN_LF_ID_LOCAL
			+ " INTEGER NOT NULL, "
			+ COLUMN_LF_NOMBRE
			+ " TEXT NOT NULL, "
			+ COLUMN_LF_LOCALIDAD
			+ " TEXT NOT NULL, "
			+ COLUMN_LF_PROVINCIA
			+ " TEXT NOT NULL,"
			+ COLUMN_LF_DIRECCION
			+ " TEXT NOT NULL,"
			+ COLUMN_LF_CODIGO_POSTAL
			+ " TEXT NOT NULL,"
			+ COLUMN_LF_TELEFONO
			+ " TEXT NOT NULL,"
			+ COLUMN_LF_EMAIL
			+ " TEXT NOT NULL,"
			+ COLUMN_LF_TIPO_COMIDA + " TEXT NOT NULL" + ")";

	private static final String CREATE_TABLE_DIRECCIONES = "CREATE TABLE "
			+ TABLE_DIRECCIONES + "(" + COLUMN_ID_DIRECCION
			+ " INTEGER PRIMARY KEY," + COLUMN_NOMBRE + " TEXT NOT NULL, "
			+ COLUMN_DIRECCION + " TEXT NOT NULL, " + COLUMN_POBLACION
			+ " INTEGER NOT NULL, " + COLUMN_PROVINCIA + " INTEGER NOT NULL)";

	private static final String CREATE_TABLE_PEDIDOS = "CREATE TABLE "
			+ TABLE_PEDIDOS + "(" + COLUMN_ID_PEDIDO + " INTEGER PRIMARY KEY,"
			+ COLUMN_ESTADO + " TEXT NOT NULL, " + COLUMN_PRECIO
			+ " INTEGER NOT NULL, " + COLUMN_FECHA_PEDIDO + " TEXT NOT NULL, "
			+ COLUMN_ID_DIRECCION_ENVIO + " INTEGER NOT NULL, "
			+ COLUMN_OBSERVACIONES + " TEXT NOT NULL, " + COLUMN_NOMBRE_LOCAL
			+ " TEXT NOT NULL, " + COLUMN_MOTIVO_RECHAZO + " TEXT NOT NULL, "
			+ COLUMN_FECHA_ENTREGA + " TEXT NOT NULL " + ")";

	private static final String CREATE_TABLE_ARTICULOS_PEDIDO = "CREATE TABLE "
			+ TABLE_ARTICULOS_PEDIDO + "(" + COLUMN_AP_ID_ARTICULO_PEDIDO
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_AP_ID_PEDIDO
			+ " INTEGER NOT NULL, " + COLUMN_AP_ARTICULO + " TEXT NOT NULL, "
			+ COLUMN_AP_PRECIO_ARTICULO + " REAL NOT NULL, "
			+ COLUMN_AP_CANTIDAD + " INTEGER NOT NULL, "
			+ COLUMN_AP_TIPO_ARTICULO + " TEXT NOT NULL, "
			+ COLUMN_AP_PERSONALIZADO + " INTEGER NOT NULL)";

	private static final String CREATE_TABLE_DETALLE_ARTICULO_PEDIDO = "CREATE TABLE "
			+ TABLE_DETALLE_ARTICULO_PEDIDO
			+ "("
			+ COLUMN_DAP_ID_INGREDIENTE
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COLUMN_DAP_ID_ARTICULO_PEDIDO
			+ " INTEGER NOT NULL,"
			+ COLUMN_DAP_INGREDIENTE + " TEXT NOT NULL)";

	private static final String DROP_TABLE_DIRECCIONES = "DROP TABLE IF EXISTS "
			+ TABLE_DIRECCIONES;

	public static final String TABLE_ACTUALIZACION = "actualizacion";
	public static final String COLUMN_ACT_FECHA = "fecha";

	private static final String CREATE_TABLE_ACTUALIZACIONES = "CREATE TABLE "
			+ TABLE_ACTUALIZACION + "(" + COLUMN_ACT_FECHA + " TEXT NOT NULL )";

	public UsuarioSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_DIRECCIONES);
		database.execSQL(CREATE_TABLE_PEDIDOS);
		database.execSQL(CREATE_TABLE_ARTICULOS_PEDIDO);
		database.execSQL(CREATE_TABLE_DETALLE_ARTICULO_PEDIDO);
		database.execSQL(CREATE_TABLE_LOCALES_FAVORITOS);
		database.execSQL(CREATE_TABLE_RESERVAS);
		database.execSQL(CREATE_TABLE_TIPOS_SERVICIOS);
		database.execSQL(CREATE_TABLE_TIPOS_COMIDA);
		database.execSQL(CREATE_TABLE_SERVICIOS_LOCAL);
		database.execSQL(CREATE_TABLE_TIPOS_ARTICULOS_LOCAL);
		database.execSQL(CREATE_TABLE_HORARIOS_PEDIDO);
		database.execSQL(CREATE_TABLE_INGREDIENTES_LOCAL);
		database.execSQL(CREATE_TABLE_DETALLE_ARTICULO);
		database.execSQL(CREATE_TABLE_ARTICULOS_LOCAL);
		database.execSQL(CREATE_TABLE_ACTUALIZACIONES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL(DROP_TABLE_DIRECCIONES);
		onCreate(database);
	}

}
