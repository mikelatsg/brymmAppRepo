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
import com.brymm.brymmapp.local.AnadirServicioActivity;
import com.brymm.brymmapp.local.AnadirTipoArticuloActivity;
import com.brymm.brymmapp.local.adapters.ArticuloAdapter;
import com.brymm.brymmapp.local.adapters.ServicioAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionServicioLocal;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.ServicioLocal;
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

public class ListaServiciosFragment extends Fragment {

	private ListView lvServicios;

	private Button btAnadir;
	private ContextMenu contextMenu;

	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_servicios,
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

		this.contextMenu = menu;

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu_servicio, menu);

	}			
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ServicioAdapter servicioAdapter = (ServicioAdapter) lvServicios
				.getAdapter();
		ServicioLocal servicio = servicioAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuServicioBorrar:
			BorrarServicio bs = new BorrarServicio();
			bs.execute(servicio.getIdServicio());
			return true;
		case R.id.contextMenuServicioModificar:
			mostrarModificarServicio(servicio);
			return true;
		case R.id.contextMenuServicioActivar:
			ActivarServicio as = new ActivarServicio();
			as.execute(servicio.getIdServicio(), 1);
			return true;
		case R.id.contextMenuServicioDesactivar:
			ActivarServicio ds = new ActivarServicio();
			ds.execute(servicio.getIdServicio(), 0);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvServicios = (ListView) getActivity().findViewById(
				R.id.listaServiciosLvLista);
		btAnadir = (Button) getActivity().findViewById(
				R.id.listaServiciosBtanadir);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.anadirServicioFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mostrarNuevoServicio();
			}
		});

		actualizarLista();

		registerForContextMenu(lvServicios);

	}

	public void actualizarLista() {
		List<ServicioLocal> servicios = new ArrayList<ServicioLocal>();
		GestionServicioLocal gsl = new GestionServicioLocal(getActivity());
		servicios = gsl.obtenerServiciosLocal();
		gsl.cerrarDatabase();

		ServicioAdapter servicioAdapter = new ServicioAdapter(getActivity(),
				R.layout.servicio_item, servicios);

		lvServicios.setAdapter(servicioAdapter);
	}

	private void mostrarNuevoServicio() {
		if (mDualPane) {
			AnadirServicioFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirServicioFragment();

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.anadirServicioFl, anadirFragment);
			// ft.add(R.id.anadirTiposArticulosFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirServicioActivity.class);
			startActivity(intent);
		}

	}

	private void mostrarModificarServicio(ServicioLocal servicio) {
		if (mDualPane) {
			AnadirServicioFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirServicioFragment();

			Bundle args = new Bundle();
			args.putParcelable(AnadirServicioFragment.EXTRA_SERVICIO, servicio);
			anadirFragment.setArguments(args);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.anadirServicioFl, anadirFragment);
			// ft.add(R.id.anadirTiposArticulosFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirServicioActivity.class);
			intent.putExtra(AnadirServicioFragment.EXTRA_SERVICIO, servicio);
			startActivity(intent);
		}

	}

	private JSONObject borrarServicio(int idServicio) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/servicios/borrarServicio/idServicio/" + idServicio
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

	public class BorrarServicio extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idServicio;

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
			this.idServicio = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarServicio(this.idServicio);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						GestionServicioLocal gsl = new GestionServicioLocal(
								getActivity());
						gsl.borrarServicioLocal(this.idServicio);
						gsl.cerrarDatabase();

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

	private JSONObject activarServicio(int idServicio, int activarServicio) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = "";

			if (activarServicio == 0) {
				url = LoginActivity.SITE_URL
						+ "/api/servicios/desactivarServicio/idServicio/"
						+ idServicio + "/format/json";
			} else {
				url = LoginActivity.SITE_URL
						+ "/api/servicios/activarServicio/idServicio/"
						+ idServicio + "/format/json";
			}

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

	public class ActivarServicio extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idServicio;
		int activarServicio = 0;

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
			this.idServicio = params[0];
			this.activarServicio = params[1];

			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = activarServicio(this.idServicio,
						this.activarServicio);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						// Si la operacion ha ido ok se guarda en la bbdd del
						// movil
						ServicioLocal servicio = GestionServicioLocal
								.servicioLocalJson2ServicioLocal(respJSON
										.getJSONObject(GestionServicioLocal.JSON_SERVICIO_LOCAL));

						GestionServicioLocal gsl = new GestionServicioLocal(
								getActivity());
						gsl.guardarServicioLocal(servicio);
						gsl.cerrarDatabase();

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

}
