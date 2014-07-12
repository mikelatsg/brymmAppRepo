package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.DetalleMenuDiaActivity;
import com.brymm.brymmapp.local.adapters.MenuAdapter;
import com.brymm.brymmapp.local.adapters.MenuDiaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionMenu;
import com.brymm.brymmapp.local.bbdd.GestionMenuDia;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.MenuLocal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ListaMenusDiaFragment extends Fragment implements ListaEstado {

	public static final String EXTRA_FECHA = "extraFecha";
	public static final int REQUEST_CODE_DETALLE = 1;

	private ListView lvMenus;
	private ListView lvSeleccionMenu;
	private Button btAnadirMenuDia;

	private boolean mDualPane;
	private String fecha;

	private OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long arg3) {
			MenuDia menuDia = (MenuDia) adapter.getItemAtPosition(position);

			mostrarDetalleMenu(menuDia);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_menus_dia,
				container, false);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {
		lvMenus = (ListView) getActivity().findViewById(
				R.id.listaMenusDiaLvLista);		
		
		btAnadirMenuDia = (Button) getActivity().findViewById(R.id.listaMenusDiaBtAnadirMenu);
		btAnadirMenuDia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogoSeleccionarMenu();
			}
		});
			
		

		/* Se guarda si esta el fragmento de añadir */
		View detalleFrame = getActivity()
				.findViewById(R.id.detalleMenuDiaFl);
		mDualPane = detalleFrame != null
				&& detalleFrame.getVisibility() == View.VISIBLE;

		// Se recoge el estado pasado
		Intent intent = getActivity().getIntent();
		this.fecha = intent.getStringExtra(EXTRA_FECHA);

		lvMenus.setOnItemClickListener(oicl);		

		actualizarLista(this.fecha);

		// registerForContextMenu(lvReservas);

	}
	
	@SuppressLint("NewApi")
	private void dialogoSeleccionarMenu() {
		final Dialog custom = new Dialog(getActivity());
		custom.setContentView(R.layout.dialog_seleccionar_menu_dia);
		
		lvSeleccionMenu = (ListView) custom.findViewById(
				R.id.listaMenusDialogoListaMenus);
				
		custom.setTitle("Custom Dialog");
		List<MenuLocal> menus = new ArrayList<MenuLocal>();
		GestionMenu gestor = new GestionMenu(getActivity());
		menus = gestor.obtenerMenus();
		gestor.cerrarDatabase();

		MenuAdapter menuAdapter = new MenuAdapter(getActivity(),
				R.layout.menu_item, menus);

		lvSeleccionMenu.setAdapter(menuAdapter);
		
		custom.show();

	}

	public void actualizarLista(String fecha) {
		List<MenuDia> menus = new ArrayList<MenuDia>();
		GestionMenuDia gestor = new GestionMenuDia(getActivity());
		menus = gestor.obtenerMenusDia(fecha);
		gestor.cerrarDatabase();

		MenuDiaAdapter menuDiaAdapter = new MenuDiaAdapter(getActivity(),
				R.layout.menu_item, menus);

		lvMenus.setAdapter(menuDiaAdapter);
	}

	

	private void mostrarDetalleMenu(MenuDia menuDia) {
		if (mDualPane) {
			DetalleMenuDiaFragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = new DetalleMenuDiaFragment();

			Bundle args = new Bundle();
			args.putParcelable(DetalleMenuDiaFragment.EXTRA_MENU_DIA, menuDia);			
			detalleFragment.setArguments(args);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleMenuDiaFl, detalleFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					DetalleMenuDiaActivity.class);
			intent.putExtra(DetalleMenuDiaFragment.EXTRA_MENU_DIA, menuDia);			
			startActivity(intent);
		}

	}

	public void ocultarDetalle() {
		if (mDualPane) {
			Fragment detalleFragment;

			detalleFragment = getFragmentManager().findFragmentById(
					R.id.detalleMenuDiaFl);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			if (detalleFragment != null) {

				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.remove(detalleFragment);

				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}
		}
	}


}
