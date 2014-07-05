package com.brymm.brymmapp.usuario.pojo;

public class ReservaUsuario {

	private int idReserva;
	private int idLocal;
	private int numeroPersonas;
	private String fecha;
	private String hora;
	private String estado;
	private String motivo;
	private String observaciones;
	private String nombreLocal;
	private String tipoMenu;

	public ReservaUsuario(int idReserva, int idLocal, int numeroPersonas,
			String fecha, String hora, String estado, String motivo,
			String observaciones, String nombreLocal, String tipoMenu) {
		super();
		this.idReserva = idReserva;
		this.idLocal = idLocal;
		this.numeroPersonas = numeroPersonas;
		this.fecha = fecha;
		this.hora = hora;
		this.estado = estado;
		this.motivo = motivo;
		this.observaciones = observaciones;
		this.nombreLocal = nombreLocal;
		this.tipoMenu = tipoMenu;
	}

	public int getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(int idReserva) {
		this.idReserva = idReserva;
	}

	public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public int getNumeroPersonas() {
		return numeroPersonas;
	}

	public void setNumeroPersonas(int numeroPersonas) {
		this.numeroPersonas = numeroPersonas;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getNombreLocal() {
		return nombreLocal;
	}

	public void setNombreLocal(String nombreLocal) {
		this.nombreLocal = nombreLocal;
	}

	public String getTipoMenu() {
		return tipoMenu;
	}

	public void setTipoMenu(String tipoMenu) {
		this.tipoMenu = tipoMenu;
	}

}
