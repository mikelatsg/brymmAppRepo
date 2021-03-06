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
import com.brymm.brymmapp.local.AnadirArticuloLocalActivity;
import com.brymm.brymmapp.local.AnadirTipoArticuloActivity;
import com.brymm.brymmapp.local.adapters.ArticuloAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.Articulo;
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

public class ListaArticulosFragment extends Fragment implements Lista{

	private ListView lvArticulos;

	private Button btAnadir;

	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_articulos,
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

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ArticuloAdapter articuloAdapter = (ArticuloAdapter) lvArticulos
				.getAdapter();
		Articulo articulo = articuloAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuArticuloBorrar:
			BorrarArticulo ba = new BorrarArticulo();
			ba.execute(articulo.getIdArticulo());
			return true;
		case R.id.contextMenuArticuloModificar:
			mostrarModificarArticulo(articulo);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvArticulos = (ListView) getActivity().findViewById(
				R.id.listaArticulosLvLista);
		btAnadir = (Button) getActivity().findViewById(
				R.id.listaArticulosBtanadir);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.anadirArticulosFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mostrarNuevoArticulo();
			}
		});

		actualizarLista();

		registerForContextMenu(lvArticulos);

	}

	public void actualizarLista() {
		List<Articulo> articulos = new ArrayList<Articulo>();
		GestionArticulo ga = new GestionArticulo(getActivity());
		articulos = ga.obtenerArticulos();
		ga.cerrarDatabase();

		ArticuloAdapter articuloAdapter = new ArticuloAdapter(getActivity(),
				R.layout.articulo_item, articulos);

		lvArticulos.setAdapter(articuloAdapter);
	}

	private void mostrarNuevoArticulo() {
		if (mDualPane) {
			AnadirArticuloFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirArticuloFragment();

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.anadirArticulosFl, anadirFragment);
			// ft.add(R.id.anadirTiposArticulosFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirTipoArticuloActivity.class);
			startActivity(intent);
		}

	}

	private void mostrarModificarArticulo(Articulo articulo) {
		if (mDualPane) {
			AnadirArticuloFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirArticuloFragment();

			Bundle args = new Bundle();
			args.putParcelable(AnadirArticuloFragment.EXTRA_ARTICULO, articulo);
			anadirFragment.setArguments(args);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.anadirArticulosFl, anadirFragment);
			// ft.add(R.id.anadirTiposArticulosFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirArticuloLocalActivity.class);
			intent.putExtra(AnadirArticuloFragment.EXTRA_ARTICULO,articulo);
			startActivity(intent);
		}

	}

	private JSONObject borrarArticulo(int idArticulo, int idLocal) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/articulos/borrarArticulo/idArticulo/" + idArticulo
					+ "/idLocal/" + idLocal
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

	public class BorrarArticulo extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idArticulo;
		int idLocal;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			this.idLocal = LoginActivity.getLocal(getActivity());
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idArticulo = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarArticulo(this.idArticulo, this.idLocal);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						// Se borra el articulo de la bbdd del movil.
						GestionArticulo ga = new GestionArticulo(getActivity());
						ga.borrarArticulo(this.idArticulo);
						ga.cerrarDatabase();

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

	@Override
	public void ocultarDetalle() {
		if (mDualPane) {
			Fragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = (Fragment) getFragmentManager()
					.findFragmentById(R.id.anadirArticulosFl);

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
