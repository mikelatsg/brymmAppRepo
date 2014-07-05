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
import com.brymm.brymmapp.local.AnadirReservaActivity;
import com.brymm.brymmapp.local.DetalleReservaActivity;
import com.brymm.brymmapp.local.ReservasActivity;
import com.brymm.brymmapp.local.adapters.ReservaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionHorarioLocal;
import com.brymm.brymmapp.local.bbdd.GestionReserva;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.local.pojo.Reserva;
import com.brymm.brymmapp.util.Resultado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListaReservasFragment extends Fragment implements ListaEstado {

	public static final String EXTRA_ID_ESTADO = "extraIdEstado";
	public static final int REQUEST_CODE_DETALLE = 1;

	private ListView lvReservas;

	private Button btAnadir;

	private boolean mDualPane;
	private String estado;

	private OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long arg3) {
			Reserva reserva = (Reserva) adapter.getItemAtPosition(position);

			mostrarDetalleReserva(reserva);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_reservas,
				container, false);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {
		lvReservas = (ListView) getActivity().findViewById(
				R.id.listaReservasLvLista);
		btAnadir = (Button) getActivity().findViewById(
				R.id.listaReservasBtanadir);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.detalleReservaFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		// Se recoge el estado pasado
		Bundle bundle = getArguments();
		this.estado = bundle.getString(EXTRA_ID_ESTADO);

		lvReservas.setOnItemClickListener(oicl);

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mostrarNuevaReserva();
			}
		});

		actualizarLista(this.estado);

		// registerForContextMenu(lvReservas);

	}

	public void actualizarLista(String estado) {
		List<Reserva> reservas = new ArrayList<Reserva>();
		GestionReserva gestor = new GestionReserva(getActivity());
		reservas = gestor.obtenerReservas(estado);
		gestor.cerrarDatabase();

		ReservaAdapter reservaAdapter = new ReservaAdapter(getActivity(),
				R.layout.reserva_local_item, reservas);

		lvReservas.setAdapter(reservaAdapter);
	}

	private void mostrarNuevaReserva() {
		if (mDualPane) {
			AnadirReservaFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirReservaFragment();

			Bundle args = new Bundle();
			args.putString(AnadirReservaFragment.EXTRA_ESTADO, this.estado);
			anadirFragment.setArguments(args);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleReservaFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirReservaActivity.class);
			intent.putExtra(AnadirReservaFragment.EXTRA_ESTADO, this.estado);
			startActivityForResult(intent,
					ReservasActivity.REQUEST_ANADIR_RESERVA);
		}

	}

	private void mostrarDetalleReserva(Reserva reserva) {
		if (mDualPane) {
			DetalleReservaFragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = new DetalleReservaFragment();

			Bundle args = new Bundle();
			args.putParcelable(DetalleReservaFragment.EXTRA_RESERVA, reserva);
			detalleFragment.setArguments(args);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleReservaFl, detalleFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					DetalleReservaActivity.class);
			intent.putExtra(DetalleReservaFragment.EXTRA_RESERVA, reserva);
			startActivity(intent);
		}

	}

	private JSONObject borrarHorarioLocal(int idHorarioLocal) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/horarios/borrarHorarioLocal/idHorarioLocal/"
					+ idHorarioLocal + "/format/json";

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

	public class BorrarHorarioLocal extends AsyncTask<String, Void, Resultado> {

		ProgressDialog progress;
		int idHorarioLocal;
		String estado;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(String... params) {
			JSONObject respJSON = null;
			this.idHorarioLocal = Integer.parseInt(params[0]);
			this.estado = params[1];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarHorarioLocal(this.idHorarioLocal);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						GestionHorarioLocal gestor = new GestionHorarioLocal(
								getActivity());
						gestor.borrarHorarioLocal(this.idHorarioLocal);
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
					actualizarLista(estado);
				}
			}

		}
	}

	public void ocultarDetalle() {
		if (mDualPane) {
			Fragment detalleFragment;

			// Make new fragment to show this selection.
			/*detalleFragment = (DetalleReservaFragment) getFragmentManager()
					.findFragmentById(R.id.detalleReservaFl);*/
			
			detalleFragment = getFragmentManager()
					.findFragmentById(R.id.detalleReservaFl);

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
