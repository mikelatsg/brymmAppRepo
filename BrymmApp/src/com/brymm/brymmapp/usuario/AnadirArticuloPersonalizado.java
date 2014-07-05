package com.brymm.brymmapp.usuario;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.R.id;
import com.brymm.brymmapp.R.layout;
import com.brymm.brymmapp.R.menu;
import com.brymm.brymmapp.R.string;
import com.brymm.brymmapp.usuario.bbdd.GestionIngredientesLocal;
import com.brymm.brymmapp.usuario.bbdd.GestionTiposArticulo;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalCantidad;
import com.brymm.brymmapp.usuario.pojo.ArticuloPersonalizado;
import com.brymm.brymmapp.usuario.pojo.IngredienteLocal;
import com.brymm.brymmapp.usuario.pojo.TipoArticulo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AnadirArticuloPersonalizado extends Activity {

	public static final String EXTRA_ARTICULO_PERSONALIZADO = "extraArticuloPersonalizado";

	private Button bAceptar, bCancelar;
	private EditText etCantidad;
	private List<CheckBox> checks = new ArrayList<CheckBox>();
	private List<TipoArticulo> tiposArticulo;
	Spinner spTipoArticulo;
	OnClickListener oclAceptar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ArticuloPersonalizado articulo = generarArticuloPersonalizado();
			if (articulo != null) {
				devolverArticuloPersonalizado(articulo);
			}
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
		setContentView(R.layout.activity_anadir_articulo_personalizado);
		inicializar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.anadir_articulo_personalizado, menu);
		return true;
	}

	private void inicializar() {
		bAceptar = (Button) findViewById(R.id.anadirArticuloPersonalizadoBtAceptar);
		bCancelar = (Button) findViewById(R.id.anadirArticuloPersonalizadoBtCancelar);
		etCantidad = (EditText) findViewById(R.id.anadirArticuloPersonalizadoEtCantidad);

		bAceptar.setOnClickListener(oclAceptar);
		bCancelar.setOnClickListener(oclCancelar);

		// Se obtienen los ingredientes
		GestionIngredientesLocal gil = new GestionIngredientesLocal(this);
		List<IngredienteLocal> ingredientes = gil.obtenerIngredientesLocal();
		gil.cerrarDatabase();

		// Se obtienen los tipos de articulo personalizables
		GestionTiposArticulo gta = new GestionTiposArticulo(this);
		tiposArticulo = gta.obtenerTiposArticuloPersonalizables();
		gta.cerrarDatabase();

		LinearLayout ll = (LinearLayout) findViewById(R.id.anadirArticuloPersonalizadoLlPrincipal);

		// Se a�ade un spinner con los tipos de articulo
		List<String> spArray = new ArrayList<String>();

		for (TipoArticulo tipoArticulo : tiposArticulo) {
			spArray.add(tipoArticulo.getNombre());
		}

		spTipoArticulo = new Spinner(this);
		ArrayAdapter<String> spArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spArray);
		spTipoArticulo.setAdapter(spArrayAdapter);

		ll.addView(spTipoArticulo);

		// Se a�ade un checkbox por cada ingrediente
		for (IngredienteLocal ingredienteLocal : ingredientes) {
			CheckBox cb = new CheckBox(this);
			cb.setText(ingredienteLocal.getNombre());
			cb.setTag(ingredienteLocal);
			ll.addView(cb);
			checks.add(cb);
		}
	}

	private ArticuloPersonalizado generarArticuloPersonalizado() {

		ArticuloPersonalizado articuloPersonalizado = null;

		// Si no se ha indicado cantidad se devuelve nulo.
		if (!(etCantidad.getText().toString().length() > 0)) {
			return articuloPersonalizado;
		}

		int cantidad = Integer.parseInt(etCantidad.getText().toString());

		List<IngredienteLocal> ingredientes = new ArrayList<IngredienteLocal>();
		for (CheckBox check : checks) {
			if (check.isChecked()) {
				ingredientes.add((IngredienteLocal) check.getTag());
			}
		}

		// Si no hay ingredientes seleccionados se devuelve el articulo nulo.
		if (ingredientes.size() == 0) {
			return articuloPersonalizado;
		}

		Resources res = getResources();

		TipoArticulo tipoArticulo = tiposArticulo.get(spTipoArticulo
				.getSelectedItemPosition());
		articuloPersonalizado = new ArticuloPersonalizado(
				res.getString(R.string.anadir_articulo_personalizado_articulo_personalizado),
				tipoArticulo, cantidad, ingredientes);

		return articuloPersonalizado;
	}

	private void devolverArticuloPersonalizado(ArticuloPersonalizado articulo) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_ARTICULO_PERSONALIZADO, articulo);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void volver() {
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

}
