package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticuloLocal;
import com.brymm.brymmapp.local.bbdd.GestionTipoComanda;
import com.brymm.brymmapp.local.interfaces.AnadibleComanda;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;
import com.brymm.brymmapp.local.pojo.TipoComanda;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class AnadirArticuloPerComandaFragment extends Fragment implements
		AnadibleComanda {

	private Spinner spTiposArticulo;

	private Button btCerrar, btAnadir;

	private boolean mDualPane, esCrear;
	private Comanda comanda;
	private List<CheckBox> checks = new ArrayList<CheckBox>();
	private OnClickListener oclAnadir = new OnClickListener() {

		@Override
		public void onClick(View v) {

			// Se comprueba si ha seleccionado algún ingrediente
			boolean algunIngredienteSeleccionado = false;
			for (CheckBox check : checks) {
				if (check.isChecked()) {
					algunIngredienteSeleccionado = true;
				}
			}
			if (algunIngredienteSeleccionado) {
				dialogoCantidad();
			} else {
				Resources res = getActivity().getResources();
				Toast.makeText(
						getActivity(),
						R.string.anadir_articulo_per_comanda_no_ingrediente_sel,
						Toast.LENGTH_LONG).show();
			}

		}
	};

	private OnClickListener oclCerrar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			cerrar();

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(
				R.layout.fragment_anadir_articulos_per_comanda, container,
				false);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {
		btCerrar = (Button) getActivity().findViewById(
				R.id.anadirArticuloPerComandaBtCerrar);

		btAnadir = (Button) getActivity().findViewById(
				R.id.anadirArticuloPerComandaBtAnadir);

		spTiposArticulo = (Spinner) getActivity().findViewById(
				R.id.anadirArticuloPerComandaSpTipoArticulo);

		/* Se guarda si esta el fragmento de crear comanda */
		View anadirFrame = getActivity().findViewById(R.id.listaComandasFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		if (mDualPane) {
			Bundle bundle = getArguments();
			if (bundle != null) {
				this.comanda = bundle
						.getParcelable(CrearComandaFragment.EXTRA_COMANDA);
			}
		} else {
			Intent intent = getActivity().getIntent();
			this.comanda = intent
					.getParcelableExtra(CrearComandaFragment.EXTRA_COMANDA);
			this.esCrear = intent.getBooleanExtra(
					CrearComandaFragment.EXTRA_CREAR, true);
		}

		actualizarTiposArticulo();

		/* Añadir los ingredientes dinamicamente */
		// Obtengo el linearlayout principal.
		LinearLayout llPrincipal = (LinearLayout) getActivity().findViewById(
				R.id.anadirArticuloPerComandaLlPrincipal);

		// Obtengo los ingredientes
		GestionIngrediente gi = new GestionIngrediente(getActivity());
		List<Ingrediente> ingredientes = gi.obtenerIngredientes();
		gi.cerrarDatabase();

		// Generar dinamicamente los platos.
		for (Ingrediente ingrediente : ingredientes) {
			CheckBox cb = new CheckBox(getActivity());
			cb.setText(ingrediente.getNombre());
			cb.setTag(ingrediente);
			llPrincipal.addView(cb);
			checks.add(cb);
		}

		btAnadir.setOnClickListener(oclAnadir);
		btCerrar.setOnClickListener(oclCerrar);
	}

	public void actualizarTiposArticulo() {

		List<TipoArticuloLocal> tiposArticuloLocal = new ArrayList<TipoArticuloLocal>();
		GestionTipoArticuloLocal gestor = new GestionTipoArticuloLocal(
				getActivity());
		tiposArticuloLocal = gestor.obtenerTiposArticuloLocalPersonalizables();
		gestor.cerrarDatabase();

		ArrayAdapter<TipoArticuloLocal> tipoArticuloLocalAdapter = new ArrayAdapter<TipoArticuloLocal>(
				getActivity(), android.R.layout.simple_spinner_item,
				tiposArticuloLocal);

		spTiposArticulo.setAdapter(tipoArticuloLocalAdapter);

	}

	private void anadirArticulo(ArticuloCantidad articuloCantidad) {
		GestionTipoComanda gtc = new GestionTipoComanda(getActivity());
		TipoComanda tipoComanda = gtc.obtenerTipoComanda(2);
		gtc.cerrarDatabase();

		DetalleComanda detalleComanda = new DetalleComanda(0, tipoComanda,
				articuloCantidad.getCantidad(), articuloCantidad.getCantidad()
						* articuloCantidad.getPrecio(), "", articuloCantidad,
				null);

		List<DetalleComanda> detallesComanda = null;
		// Si es nulo inicializo los detalles.
		if (this.comanda.getDetallesComanda() == null) {
			detallesComanda = new ArrayList<DetalleComanda>();
		} else {
			detallesComanda = this.comanda.getDetallesComanda();
		}

		float precio = this.comanda.getPrecio();
		precio = precio + (detalleComanda.getPrecio());
		this.comanda.setPrecio(precio);

		detallesComanda.add(detalleComanda);
		this.comanda.setDetallesComanda(detallesComanda);

		// Con la pantalla dividida actualizo el pedido.
		if (mDualPane) {
			Fragment fragment = (CrearComandaFragment) getFragmentManager()
					.findFragmentById(R.id.listaComandasFl);
			if (fragment instanceof CrearComandaFragment) {
				CrearComandaFragment crearFragment;

				// Make new fragment to show this selection.
				crearFragment = (CrearComandaFragment) getFragmentManager()
						.findFragmentById(R.id.listaComandasFl);

				crearFragment.setComanda(this.comanda);
				crearFragment.actualizarComanda();
			} else {
				AnadirAComandaFragment anadirFragment;

				// Make new fragment to show this selection.
				anadirFragment = (AnadirAComandaFragment) getFragmentManager()
						.findFragmentById(R.id.listaComandasFl);

				anadirFragment.setComanda(this.comanda);
				anadirFragment.actualizarComanda();
			}

		}
	}

	private Articulo obtenerArticulo() {
		Articulo articulo = null;

		TipoArticuloLocal tipoArticuloLocal = (TipoArticuloLocal) spTiposArticulo
				.getSelectedItem();

		List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();
		float precio = (float) 0;
		for (CheckBox check : checks) {
			if (check.isChecked()) {
				ingredientes.add((Ingrediente) check.getTag());
				precio += ((Ingrediente) check.getTag()).getPrecio();
			}
		}

		precio += tipoArticuloLocal.getPrecio();
		articulo = new Articulo(0, tipoArticuloLocal, "Articulo per",
				"Articulo per", precio, false, ingredientes);
		return articulo;
	}

	private void dialogoCantidad() {
		final Dialog custom = new Dialog(getActivity());
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

				Articulo articulo = obtenerArticulo();

				ArticuloCantidad articuloCantidad = new ArticuloCantidad(
						articulo, cantidad);

				anadirArticulo(articuloCantidad);
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

	private void cerrar() {
		if (mDualPane) {
			CrearComandaFragment crearFragment;

			// Make new fragment to show this selection.
			crearFragment = (CrearComandaFragment) getFragmentManager()
					.findFragmentById(R.id.listaComandasFl);

			crearFragment.ocultarDetalle();

		} else {

			Intent intent = new Intent();
			intent.putExtra(CrearComandaFragment.EXTRA_COMANDA, this.comanda);
			intent.putExtra(CrearComandaFragment.EXTRA_CREAR, this.esCrear);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();

		}
	}

	@Override
	public void vaciarDetalle() {
		if (mDualPane) {
			List<DetalleComanda> detallesComanda = new ArrayList<DetalleComanda>();
			this.comanda = null;

			this.comanda = new Comanda(0, "", null, "", (float) 0, null, "",
					"", detallesComanda);

		}

	}

}
