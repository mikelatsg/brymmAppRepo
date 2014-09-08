package com.brymm.brymmapp.local.bbdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalSQLite extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "local.db";

	// Direcciones
	public static final String TABLE_DIRECCIONES = "direcciones";

	public static final String COLUMN_DR_ID_DIRECCION = "id_direccion";
	public static final String COLUMN_DR_NOMBRE = "nombre";
	public static final String COLUMN_DR_DIRECCION = "direccion";
	public static final String COLUMN_DR_POBLACION = "poblacion";
	public static final String COLUMN_DR_PROVINCIA = "provincia";

	private static final String CREATE_TABLE_DIRECCIONES = "CREATE TABLE "
			+ TABLE_DIRECCIONES + "(" + COLUMN_DR_ID_DIRECCION
			+ " INTEGER PRIMARY KEY," + COLUMN_DR_NOMBRE + " TEXT NOT NULL, "
			+ COLUMN_DR_DIRECCION + " TEXT NOT NULL, " + COLUMN_DR_POBLACION
			+ " INTEGER NOT NULL, " + COLUMN_DR_PROVINCIA
			+ " INTEGER NOT NULL)";

	// Articulos
	public static final String TABLE_ARTICULOS = "articulos";
	public static final String COLUMN_AR_ID_ARTICULO = "id_articulo";
	public static final String COLUMN_AR_NOMBRE = "nombre";
	public static final String COLUMN_AR_PRECIO = "precio";
	public static final String COLUMN_AR_DESCRIPCION = "descripcion";
	public static final String COLUMN_AR_ID_TIPO_ARTICULO = "id_tipo_articulo";
	public static final String COLUMN_AR_VALIDO_PEDIDOS = "valido_pedidos";

	private static final String CREATE_TABLE_ARTICULOS = "CREATE TABLE "
			+ TABLE_ARTICULOS + "(" + COLUMN_AR_ID_ARTICULO
			+ " INTEGER PRIMARY KEY," + COLUMN_AR_NOMBRE + " TEXT NOT NULL, "
			+ COLUMN_AR_PRECIO + " INTEGER NOT NULL, " + COLUMN_AR_DESCRIPCION
			+ " TEXT NOT NULL, " + COLUMN_AR_ID_TIPO_ARTICULO
			+ " INTEGER NOT NULL," + COLUMN_AR_VALIDO_PEDIDOS
			+ " INTEGER NOT NULL)";

	// Ingredientes
	public static final String TABLE_INGREDIENTES = "ingredientes";
	public static final String COLUMN_IN_ID_INGREDIENTE = "id_ingrediente";
	public static final String COLUMN_IN_NOMBRE = "nombre";
	public static final String COLUMN_IN_DESCRIPCION = "descripcion";
	public static final String COLUMN_IN_PRECIO = "precio";

	private static final String CREATE_TABLE_INGREDIENTES = "CREATE TABLE "
			+ TABLE_INGREDIENTES + "(" + COLUMN_IN_ID_INGREDIENTE
			+ " INTEGER PRIMARY KEY," + COLUMN_IN_NOMBRE + " TEXT NOT NULL, "
			+ COLUMN_AR_PRECIO + " REAL NOT NULL, " + COLUMN_IN_DESCRIPCION
			+ " TEXT NOT NULL)";

	// Ingredientes articulos
	public static final String TABLE_INGREDIENTES_ARTICULOS = "ingredientes_articulos";
	public static final String COLUMN_IA_ID_INGREDIENTE = "id_ingrediente";
	public static final String COLUMN_IA_ID_ARTICULO = "id_articulo";
	public static final String COLUMN_IA_ID_INGREDIENTE_ARTICULO = "id_ingrediente_articulo";

	private static final String CREATE_TABLE_INGREDIENTES_ARTICULOS = "CREATE TABLE "
			+ TABLE_INGREDIENTES_ARTICULOS
			+ "("
			+ COLUMN_IA_ID_INGREDIENTE_ARTICULO
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COLUMN_IA_ID_INGREDIENTE
			+ " INTEGER NOT NULL, "
			+ COLUMN_IA_ID_ARTICULO
			+ " INTEGER NOT NULL)";

	// tipos articulo local
	public static final String TABLE_TIPOS_ARTICULO_LOCAL = "tipos_articulo_local";
	public static final String COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL = "id_tipo_articulo_local";
	public static final String COLUMN_TAL_ID_TIPO_ARTICULO = "id_tipo_articulo";
	public static final String COLUMN_TAL_PRECIO = "precio";
	public static final String COLUMN_TAL_PERSONALIZAR = "personalizar";

	private static final String CREATE_TABLE_TIPOS_ARTICULO_LOCAL = "CREATE TABLE "
			+ TABLE_TIPOS_ARTICULO_LOCAL
			+ "("
			+ COLUMN_TAL_ID_TIPO_ARTICULO_LOCAL
			+ " INTEGER PRIMARY KEY,"
			+ COLUMN_TAL_ID_TIPO_ARTICULO
			+ " INTEGER NOT NULL, "
			+ COLUMN_TAL_PERSONALIZAR
			+ " INTEGER NOT NULL, "
			+ COLUMN_TAL_PRECIO + " REAL NOT NULL)";

	// tipos articulo
	public static final String TABLE_TIPOS_ARTICULO = "tipos_articulo";
	public static final String COLUMN_TA_ID_TIPO_ARTICULO = "id_tipo_articulo";
	public static final String COLUMN_TA_DESCRIPCION = "descripcion";
	public static final String COLUMN_TA_TIPO_ARTICULO = "tipo_articulo";

	private static final String CREATE_TABLE_TIPOS_ARTICULO = "CREATE TABLE "
			+ TABLE_TIPOS_ARTICULO + "(" + COLUMN_TA_ID_TIPO_ARTICULO
			+ " INTEGER PRIMARY KEY," + COLUMN_TA_TIPO_ARTICULO
			+ " TEXT NOT NULL, " + COLUMN_TA_DESCRIPCION + " TEXT NOT NULL)";

	// Usuarios
	public static final String TABLE_USUARIOS = "usuarios";
	public static final String COLUMN_US_ID_USUARIO = "id_usuario";
	public static final String COLUMN_US_NOMBRE = "nombre";
	public static final String COLUMN_US_APELLIDO = "apellido";
	public static final String COLUMN_US_NICK = "nick";
	public static final String COLUMN_US_EMAIL = "email";
	public static final String COLUMN_US_LOCALIDAD = "localidad";
	public static final String COLUMN_US_PROVINCIA = "provincia";
	public static final String COLUMN_US_COD_POSTAL = "cod_postal";

	private static final String CREATE_TABLE_USUARIOS = "CREATE TABLE "
			+ TABLE_USUARIOS + "(" + COLUMN_US_ID_USUARIO
			+ " INTEGER PRIMARY KEY," + COLUMN_US_NOMBRE + " TEXT NOT NULL, "
			+ COLUMN_US_APELLIDO + " TEXT NOT NULL, " + COLUMN_US_NICK
			+ " TEXT NOT NULL, " + COLUMN_US_EMAIL + " TEXT NOT NULL, "
			+ COLUMN_US_LOCALIDAD + " TEXT NOT NULL, " + COLUMN_US_PROVINCIA
			+ " TEXT NOT NULL, " + COLUMN_US_COD_POSTAL + " INTEGER NOT NULL)";

	// Pedidos
	public static final String TABLE_PEDIDOS = "pedidos";
	public static final String COLUMN_PD_ID_PEDIDO = "id_pedido";
	public static final String COLUMN_PD_ID_DIRECCION = "id_direccion";
	public static final String COLUMN_PD_ESTADO = "estado";
	public static final String COLUMN_PD_FECHA = "fecha";
	public static final String COLUMN_PD_FECHA_ENTREGA = "fecha_entrega";
	public static final String COLUMN_PD_MOTIVO_RECHAZO = "motivo_rechazo";
	public static final String COLUMN_PD_OBSERVACIONES = "observaciones";
	public static final String COLUMN_PD_PRECIO = "precio";
	public static final String COLUMN_PD_ID_USUARIO = "id_usuario";

	private static final String CREATE_TABLE_PEDIDOS = "CREATE TABLE "
			+ TABLE_PEDIDOS + "(" + COLUMN_PD_ID_PEDIDO
			+ " INTEGER PRIMARY KEY," + COLUMN_PD_ID_DIRECCION
			+ " INTEGER NOT NULL, " + COLUMN_PD_ESTADO + " TEXT NOT NULL, "
			+ COLUMN_PD_FECHA + " TEXT NOT NULL, " + COLUMN_PD_FECHA_ENTREGA
			+ " TEXT NOT NULL, " + COLUMN_PD_MOTIVO_RECHAZO
			+ " TEXT NOT NULL, " + COLUMN_PD_OBSERVACIONES + " TEXT NOT NULL, "
			+ COLUMN_PD_PRECIO + " REAL NOT NULL, " + COLUMN_PD_ID_USUARIO
			+ " INTEGER NOT NULL)";

	// articulos pedidos
	public static final String TABLE_ARTICULOS_PEDIDOS = "articulos_pedidos";
	public static final String COLUMN_AP_ID_PEDIDO = "id_pedido";
	public static final String COLUMN_AP_ID_ARTICULO = "id_articulo";
	public static final String COLUMN_AP_ID_ARTICULO_PEDIDO = "id_articulo_pedido";
	public static final String COLUMN_AP_CANTIDAD = "cantidad";

	private static final String CREATE_TABLE_ARTICULOS_PEDIDOS = "CREATE TABLE "
			+ TABLE_ARTICULOS_PEDIDOS
			+ "("
			+ COLUMN_AP_ID_ARTICULO_PEDIDO
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COLUMN_AP_ID_PEDIDO
			+ " INTEGER NOT NULL, "
			+ COLUMN_AP_ID_ARTICULO
			+ " INTEGER NOT NULL, " + COLUMN_AP_CANTIDAD + " INTEGER NOT NULL)";

	// articulos personalizados
	public static final String TABLE_ARTICULOS_PERSONALIZADOS = "articulos_personalizados";
	public static final String COLUMN_APR_ID_ARTICULO_PEDIDO = "id_articulo_pedido";
	public static final String COLUMN_APR_NOMBRE = "nombre";
	public static final String COLUMN_APR_PRECIO = "precio";
	public static final String COLUMN_APR_ID_TIPO_ARTICULO = "id_tipo_articulo";

	private static final String CREATE_TABLE_ARTICULOS_PERSONALIZADOS = "CREATE TABLE "
			+ TABLE_ARTICULOS_PERSONALIZADOS
			+ "("
			+ COLUMN_APR_ID_ARTICULO_PEDIDO
			+ " INTEGER PRIMARY KEY,"
			+ COLUMN_APR_ID_TIPO_ARTICULO
			+ " INTEGER NOT NULL, "
			+ COLUMN_APR_PRECIO
			+ " REAL NOT NULL,"
			+ COLUMN_APR_NOMBRE
			+ " TEXT NOT NULL)";

	// detalle articulos personalizados
	public static final String TABLE_DETALLE_ARTICULOS_PERSONALIZADOS = "detalle_articulos_personalizados";
	public static final String COLUMN_DAP_ID_DET_ART_PER = "id_detalle_articulo_personalizado";
	public static final String COLUMN_DAP_ID_ARTICULO_PEDIDO = "id_articulo_pedido";
	public static final String COLUMN_DAP_ID_INGREDIENTE = "id_ingrediente";

	private static final String CREATE_TABLE_DETALLE_ARTICULOS_PERSONALIZADOS = "CREATE TABLE "
			+ TABLE_DETALLE_ARTICULOS_PERSONALIZADOS
			+ "("
			+ COLUMN_DAP_ID_DET_ART_PER
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COLUMN_DAP_ID_ARTICULO_PEDIDO
			+ " INTEGER NOT NULL, "
			+ COLUMN_DAP_ID_INGREDIENTE + " INTEGER NOT NULL)";

	// tipo servicio
	public static final String TABLE_TIPOS_SERVICIO = "tipos_servicios";
	public static final String COLUMN_TS_ID_TIPO_SERVICIO = "id_tipo_servicio";
	public static final String COLUMN_TS_SERVICIO = "servicio";
	public static final String COLUMN_TS_DESCRIPCION = "descripcion";
	public static final String COLUMN_TS_MOSTRAR_BUSCADOR = "mostrar_buscador";

	private static final String CREATE_TABLE_TIPOS_SERVICIO = "CREATE TABLE "
			+ TABLE_TIPOS_SERVICIO + "(" + COLUMN_TS_ID_TIPO_SERVICIO
			+ " INTEGER PRIMARY KEY ," + COLUMN_TS_SERVICIO
			+ " TEXT NOT NULL, " + COLUMN_TS_DESCRIPCION + " TEXT , "
			+ COLUMN_TS_MOSTRAR_BUSCADOR + " INTEGER NOT NULL)";

	// detalle articulos personalizados
	public static final String TABLE_SERVICIOS_LOCAL = "servicios_local";
	public static final String COLUMN_SL_ID_SERVICIO = "id_servicio";
	public static final String COLUMN_SL_ID_TIPO_SERVICIO = "id_tipo_servicio";
	public static final String COLUMN_SL_ACTIVO = "activo";
	public static final String COLUMN_SL_IMPORTE_MINIMO = "importe_minimo";
	public static final String COLUMN_SL_PRECIO = "precio";

	private static final String CREATE_TABLE_SERVICIOS_LOCAL = "CREATE TABLE "
			+ TABLE_SERVICIOS_LOCAL + "(" + COLUMN_SL_ID_SERVICIO
			+ " INTEGER PRIMARY KEY," + COLUMN_SL_ID_TIPO_SERVICIO
			+ " INTEGER NOT NULL, " + COLUMN_SL_IMPORTE_MINIMO
			+ " REAL NOT NULL, " + COLUMN_SL_PRECIO + " REAL NOT NULL, "
			+ COLUMN_SL_ACTIVO + " INTEGER NOT NULL)";

	// dias semana
	public static final String TABLE_DIAS_SEMANA = "dias_semana";
	public static final String COLUMN_DS_ID_DIA = "id_dia";
	public static final String COLUMN_DS_DIA = "dia";

	private static final String CREATE_TABLE_DIAS_SEMANA = "CREATE TABLE "
			+ TABLE_DIAS_SEMANA + "(" + COLUMN_DS_ID_DIA
			+ " INTEGER PRIMARY KEY," + COLUMN_DS_DIA + " TEXT NOT NULL)";

	// horarios local
	public static final String TABLE_HORARIOS_LOCAL = "horarios_local";
	public static final String COLUMN_HL_ID_HORARIO_LOCAL = "id_horario_local";
	public static final String COLUMN_HL_ID_DIA = "id_dia";
	public static final String COLUMN_HL_HORA_INICIO = "hora_inicio";
	public static final String COLUMN_HL_HORA_FIN = "hora_fin";

	private static final String CREATE_TABLE_HORARIOS_LOCAL = "CREATE TABLE "
			+ TABLE_HORARIOS_LOCAL + "(" + COLUMN_HL_ID_HORARIO_LOCAL
			+ " INTEGER PRIMARY KEY," + COLUMN_HL_ID_DIA
			+ " INTEGER NOT NULL, " + COLUMN_HL_HORA_INICIO
			+ " TEXT NOT NULL, " + COLUMN_HL_HORA_FIN + " TEXT NOT NULL)";

	// horarios pedido
	public static final String TABLE_HORARIOS_PEDIDO = "horarios_pedido";
	public static final String COLUMN_HP_ID_HORARIO_PEDIDO = "id_horario_pedido";
	public static final String COLUMN_HP_ID_DIA = "id_dia";
	public static final String COLUMN_HP_HORA_INICIO = "hora_inicio";
	public static final String COLUMN_HP_HORA_FIN = "hora_fin";

	private static final String CREATE_TABLE_HORARIOS_PEDIDO = "CREATE TABLE "
			+ TABLE_HORARIOS_PEDIDO + "(" + COLUMN_HP_ID_HORARIO_PEDIDO
			+ " INTEGER PRIMARY KEY," + COLUMN_HP_ID_DIA
			+ " INTEGER NOT NULL, " + COLUMN_HP_HORA_INICIO
			+ " TEXT NOT NULL, " + COLUMN_HP_HORA_FIN + " TEXT NOT NULL)";

	// dias cierre
	public static final String TABLE_DIAS_CIERRE = "dias_cierre";
	public static final String COLUMN_DC_ID_DIA_CIERRE = "id_dia_cierre";
	public static final String COLUMN_DC_FECHA = "fecha";

	private static final String CREATE_TABLE_DIAS_CIERRE = "CREATE TABLE "
			+ TABLE_DIAS_CIERRE + "(" + COLUMN_DC_ID_DIA_CIERRE
			+ " INTEGER PRIMARY KEY," + COLUMN_DC_FECHA + " TEXT NOT NULL)";

	// mesas
	public static final String TABLE_MESAS = "mesas";
	public static final String COLUMN_MS_ID_MESA = "id_mesa";
	public static final String COLUMN_MS_NOMBRE = "nombre";
	public static final String COLUMN_MS_CAPACIDAD = "capacidad";

	private static final String CREATE_TABLE_MESAS = "CREATE TABLE "
			+ TABLE_MESAS + "(" + COLUMN_MS_ID_MESA + " INTEGER PRIMARY KEY,"
			+ COLUMN_MS_NOMBRE + " TEXT NOT NULL," + COLUMN_MS_CAPACIDAD
			+ " INTEGER NOT NULL)";

	// tipo menu
	public static final String TABLE_TIPOS_MENU = "tiposMenu";
	public static final String COLUMN_TM_ID_TIPO_MENU = "id_tipo_menu";
	public static final String COLUMN_TM_DESCRIPCION = "descripcion";

	private static final String CREATE_TIPOS_MENU = "CREATE TABLE "
			+ TABLE_TIPOS_MENU + "(" + COLUMN_TM_ID_TIPO_MENU
			+ " INTEGER PRIMARY KEY," + COLUMN_TM_DESCRIPCION
			+ " TEXT NOT NULL)";

	// tipo menu
	public static final String TABLE_RESERVAS = "reservas";
	public static final String COLUMN_RS_ID_RESERVA = "id_reserva";
	public static final String COLUMN_RS_ID_USUARIO = "id_usuario";
	public static final String COLUMN_RS_NUMERO_PERSONAS = "numero_personas";
	public static final String COLUMN_RS_FECHA = "fecha";
	public static final String COLUMN_RS_ID_TIPO_MENU = "id_tipo_menu";
	public static final String COLUMN_RS_HORA_INICIO = "hora_inicio";
	public static final String COLUMN_RS_HORA_FIN = "hora_fin";
	public static final String COLUMN_RS_ESTADO = "estado";
	public static final String COLUMN_RS_MOTIVO = "motivo";
	public static final String COLUMN_RS_OBSERVACIONES = "observaciones";
	public static final String COLUMN_RS_NOMBRE_EMISOR = "nombreEmisor";

	private static final String CREATE_TABLE_RESERVAS = "CREATE TABLE "
			+ TABLE_RESERVAS + "(" + COLUMN_RS_ID_RESERVA
			+ " INTEGER PRIMARY KEY," + COLUMN_RS_ID_USUARIO
			+ " INTEGER NOT NULL," + COLUMN_RS_NUMERO_PERSONAS
			+ " INTEGER NOT NULL ," + COLUMN_RS_FECHA + " TEXT NOT NULL ,"
			+ COLUMN_RS_ID_TIPO_MENU + " INTEGER NOT NULL ,"
			+ COLUMN_RS_HORA_INICIO + " TEXT NOT NULL ," + COLUMN_RS_HORA_FIN
			+ " TEXT ," + COLUMN_RS_ESTADO + " TEXT NOT NULL ,"
			+ COLUMN_RS_MOTIVO + " TEXT ," + COLUMN_RS_OBSERVACIONES
			+ " TEXT ," + COLUMN_RS_NOMBRE_EMISOR + " TEXT )";

	// tipo menu
	public static final String TABLE_MESAS_RESERVA = "mesasReserva";
	public static final String COLUMN_MR_ID_MESA_RESERVA = "id_mesa_reserva";
	public static final String COLUMN_MR_ID_RESERVA = "id_reserva";
	public static final String COLUMN_MR_ID_MESA = "id_mesa";

	private static final String CREATE_TABLE_MESAS_RESERVA = "CREATE TABLE "
			+ TABLE_MESAS_RESERVA + "(" + COLUMN_MR_ID_MESA_RESERVA
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_MR_ID_RESERVA
			+ " INTEGER NOT NULL," + COLUMN_MR_ID_MESA + " INTEGER NOT NULL)";

	// dias cierre reserva
	public static final String TABLE_DIAS_CIERRE_RESERVA = "dias_cierre_reserva";
	public static final String COLUMN_DCR_ID_DIA_CIERRE_RESERVA = "id_dia_cierre_reserva";
	public static final String COLUMN_DCR_FECHA = "fecha";
	public static final String COLUMN_DCR_ID_TIPO_MENU = "id_tipo_menu";

	private static final String CREATE_TABLE_DIAS_CIERRE_RESERVA = "CREATE TABLE "
			+ TABLE_DIAS_CIERRE_RESERVA
			+ "("
			+ COLUMN_DCR_ID_DIA_CIERRE_RESERVA
			+ " INTEGER PRIMARY KEY,"
			+ COLUMN_DCR_FECHA
			+ " TEXT NOT NULL,"
			+ COLUMN_DCR_ID_TIPO_MENU
			+ " INTEGER NOT NULL)";

	// tipos plato
	public static final String TABLE_TIPOS_PLATO = "tipos_plato";
	public static final String COLUMN_TP_ID_TIPO_PLATO = "id_tipo_plato";
	public static final String COLUMN_TP_DESCRIPCION = "descripcion";

	private static final String CREATE_TABLE_TIPOS_PLATO = "CREATE TABLE "
			+ TABLE_TIPOS_PLATO + "(" + COLUMN_TP_ID_TIPO_PLATO
			+ " INTEGER PRIMARY KEY," + COLUMN_TP_DESCRIPCION
			+ " TEXT NOT NULL)";

	// platos
	public static final String TABLE_PLATOS = "platos";
	public static final String COLUMN_PL_ID_PLATO = "id_plato";
	public static final String COLUMN_PL_NOMBRE = "nombre";
	public static final String COLUMN_PL_PRECIO = "precio";
	public static final String COLUMN_PL_ID_TIPO_PLATO = "id_tipo_plato";

	private static final String CREATE_TABLE_PLATOS = "CREATE TABLE "
			+ TABLE_PLATOS + "(" + COLUMN_PL_ID_PLATO + " INTEGER PRIMARY KEY,"
			+ COLUMN_PL_ID_TIPO_PLATO + " INTEGER NOT NULL," + COLUMN_PL_NOMBRE
			+ " TEXT NOT NULL," + COLUMN_PL_PRECIO + " REAL NOT NULL)";

	// menus
	public static final String TABLE_MENUS = "menus";
	public static final String COLUMN_MN_ID_MENU = "id_menu";
	public static final String COLUMN_MN_NOMBRE = "nombre";
	public static final String COLUMN_MN_PRECIO = "precio";
	public static final String COLUMN_MN_ID_TIPO_MENU = "id_tipo_menu";
	public static final String COLUMN_MN_CARTA = "carta";

	private static final String CREATE_TABLE_MENUS = "CREATE TABLE "
			+ TABLE_MENUS + "(" + COLUMN_MN_ID_MENU + " INTEGER PRIMARY KEY,"
			+ COLUMN_MN_ID_TIPO_MENU + " INTEGER NOT NULL," + COLUMN_MN_NOMBRE
			+ " TEXT NOT NULL," + COLUMN_MN_PRECIO + " REAL NOT NULL, "
			+ COLUMN_MN_CARTA + " INTEGER NOT NULL)";

	// menus dia
	public static final String TABLE_MENUS_DIA = "menus_dia";
	public static final String COLUMN_MD_ID_MENU_DIA = "id_menu_dia";
	public static final String COLUMN_MD_FECHA = "fecha";
	public static final String COLUMN_MD_ID_MENU = "id_menu";

	private static final String CREATE_TABLE_MENUS_DIA = "CREATE TABLE "
			+ TABLE_MENUS_DIA + "(" + COLUMN_MD_ID_MENU_DIA
			+ " INTEGER PRIMARY KEY," + COLUMN_MD_ID_MENU
			+ " INTEGER NOT NULL," + COLUMN_MD_FECHA + " TEXT NOT NULL)";

	// platos menu dia
	public static final String TABLE_PLATOS_MENU_DIA = "platos_menu_dia";
	public static final String COLUMN_PMD_ID_PLATO_MENU_DIA = "id_plato_menu_dia";
	public static final String COLUMN_PMD_ID_PLATO = "id_plato";
	public static final String COLUMN_PMD_ID_MENU_DIA = "id_menu_dia";

	private static final String CREATE_TABLE_PLATOS_MENU_DIA = "CREATE TABLE "
			+ TABLE_PLATOS_MENU_DIA + "(" + COLUMN_PMD_ID_PLATO_MENU_DIA
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PMD_ID_PLATO
			+ " INTEGER NOT NULL," + COLUMN_PMD_ID_MENU_DIA
			+ " INTEGER NOT NULL)";

	// Tipos comanda
	public static final String TABLE_TIPOS_COMANDA = "tipos_comanda";
	public static final String COLUMN_TC_ID_TIPO_COMANDA = "id_tipo_comanda";
	public static final String COLUMN_TC_DESCRIPCION = "descripcion";

	private static final String CREATE_TABLE_TIPOS_COMANDA = "CREATE TABLE "
			+ TABLE_TIPOS_COMANDA + "(" + COLUMN_TC_ID_TIPO_COMANDA
			+ " INTEGER PRIMARY KEY ," + COLUMN_TC_DESCRIPCION
			+ " TEXT NOT NULL)";

	// Comanda menu
	public static final String TABLE_COMANDA_MENU = "comanda_menu";
	public static final String COLUMN_CM_ID_COMANDA_MENU = "id_comanda_menu";
	public static final String COLUMN_CM_ID_DETALLE_COMANDA = "id_detalle_comanda";
	public static final String COLUMN_CM_ID_PLATO = "id_plato";
	public static final String COLUMN_CM_CANTIDAD = "cantidad";
	public static final String COLUMN_CM_ESTADO = "estado";

	private static final String CREATE_TABLE_COMANDA_MENU = "CREATE TABLE "
			+ TABLE_COMANDA_MENU + "(" + COLUMN_CM_ID_COMANDA_MENU
			+ " INTEGER PRIMARY KEY ," + COLUMN_CM_ID_PLATO
			+ " INTEGER NOT NULL ," + COLUMN_CM_ID_DETALLE_COMANDA
			+ " INTEGER NOT NULL ," + COLUMN_CM_CANTIDAD
			+ " INTEGER NOT NULL ," + COLUMN_CM_ESTADO + " TEXT NOT NULL)";

	// Comanda menu
	public static final String TABLE_COMANDA_ARTICULO_PER = "comanda_articulo_per";
	public static final String COLUMN_CAM_ID_COMANDA_ARTICULO_PER = "id_comanda_articulo_per";
	public static final String COLUMN_CAM_ID_DETALLE_COMANDA = "id_detalle_comanda";
	public static final String COLUMN_CAM_ID_INGREDIENTE = "id_ingrediente";
	public static final String COLUMN_CAM_PRECIO = "precio";

	private static final String CREATE_TABLE_COMANDA_ARTICULO_PER = "CREATE TABLE "
			+ TABLE_COMANDA_ARTICULO_PER
			+ "("
			+ COLUMN_CAM_ID_COMANDA_ARTICULO_PER
			+ " INTEGER PRIMARY KEY ,"
			+ COLUMN_CAM_ID_DETALLE_COMANDA
			+ " INTEGER NOT NULL ,"
			+ COLUMN_CAM_ID_INGREDIENTE
			+ " INTEGER NOT NULL ,"
			+ COLUMN_CAM_PRECIO + " REAL NOT NULL )";

	// Detalle comanda
	public static final String TABLE_DETALLE_COMANDA = "detalle_comanda";
	public static final String COLUMN_DC_ID_DETALLE_COMANDA = "id_detalle_comanda";
	public static final String COLUMN_DC_ID_TIPO_COMANDA = "id_tipo_comanda";
	public static final String COLUMN_DC_CANTIDAD = "cantidad";
	public static final String COLUMN_DC_PRECIO = "precio";
	public static final String COLUMN_DC_ID_COMANDA = "id_comanda";
	public static final String COLUMN_DC_ESTADO = "estado";
	public static final String COLUMN_DC_ID_ARTICULO = "id_articulo";

	private static final String CREATE_TABLE_DETALLE_COMANDA = "CREATE TABLE "
			+ TABLE_DETALLE_COMANDA
			+ "("
			+ COLUMN_DC_ID_DETALLE_COMANDA
			+ " INTEGER PRIMARY KEY ,"
			+ COLUMN_DC_ID_TIPO_COMANDA
			+ " INTEGER NOT NULL ,"
			+ COLUMN_DC_CANTIDAD
			+ " INTEGER NOT NULL ,"
			+ COLUMN_DC_ID_COMANDA
			+ " INTEGER NOT NULL ,"
			+ COLUMN_DC_ESTADO
			+ " TEXT NOT NULL ,"
			+ COLUMN_DC_ID_ARTICULO
			+ " INTEGER NOT NULL ,"
			+ COLUMN_CAM_PRECIO + " REAL NOT NULL )";

	public LocalSQLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_DIRECCIONES);
		database.execSQL(CREATE_TABLE_ARTICULOS);
		database.execSQL(CREATE_TABLE_INGREDIENTES);
		database.execSQL(CREATE_TABLE_INGREDIENTES_ARTICULOS);
		database.execSQL(CREATE_TABLE_TIPOS_ARTICULO_LOCAL);
		database.execSQL(CREATE_TABLE_TIPOS_ARTICULO);
		database.execSQL(CREATE_TABLE_USUARIOS);
		database.execSQL(CREATE_TABLE_PEDIDOS);
		database.execSQL(CREATE_TABLE_ARTICULOS_PEDIDOS);
		database.execSQL(CREATE_TABLE_DETALLE_ARTICULOS_PERSONALIZADOS);
		database.execSQL(CREATE_TABLE_TIPOS_SERVICIO);
		database.execSQL(CREATE_TABLE_SERVICIOS_LOCAL);
		database.execSQL(CREATE_TABLE_DIAS_SEMANA);
		database.execSQL(CREATE_TABLE_HORARIOS_LOCAL);
		database.execSQL(CREATE_TABLE_ARTICULOS_PERSONALIZADOS);
		database.execSQL(CREATE_TABLE_HORARIOS_PEDIDO);
		database.execSQL(CREATE_TABLE_DIAS_CIERRE);
		database.execSQL(CREATE_TABLE_MESAS);
		database.execSQL(CREATE_TIPOS_MENU);
		database.execSQL(CREATE_TABLE_RESERVAS);
		database.execSQL(CREATE_TABLE_MESAS_RESERVA);
		database.execSQL(CREATE_TABLE_DIAS_CIERRE_RESERVA);
		database.execSQL(CREATE_TABLE_TIPOS_PLATO);
		database.execSQL(CREATE_TABLE_PLATOS);
		database.execSQL(CREATE_TABLE_MENUS);
		database.execSQL(CREATE_TABLE_MENUS_DIA);
		database.execSQL(CREATE_TABLE_PLATOS_MENU_DIA);
		database.execSQL(CREATE_TABLE_TIPOS_COMANDA);
		database.execSQL(CREATE_TABLE_COMANDA_MENU);
		database.execSQL(CREATE_TABLE_COMANDA_ARTICULO_PER);
		database.execSQL(CREATE_TABLE_DETALLE_COMANDA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		onCreate(database);
	}

}
