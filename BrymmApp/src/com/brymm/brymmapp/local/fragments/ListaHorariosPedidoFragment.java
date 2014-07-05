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
import com.brymm.brymmapp.local.AnadirHorarioPedidoActivity;
import com.brymm.brymmapp.local.HorariosActivity;
import com.brymm.brymmapp.local.adapters.HorarioPedidoAdapter;
import com.brymm.brymmapp.local.bbdd.GestionHorarioPedido;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.HorarioPedido;
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

public class ListaHorariosPedidoFragment extends Fragment implements Lista {

	private ListView lvHorariosPedido;

	private Button btAnadir;

	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_horarios_pedido,
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
		inflater.inflate(R.menu.context_menu_horario_pedido, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		HorarioPedidoAdapter horarioPedidoAdapter = (HorarioPedidoAdapter) lvHorariosPedido
				.getAdapter();
		HorarioPedido horarioPedido = horarioPedidoAdapter
				.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuHorarioPedidoBorrar:
			BorrarHorarioPedido bhp = new BorrarHorarioPedido();
			bhp.execute(horarioPedido.getIdHorarioPedido());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvHorariosPedido = (ListView) getActivity().findViewById(
				R.id.listaHorariosPedidoLvLista);
		btAnadir = (Button) getActivity().findViewById(
				R.id.listaHorariosPedidoBtanadir);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.anadirHorariosFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mostrarNuevoHorarioPedido();
			}
		});

		actualizarLista();

		registerForContextMenu(lvHorariosPedido);

	}

	public void actualizarLista() {
		List<HorarioPedido> horariosPedido = new ArrayList<HorarioPedido>();
		GestionHorarioPedido gestor = new GestionHorarioPedido(getActivity());
		horariosPedido = gestor.obtenerHorariosPedido();
		gestor.cerrarDatabase();

		HorarioPedidoAdapter horarioPedidoAdapter = new HorarioPedidoAdapter(
				getActivity(), R.layout.horario_pedido_item, horariosPedido);

		lvHorariosPedido.setAdapter(horarioPedidoAdapter);
	}

	private void mostrarNuevoHorarioPedido() {
		if (mDualPane) {
			AnadirHorarioPedidoFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirHorarioPedidoFragment();

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.anadirHorariosFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirHorarioPedidoActivity.class);
			startActivityForResult(intent,
					HorariosActivity.REQUEST_ANADIR_HORARIO_PEDIDO);
		}

	}

	private JSONObject borrarHorarioPedido(int idHorarioPedido) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/horarios/borrarHorarioPedido/idHorarioPedido/"
					+ idHorarioPedido + "/format/json";

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

	public class BorrarHorarioPedido extends
			AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idHorarioPedido;

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
			this.idHorarioPedido = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarHorarioPedido(this.idHorarioPedido);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						GestionHorarioPedido gestor = new GestionHorarioPedido(
								getActivity());
						gestor.borrarHorarioPedido(this.idHorarioPedido);
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
			AnadirHorarioPedidoFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = (AnadirHorarioPedidoFragment) getFragmentManager()
					.findFragmentById(R.id.anadirHorariosFl);

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
