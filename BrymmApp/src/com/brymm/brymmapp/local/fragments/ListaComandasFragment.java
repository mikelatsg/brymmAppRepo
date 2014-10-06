package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.DetalleComandaActivity;
import com.brymm.brymmapp.local.adapters.ComandaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionComanda;
import com.brymm.brymmapp.local.pojo.Comanda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListaComandasFragment extends Fragment {

	public static final String EXTRA_ID_ESTADO = "extraIdEstado";
	public static final int REQUEST_CODE_DETALLE = 1;

	private ListView lvComandas;
	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_comandas,
				container, false);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu_articulo, menu);
	}

	private void inicializar() {
		lvComandas = (ListView) getActivity().findViewById(
				R.id.listaComandasLvLista);

		/* Se guarda si esta el fragmento de detalle */
		View detalleFrame = getActivity().findViewById(R.id.detalleComandaFl);
		mDualPane = detalleFrame != null
				&& detalleFrame.getVisibility() == View.VISIBLE;

		// Se recoge el estado pasado
		Bundle bundle = getArguments();
		String estado = bundle.getString(EXTRA_ID_ESTADO);

		actualizarLista(estado);

		lvComandas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				Comanda comanda = (Comanda) adapter.getItemAtPosition(position);
				mostrarDetalleComanda(comanda);
			}
		});

	}

	public void actualizarLista(String estado) {
		List<Comanda> comandas = new ArrayList<Comanda>();
		GestionComanda gc = new GestionComanda(getActivity());
		if (estado.equals(GestionComanda.ESTADO_TERMINADO_COCINA)) {
			comandas = gc.obtenerComandasActivas();
		} else if (estado.equals(GestionComanda.ESTADO_ENVIADO_COCINA)) {
			comandas = gc.obtenerComandasActivas();
		} else if (estado.equals(GestionComanda.ESTADO_ACTIVO)) {
			comandas = gc.obtenerComandasActivas();
		} else if (estado.equals(GestionComanda.ESTADO_CANCELADO_CAMARERO)) {
			comandas = gc.obtenerComandasCerradas();
		} else if (estado.equals(GestionComanda.ESTADO_TERMINADO_CAMARERO)) {
			comandas = gc.obtenerComandasCerradas();
		} else if (estado.equals(GestionComanda.ESTADO_CERRADA)) {
			comandas = gc.obtenerComandasCerradas();
		}

		gc.cerrarDatabase();

		ComandaAdapter comandaAdapter = new ComandaAdapter(getActivity(),
				R.layout.comanda_item, comandas);		
		
		lvComandas.setAdapter(comandaAdapter);
				
	}

	private void mostrarDetalleComanda(Comanda comanda) {
		if (mDualPane) {
			DetalleComandaFragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = new DetalleComandaFragment();

			Bundle bundle = new Bundle();
			bundle.putParcelable(DetalleComandaFragment.EXTRA_COMANDA, comanda);
			detalleFragment.setArguments(bundle);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleComandaFl, detalleFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

		} else {

			Intent intent = new Intent(getActivity(),
					DetalleComandaActivity.class);
			intent.putExtra(DetalleComandaFragment.EXTRA_COMANDA, comanda);
			startActivityForResult(intent, REQUEST_CODE_DETALLE);
		}

	}

	public void ocultarDetalle() {
		if (mDualPane) {
			DetalleComandaFragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = (DetalleComandaFragment) getFragmentManager()
					.findFragmentById(R.id.detalleComandaFl);

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
