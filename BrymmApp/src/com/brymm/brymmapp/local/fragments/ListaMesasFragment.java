package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.AnadirMesaActivity;
import com.brymm.brymmapp.local.ReservasActivity;
import com.brymm.brymmapp.local.adapters.MesaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionMesa;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.util.Resultado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListaMesasFragment extends Fragment implements Lista {

	private ListView lvMesas;

	private Button btAnadir;

	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_mesas, container,
				false);
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
		inflater.inflate(R.menu.context_menu_mesa, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		MesaAdapter mesaAdapter = (MesaAdapter) lvMesas.getAdapter();
		Mesa mesa = mesaAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuMesaBorrar:
			BorrarMesa bm = new BorrarMesa();
			bm.execute(mesa.getIdMesa());
			return true;

		case R.id.contextMenuMesaModificar:
			mostrarModificarMesa(mesa);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvMesas = (ListView) getActivity().findViewById(R.id.listaMesasLvLista);
		btAnadir = (Button) getActivity().findViewById(R.id.listaMesasBtanadir);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.detalleReservaFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mostrarNuevaMesa();
			}
		});

		actualizarLista();

		registerForContextMenu(lvMesas);

	}

	public void actualizarLista() {
		List<Mesa> mesas = new ArrayList<Mesa>();
		GestionMesa gestor = new GestionMesa(getActivity());
		mesas = gestor.obtenerMesas();
		gestor.cerrarDatabase();

		MesaAdapter mesaAdapter = new MesaAdapter(getActivity(),
				R.layout.mesa_item, mesas);

		lvMesas.setAdapter(mesaAdapter);
	}

	private void mostrarNuevaMesa() {
		if (mDualPane) {
			AnadirMesaFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirMesaFragment();

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleReservaFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(), AnadirMesaActivity.class);
			startActivityForResult(intent, ReservasActivity.REQUEST_ANADIR_MESA);
		}

	}
	
	private void mostrarModificarMesa(Mesa mesa) {
		if (mDualPane) {
			AnadirMesaFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirMesaFragment();

			Bundle bundle = new Bundle();
			bundle.putParcelable(AnadirMesaFragment.EXTRA_MESA, mesa);
			
			anadirFragment.setArguments(bundle);
			
			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleReservaFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(), AnadirMesaActivity.class);
			intent.putExtra(AnadirMesaFragment.EXTRA_MESA, mesa);
			startActivityForResult(intent, ReservasActivity.REQUEST_ANADIR_MESA);
		}

	}

	private JSONObject borrarMesa(int idMesa) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/reservas/borrarMesa/idMesa/" + idMesa
					+ "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			respStr = EntityUtils.toString(resp.getEntity());
			Log.d("res", respStr);
			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	public class BorrarMesa extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idMesa;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idMesa = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarMesa(this.idMesa);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						GestionMesa gestor = new GestionMesa(getActivity());
						gestor.borrarMesa(this.idMesa);
						gestor.cerrarDatabase();

					}

					res = new Resultado(
							respJSON.getInt(Resultado.JSON_OPERACION_OK),
							mensaje);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(Resultado resultado) {
			super.onPostExecute(resultado);
			progress.dismiss();

			if (resultado != null) {
				Toast.makeText(getActivity(), resultado.getMensaje(),
						Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					actualizarLista();
				}
			}

		}
	}

	public void ocultarDetalle() {
		if (mDualPane) {
			AnadirMesaFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = (AnadirMesaFragment) getFragmentManager()
					.findFragmentById(R.id.detalleReservaFl);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			if (anadirFragment != null) {

				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.remove(anadirFragment);

				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}
		}
	}

}
