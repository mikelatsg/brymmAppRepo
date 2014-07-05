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
import com.brymm.brymmapp.local.AnadirDiaCierreActivity;
import com.brymm.brymmapp.local.HorariosActivity;
import com.brymm.brymmapp.local.bbdd.GestionDiaCierre;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.DiaCierre;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListaDiasCierreFragment extends Fragment implements Lista{

	private ListView lvDiasCierre;

	private Button btAnadir;

	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_dias_cierre,
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
		inflater.inflate(R.menu.context_menu_dia_cierre, menu);

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ArrayAdapter<DiaCierre> diaCierreAdapter = (ArrayAdapter<DiaCierre>) lvDiasCierre
				.getAdapter();
		DiaCierre diaCierre = diaCierreAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuDiaCierreBorrar:
			BorrarDiaCierre bdc = new BorrarDiaCierre();
			bdc.execute(diaCierre.getIdDiaCierre());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvDiasCierre = (ListView) getActivity().findViewById(
				R.id.listaDiasCierreLvLista);
		btAnadir = (Button) getActivity().findViewById(
				R.id.listaDiasCierreBtanadir);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.anadirHorariosFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mostrarNuevoDiaCierre();
			}
		});

		actualizarLista();

		registerForContextMenu(lvDiasCierre);

	}

	public void actualizarLista() {
		List<DiaCierre> diasCierre = new ArrayList<DiaCierre>();
		GestionDiaCierre gestor = new GestionDiaCierre(getActivity());
		diasCierre = gestor.obtenerDiasCierre();
		gestor.cerrarDatabase();

		List<String> diasCierreString = new ArrayList<String>();
		for (DiaCierre diaCierre : diasCierre) {
			diasCierreString.add(diaCierre.getFecha());
		}

		ArrayAdapter<DiaCierre> adapter = new ArrayAdapter<DiaCierre>(
				getActivity(), android.R.layout.simple_list_item_1, diasCierre);

		lvDiasCierre.setAdapter(adapter);
	}

	private void mostrarNuevoDiaCierre() {
		if (mDualPane) {
			AnadirDiaCierreFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirDiaCierreFragment();

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.anadirHorariosFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirDiaCierreActivity.class);
			startActivityForResult(intent,HorariosActivity.REQUEST_ANADIR_DIA_CIERRE);
		}

	}

	private JSONObject borrarDiaCierre(int idDiaCierre) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/horarios/borrarDiaCierre/idDiaCierre/"
					+ idDiaCierre + "/format/json";

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

	public class BorrarDiaCierre extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idDiaCierre;

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
			this.idDiaCierre = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarDiaCierre(this.idDiaCierre);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						GestionDiaCierre gestor = new GestionDiaCierre(
								getActivity());
						gestor.borrarDiaCierre(this.idDiaCierre);
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
			AnadirDiaCierreFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = (AnadirDiaCierreFragment) getFragmentManager()
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
