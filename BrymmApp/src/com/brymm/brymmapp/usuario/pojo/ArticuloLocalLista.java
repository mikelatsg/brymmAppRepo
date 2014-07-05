package com.brymm.brymmapp.usuario.pojo;

import java.util.List;

public class ArticuloLocalLista extends ArticuloLocal {

	private boolean seccion;

	public ArticuloLocalLista(int idArticulo, int idLocal, int idTipoArticulo,
			String nombre, String descripcion, float precio,
			String tipoArticulo, List<String> ingredientes, boolean seccion) {
		super(idArticulo, idLocal, idTipoArticulo, nombre, descripcion, precio,
				tipoArticulo, ingredientes);
		this.seccion = seccion;
	}

	public ArticuloLocalLista(ArticuloLocal articulo, boolean seccion) {
		super(articulo.getIdArticulo(), articulo.getIdLocal(), articulo
				.getIdTipoArticulo(), articulo.getNombre(), articulo
				.getDescripcion(), articulo.getPrecio(), articulo
				.getTipoArticulo(), articulo.getIngredientes());
		this.seccion = seccion;
	}

	public boolean isSeccion() {
		return seccion;
	}

	public void setSeccion(boolean seccion) {
		this.seccion = seccion;
	}

}
