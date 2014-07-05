package com.brymm.brymmapp.util;

public class Resultado {

	public static final String JSON_OPERACION_OK = "operacionOK";
	public static final String JSON_MENSAJE = "mensaje";
	public static final int RES_OK = 1;
	public static final String NO_DATA = "no_data";
	
	private int codigo;
	private String mensaje;

	public Resultado(int codigo, String mensaje) {
		super();
		this.codigo = codigo;
		this.mensaje = mensaje;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
