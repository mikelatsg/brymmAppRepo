package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.bbdd.GestionMenuDia;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticuloLocal;
import com.brymm.brymmapp.local.bbdd.GestionTipoComanda;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.MenuComanda;
import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.Plato;
import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class AnadirMenuComandaFragment extends Fragment {

	private Spinner spMenus;

	private Button btCerrar, btAnadir;
	private int RESOURCE_PLATOS = 0;
	

	private boolean mDualPane;
	private Comanda comanda;
	private List<CheckBox> checks = new ArrayList<CheckBox>();
	private OnClickListener oclAnadir = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};

	private OnClickListener oclCerrar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			cerrar();

		}
	};

	private OnItemSelectedListener oisl = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			actualizarPlatosMenu();

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

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
				R.id.anadirMenuComandaBtCerrar);

		btAnadir = (Button) getActivity().findViewById(
				R.id.anadirMenuComandaBtAnadir);

		spMenus = (Spinner) getActivity().findViewById(
				R.id.anadirMenuComandaSpMenus);

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
		}

		actualizarMenus();
		actualizarPlatosMenu();
		spMenus.setOnItemSelectedListener(oisl);

		btAnadir.setOnClickListener(oclAnadir);
		btCerrar.setOnClickListener(oclCerrar);
	}

	public void actualizarMenus() {

		Calendar c = Calendar.getInstance();

		String fecha = Integer.toString(c.get(Calendar.YEAR)) + "-"
				+ Integer.toString(c.get(Calendar.MONTH)) + "-"
				+ Integer.toString(c.get(Calendar.DAY_OF_MONTH));

		List<MenuDia> menusDia = new ArrayList<MenuDia>();
		GestionMenuDia gestor = new GestionMenuDia(getActivity());
		menusDia = gestor.obtenerMenusDia(fecha);
		gestor.cerrarDatabase();

		ArrayAdapter<MenuDia> menusDiaAdapter = new ArrayAdapter<MenuDia>(
				getActivity(), android.R.layout.simple_spinner_item, menusDia);

		spMenus.setAdapter(menusDiaAdapter);

	}

	private void actualizarPlatosMenu() {
		// Si existe el LinearLayout de los platos lo borro.
		LinearLayout llPlatos = (LinearLayout) getActivity().findViewById(
				RESOURCE_PLATOS);

		if (llPlatos != null) {
			llPlatos.setVisibility(View.GONE);
		}

		/* Añadir los ingredientes dinamicamente */
		// Obtengo el linearlayout principal.
		LinearLayout llPrincipal = (LinearLayout) getActivity().findViewById(
				R.id.anadirMenuComandaLlPrincipal);

		// Obtengo los platos del menu
		GestionMenuDia gestor = new GestionMenuDia(getActivity());
		MenuDia menuDia = (MenuDia) spMenus.getSelectedItem();
		List<Plato> platos = gestor
				.obtenerPlatosMenuDia(menuDia.getIdMenuDia());
		gestor.cerrarDatabase();

		// Creo un linearlayout para meter los checks
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		llPlatos = (LinearLayout) inflater
				.inflate(RESOURCE_PLATOS, null, false);

		// Generar dinamicamente los platos.
		for (Plato plato : platos) {
			LinearLayout llPlato = new LinearLayout(getActivity());
			llPlato.setOrientation(LinearLayout.HORIZONTAL);
					
			EditText et = new EditText(getActivity());
			et.setText("0");
			et.setId(plato.getIdPlato());
			CheckBox cb = new CheckBox(getActivity());
			cb.setText(plato.getNombre());
			cb.setTag(plato);
			llPlato.addView(cb);
			llPlato.addView(et);
			checks.add(cb);

			llPlatos.addView(llPlato);
		}
		llPrincipal.addView(llPlatos);
	}

	private void anadirMenu(MenuComanda menuComanda) {
		GestionTipoComanda gtc = new GestionTipoComanda(getActivity());
		TipoComanda tipoComanda;
		if (menuComanda.getMenu().isCarta()) {
			tipoComanda = gtc.obtenerTipoComanda(4);
		} else {
			tipoComanda = gtc.obtenerTipoComanda(3);
		}
		gtc.cerrarDatabase();

		DetalleComanda detalleComanda = new DetalleComanda(0, tipoComanda,
				menuComanda.getCantidad(), menuComanda.getCantidad()
						* menuComanda.getMenu().getPrecio(), "", null,
				menuComanda);

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
			CrearComandaFragment crearFragment;

			// Make new fragment to show this selection.
			crearFragment = (CrearComandaFragment) getFragmentManager()
					.findFragmentById(R.id.listaComandasFl);

			crearFragment.setComanda(this.comanda);
			crearFragment.actualizarComanda();

		}
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
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();

		}
	}

}
