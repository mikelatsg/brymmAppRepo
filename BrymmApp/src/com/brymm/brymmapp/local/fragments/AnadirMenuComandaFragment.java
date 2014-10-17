package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.bbdd.GestionMenu;
import com.brymm.brymmapp.local.bbdd.GestionMenuDia;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticuloLocal;
import com.brymm.brymmapp.local.bbdd.GestionTipoComanda;
import com.brymm.brymmapp.local.interfaces.AnadibleComanda;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.MenuComanda;
import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.local.pojo.Plato;
import com.brymm.brymmapp.local.pojo.PlatoComanda;
import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;
import com.brymm.brymmapp.local.pojo.TipoComanda;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class AnadirMenuComandaFragment extends Fragment implements AnadibleComanda {

	private Spinner spMenus;

	private Button btCerrar, btAnadir;
	private EditText etCantidadMenu;
	private LinearLayout llPlatos;
	private int RESOURCE_PLATOS = 0;

	private boolean mDualPane;
	private Comanda comanda;
	private List<CheckBox> checks = new ArrayList<CheckBox>();
	private OnClickListener oclAnadir = new OnClickListener() {

		@Override
		public void onClick(View v) {
			MenuComanda menuComanda = obtenerMenuComanda();
			anadirMenu(menuComanda);
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
		View root = inflater.inflate(R.layout.fragment_anadir_menu_comanda,
				container, false);
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

		etCantidadMenu = (EditText) getActivity().findViewById(
				R.id.anadirMenuComandaEtCantidadMenu);

		llPlatos = (LinearLayout) getActivity().findViewById(
				R.id.anadirMenuComandaLlPlatos);

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
				+ Integer.toString(c.get(Calendar.MONTH) + 1) + "-"
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
		// Vacio el linear layout platos
		llPlatos.removeAllViews();

		checks = new ArrayList<CheckBox>();

		/* A�adir los ingredientes dinamicamente */

		// Obtengo los platos del menu
		GestionMenuDia gestor = new GestionMenuDia(getActivity());
		MenuDia menuDia = (MenuDia) spMenus.getSelectedItem();
		List<Plato> platos = gestor
				.obtenerPlatosMenuDia(menuDia.getIdMenuDia());
		gestor.cerrarDatabase();

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
		// llPrincipal.addView(llPlatos);
	}

	private MenuComanda obtenerMenuComanda() {
		// Se obtiene el menu seleccionado
		int idMenu = ((MenuDia) spMenus.getSelectedItem()).getMenu()
				.getIdMenu();
		GestionMenu gm = new GestionMenu(getActivity());
		MenuLocal menu = gm.obtenerMenu(idMenu);
		gm.cerrarDatabase();

		// Se obtienen los platos seleccionados
		List<PlatoComanda> platosComanda = new ArrayList<PlatoComanda>();
		for (CheckBox checkBox : this.checks) {
			if (checkBox.isChecked()) {
				Plato plato = (Plato) checkBox.getTag();
				EditText etTemporal = (EditText) getActivity().findViewById(
						plato.getIdPlato());

				PlatoComanda platoComanda = new PlatoComanda(0, plato, "",
						Integer.parseInt(etTemporal.getText().toString()));

				platosComanda.add(platoComanda);
			}
		}

		MenuComanda menuComanda = new MenuComanda(menu, platosComanda,
				Integer.parseInt(etCantidadMenu.getText().toString()));
		return menuComanda;
	}

	private void anadirMenu(MenuComanda menuComanda) {
		GestionTipoComanda gtc = new GestionTipoComanda(getActivity());
		TipoComanda tipoComanda;
		Float precioDetalle = (float) 0;
		// Obtengo el tipo comanda y el precio dependiendo de si es carta o no
		if (menuComanda.getMenu().isCarta()) {
			tipoComanda = gtc.obtenerTipoComanda(4);
			for (PlatoComanda platoComanda : menuComanda.getPlatos()) {
				precioDetalle = platoComanda.getPrecio()
						* platoComanda.getCantidad();
			}
		} else {
			tipoComanda = gtc.obtenerTipoComanda(3);
			precioDetalle = menuComanda.getMenu().getPrecio();
		}
		gtc.cerrarDatabase();

		DetalleComanda detalleComanda = new DetalleComanda(0, tipoComanda,
				menuComanda.getCantidad(), precioDetalle, "", null, menuComanda);

		List<DetalleComanda> detallesComanda = null;
		// Si es nulo inicializo los detalles.
		if (this.comanda.getDetallesComanda() == null) {
			detallesComanda = new ArrayList<DetalleComanda>();
		} else {
			detallesComanda = this.comanda.getDetallesComanda();
		}

		float precio = this.comanda.getPrecio();
		precio = precio
				+ (detalleComanda.getPrecio() * detalleComanda.getCantidad());
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

	@Override
	public void vaciarDetalle() {
		if (mDualPane) {
			List<DetalleComanda> detallesComanda = new ArrayList<DetalleComanda>();
			this.comanda = null;

			this.comanda = new Comanda(0, "", null, "", (float) 0, null, "", "",
					detallesComanda);

		} 
		
	}

}
