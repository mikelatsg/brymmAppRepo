package com.brymm.brymmapp.usuario.pojo;

public class HorarioPedido {

	private int idHorarioPedido;
	private int idLocal;
	private int idDia;
	private String horaInicio;
	private String horaFin;
	private String dia;

	public HorarioPedido(int idHorarioPedido, int idLocal, int idDia,
			String horaInicio, String horaFin, String dia) {
		super();
		this.idHorarioPedido = idHorarioPedido;
		this.idLocal = idLocal;
		this.idDia = idDia;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.dia = dia;
	}

	public int getIdHorarioPedido() {
		return idHorarioPedido;
	}

	public void setIdHorarioPedido(int idHorarioPedido) {
		this.idHorarioPedido = idHorarioPedido;
	}

	public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public int getIdDia() {
		return idDia;
	}

	public void setIdDia(int idDia) {
		this.idDia = idDia;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	public String getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(String horaFin) {
		this.horaFin = horaFin;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

}
