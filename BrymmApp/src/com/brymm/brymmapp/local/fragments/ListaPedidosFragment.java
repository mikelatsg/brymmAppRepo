package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.List;


import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.DetallePedidoActivity;
import com.brymm.brymmapp.local.adapters.PedidoAdapter;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.pojo.Pedido;

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

public class ListaPedidosFragment extends Fragment {

	public static final String EXTRA_ID_ESTADO = "extraIdEstado";
	public static final int REQUEST_CODE_DETALLE = 1;

	private ListView lvPedidos;
	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_pedidos,
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
		lvPedidos = (ListView) getActivity().findViewById(
				R.id.listaPedidosLvLista);

		/* Se guarda si esta el fragmento de detalle */
		View detalleFrame = getActivity().findViewById(R.id.detallePedidoFl);
		mDualPane = detalleFrame != null
				&& detalleFrame.getVisibility() == View.VISIBLE;

		// Se recoge el estado pasado
		Bundle bundle = getArguments();
		String estado = bundle.getString(EXTRA_ID_ESTADO);

		actualizarLista(estado);

		lvPedidos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {
				Pedido pedido = (Pedido) adapter.getItemAtPosition(position);
				mostrarDetallePedido(pedido);
			}
		});

	}

	public void actualizarLista(String estado) {
		List<Pedido> pedidos = new ArrayList<Pedido>();
		GestionPedido gp = new GestionPedido(getActivity());
		pedidos = gp.obtenerPedidos(estado);
		gp.cerrarDatabase();

		PedidoAdapter pedidoAdapter = new PedidoAdapter(getActivity(),
				R.layout.pedido_local_item, pedidos);

		lvPedidos.setAdapter(pedidoAdapter);
	}

	private void mostrarDetallePedido(Pedido pedido) {
		if (mDualPane) {
			DetallePedidoFragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = new DetallePedidoFragment();

			Bundle bundle = new Bundle();
			bundle.putParcelable(DetallePedidoFragment.EXTRA_PEDIDO, pedido);
			detalleFragment.setArguments(bundle);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detallePedidoFl, detalleFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

		} else {

			Intent intent = new Intent(getActivity(),
					DetallePedidoActivity.class);
			intent.putExtra(DetallePedidoFragment.EXTRA_PEDIDO, pedido);
			startActivityForResult(intent, REQUEST_CODE_DETALLE);
		}

	}

	public void ocultarDetalle() {
		if (mDualPane) {
			DetallePedidoFragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = (DetallePedidoFragment) getFragmentManager()
					.findFragmentById(R.id.detallePedidoFl);

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
