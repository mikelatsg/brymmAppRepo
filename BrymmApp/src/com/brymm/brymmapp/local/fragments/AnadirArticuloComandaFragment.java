package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.adapters.ArticuloComandaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionTipoComanda;
import com.brymm.brymmapp.local.interfaces.AnadibleComanda;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.local.pojo.TipoComanda;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AnadirArticuloComandaFragment extends Fragment implements
		AnadibleComanda {

	private ListView lvArticulos;

	private Button btCerrar;

	private boolean mDualPane, esCrear;
	private Comanda comanda;

	OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adaptador, View view,
				int posicion, long id) {
			Articulo articulo = (Articulo) lvArticulos.getAdapter().getItem(
					posicion);
			dialogoCantidad(articulo);
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
				R.layout.fragment_anadir_articulos_comanda, container, false);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {
		lvArticulos = (ListView) getActivity().findViewById(
				R.id.anadirArticulosComandaLvLista);
		btCerrar = (Button) getActivity().findViewById(
				R.id.anadirArticulosComandaBtCerrar);

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

		lvArticulos.setOnItemClickListener(oicl);
		actualizarLista();
		btCerrar.setOnClickListener(oclCerrar);

	}

	public void actualizarLista() {
		List<Articulo> articulos = new ArrayList<Articulo>();
		GestionArticulo ga = new GestionArticulo(getActivity());
		articulos = ga.obtenerArticulos();
		ga.cerrarDatabase();

		ArticuloComandaAdapter articuloComandaAdapter = new ArticuloComandaAdapter(
				getActivity(), R.layout.articulo_comanda_item, articulos);

		lvArticulos.setAdapter(articuloComandaAdapter);
	}

	private void anadirArticulo(ArticuloCantidad articuloCantidad) {
		GestionTipoComanda gtc = new GestionTipoComanda(getActivity());
		TipoComanda tipoComanda = gtc.obtenerTipoComanda(1);
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

	private void dialogoCantidad(final Articulo articulo) {
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
