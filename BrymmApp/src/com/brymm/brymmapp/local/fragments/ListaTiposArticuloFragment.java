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
import com.brymm.brymmapp.local.AnadirTipoArticuloActivity;
import com.brymm.brymmapp.local.IngredientesActivity;
import com.brymm.brymmapp.local.adapters.TipoArticuloAdapter;
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticuloLocal;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListaTiposArticuloFragment extends Fragment implements Lista{

	private ListView lvTiposArticulo;

	private Button btAnadir;

	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_tipos_articulo,
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
		inflater.inflate(R.menu.context_menu_tipo_articulo, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		TipoArticuloAdapter tipoArticuloAdapter = (TipoArticuloAdapter) lvTiposArticulo
				.getAdapter();
		TipoArticuloLocal tipoArticuloLocal = tipoArticuloAdapter
				.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuTipoArticuloBorrar:
			BorrarTipoArticuloLocal btal = new BorrarTipoArticuloLocal();
			btal.execute(tipoArticuloLocal.getIdTipoArticuloLocal());
			return true;
		case R.id.contextMenuTipoArticuloModificar:
			mostrarModificarArticulo(tipoArticuloLocal);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvTiposArticulo = (ListView) getActivity().findViewById(
				R.id.listaTiposArticuloLvLista);
		btAnadir = (Button) getActivity().findViewById(
				R.id.listaTiposArticuloBtanadir);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(
				R.id.anadirArticulosFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mostrarNuevoArticulo();
			}
		});

		actualizarLista();

		registerForContextMenu(lvTiposArticulo);

	}

	public void actualizarLista() {
		List<TipoArticuloLocal> tiposArticulo = new ArrayList<TipoArticuloLocal>();
		GestionTipoArticuloLocal gtal = new GestionTipoArticuloLocal(
				getActivity());
		tiposArticulo = gtal.obtenerTiposArticulo();
		gtal.cerrarDatabase();

		TipoArticuloAdapter tipoArticuloAdapter = new TipoArticuloAdapter(
				getActivity(), R.layout.tipo_articulo_item, tiposArticulo);

		lvTiposArticulo.setAdapter(tipoArticuloAdapter);
	}

	private void mostrarNuevoArticulo() {
		if (mDualPane) {
			AnadirTipoArticuloFragment anadirFragment;
			// Check what fragment is currently shown, replace if needed.
			/*
			 * AnadirTipoArticuloFragment anadirFragment =
			 * (AnadirTipoArticuloFragment) getFragmentManager()
			 * .findFragmentById(R.id.anadirTiposArticulosFl); if
			 * (anadirFragment == null) {
			 */
			// Make new fragment to show this selection.
			anadirFragment = new AnadirTipoArticuloFragment();

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

	private void mostrarModificarArticulo(TipoArticuloLocal tipoArticuloLocal) {
		if (mDualPane) {
			AnadirTipoArticuloFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirTipoArticuloFragment();

			Bundle args = new Bundle();
			args.putParcelable(AnadirTipoArticuloFragment.EXTRA_TIPO_ARTICULO,
					tipoArticuloLocal);
			anadirFragment.setArguments(args);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.anadirTiposArticulosFl, anadirFragment);
			// ft.add(R.id.anadirTiposArticulosFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirTipoArticuloActivity.class);
			startActivity(intent);
		}

	}

	private JSONObject borrarTipoArticuloLocal(int idTipoArticuloLocal) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/articulos/borrarTipoArticuloLocal/idTipoArticuloLocal/"
					+ idTipoArticuloLocal + "/format/json";

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

	public class BorrarTipoArticuloLocal extends
			AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idTipoArticuloLocal;

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
			this.idTipoArticuloLocal = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarTipoArticuloLocal(this.idTipoArticuloLocal);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						// Se borra el tipo articulo de la bbdd del movil.
						GestionTipoArticuloLocal gtal = new GestionTipoArticuloLocal(
								getActivity());
						gtal.borrarTipoArticuloLocal(idTipoArticuloLocal);
						gtal.cerrarDatabase();

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
				Toast.makeText(getActivity(),
						resultado.getMensaje(), Toast.LENGTH_LONG).show();

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
