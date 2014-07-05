package com.brymm.brymmapp.usuario;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.adapters.ArticuloAdapter;
import com.brymm.brymmapp.usuario.bbdd.GestionArticuloLocal;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocal;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalCantidad;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalLista;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AnadirArticulo extends Activity {

	public static final String EXTRA_ARTICULO_CANTIDAD = "extraArticuloCantidad";
	private ListView lvArticulos;

	OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adaptador, View view,
				int posicion, long id) {
			ArticuloLocal articuloLocal = (ArticuloLocal) lvArticulos
					.getAdapter().getItem(posicion);
			dialogoCantidad(articuloLocal);
		}
	};
	
	OnClickListener oclCancelar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			volver();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anadir_articulo);
		inicializar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.anadir_articulo, menu);
		return true;
	}

	private void inicializar() {
		Button bCancelar = (Button) findViewById(R.id.anadirArticuloBtCancelar);
		bCancelar.setOnClickListener(oclCancelar);
		
		lvArticulos = (ListView) findViewById(R.id.anadirArticuloLvArticulos);
		

		GestionArticuloLocal gal = new GestionArticuloLocal(this);
		List<ArticuloLocalLista> articulos = gal.obtenerArticulosLocalLista();
		gal.cerrarDatabase();

		ArticuloAdapter adaptador = new ArticuloAdapter(this,
				R.layout.anadir_articulo_item, articulos);

		lvArticulos.setAdapter(adaptador);
		lvArticulos.setOnItemClickListener(oicl);

	}

	private void devolverArticulo(ArticuloLocalCantidad articulo) {
		/*
		 * ArticuloLocal articulo = (ArticuloLocal) lvArticulos.getAdapter()
		 * .getItem(posicion);
		 */
		Intent intent = new Intent();
		intent.putExtra(EXTRA_ARTICULO_CANTIDAD, articulo);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void volver() {
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	private void dialogoCantidad(final ArticuloLocal articuloLocal) {
		final Dialog custom = new Dialog(this);
		custom.setContentView(R.layout.dialog_anadir_articulo);
		final EditText etCantidad = (EditText) custom
				.findViewById(R.id.dialogAnadirArticuloEtCantidad);
		Button bAceptar = (Button) custom
				.findViewById(R.id.dialogAnadirArticuloBtAceptar);
		Button bCancelar = (Button) custom
				.findViewById(R.id.dialogAnadirArticuloBtCancelar);
		custom.setTitle("Custom Dialog");
		bAceptar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				int cantidad = Integer
						.parseInt(etCantidad.getText().toString());
				ArticuloLocalCantidad articuloCantidad = new ArticuloLocalCantidad(
						articuloLocal, cantidad);
				devolverArticulo(articuloCantidad);
				custom.dismiss();
			}

		});
		bCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				custom.dismiss();
			}
		});
		custom.show();

	}

}
