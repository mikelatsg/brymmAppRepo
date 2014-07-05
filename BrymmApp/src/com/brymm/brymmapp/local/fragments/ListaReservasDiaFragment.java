package com.brymm.brymmapp.local.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.AnadirReservaActivity;
import com.brymm.brymmapp.local.DetalleReservaActivity;
import com.brymm.brymmapp.local.ReservasActivity;
import com.brymm.brymmapp.local.adapters.ReservaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionDiaCierreReserva;
import com.brymm.brymmapp.local.bbdd.GestionHorarioLocal;
import com.brymm.brymmapp.local.bbdd.GestionMesa;
import com.brymm.brymmapp.local.bbdd.GestionReserva;
import com.brymm.brymmapp.local.bbdd.GestionTipoMenu;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.local.pojo.DiaCierreReserva;
import com.brymm.brymmapp.local.pojo.Reserva;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListaReservasDiaFragment extends Fragment implements ListaEstado {

	public static final String EXTRA_FECHA = "extraFecha";
	public static final int REQUEST_CODE_DETALLE = 1;

	private ListView lvReservas;

	private Button btCerrarReservas;
	private Spinner spTipoMenu;

	private boolean mDualPane;
	private String fecha;

	private List<String> tiposMenu;
	private List<Integer> tiposMenuId;

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
		View root = inflater.inflate(R.layout.fragment_lista_reservas_dia,
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
				R.id.listaReservasDiaLvLista);
		btCerrarReservas = (Button) getActivity().findViewById(
				R.id.listaReservasDiaBtcerrarReservas);
		spTipoMenu = (Spinner) getActivity().findViewById(
				R.id.listaReservasDiaSpTipoMenu);

		/* Se guarda si esta el fragmento de añadir */
		View detalleFrame = getActivity()
				.findViewById(R.id.detalleReservaDiaFl);
		mDualPane = detalleFrame != null
				&& detalleFrame.getVisibility() == View.VISIBLE;

		// Se recoge el estado pasado
		Intent intent = getActivity().getIntent();
		this.fecha = intent.getStringExtra(EXTRA_FECHA);

		lvReservas.setOnItemClickListener(oicl);

		String[] tiposMenuArray = getResources().getStringArray(
				R.array.tipoMenuArray);
		tiposMenu = new ArrayList<String>();
		tiposMenuId = new ArrayList<Integer>();
		for (int i = 0; i < tiposMenuArray.length; i++) {
			String[] stringTipoMenu = tiposMenuArray[i].split("#");
			tiposMenu.add(stringTipoMenu[0]);
			tiposMenuId.add(Integer.parseInt(stringTipoMenu[1]));
		}

		ArrayAdapter<String> tipoMenuAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, tiposMenu);

		spTipoMenu.setAdapter(tipoMenuAdapter);

		actualizarBtCerrarReserva();

		actualizarLista(this.fecha);

		// registerForContextMenu(lvReservas);

	}

	public void actualizarLista(String fecha) {
		List<Reserva> reservas = new ArrayList<Reserva>();
		GestionReserva gestor = new GestionReserva(getActivity());
		reservas = gestor.obtenerReservasDia(fecha);
		gestor.cerrarDatabase();

		ReservaAdapter reservaAdapter = new ReservaAdapter(getActivity(),
				R.layout.reserva_local_item, reservas);

		lvReservas.setAdapter(reservaAdapter);
	}

	private void actualizarBtCerrarReserva() {
		GestionDiaCierreReserva gestor = new GestionDiaCierreReserva(
				getActivity());
		final int idTipoMenu = tiposMenuId.get(spTipoMenu.getSelectedItemPosition());
		
		boolean reservasCerradas = gestor.comprobarReservasCerradas(this.fecha,
				idTipoMenu);
		gestor.cerrarDatabase();
		Resources res = getActivity().getResources();
		// Si las reservas estan cerradas se da la opcion de abrirlas y al
		// contrario
		if (reservasCerradas) {
			btCerrarReservas.setText(res
					.getString(R.string.reservas_dia_abrir_reservas));
			btCerrarReservas.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AbrirReservas ar = new AbrirReservas();
					ar.execute(idTipoMenu);

				}
			});
		} else {
			btCerrarReservas.setText(res
					.getString(R.string.reservas_dia_cerrar_reservas));
			btCerrarReservas.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CerrarReservas cr = new CerrarReservas();
					cr.execute(idTipoMenu);

				}
			});
		}
	}

	private void mostrarDetalleReserva(Reserva reserva) {
		if (mDualPane) {
			DetalleReservaFragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = new DetalleReservaFragment();

			Bundle args = new Bundle();
			args.putParcelable(DetalleReservaFragment.EXTRA_RESERVA, reserva);
			args.putBoolean(DetalleReservaFragment.EXTRA_RESERVA_DIA, true);
			detalleFragment.setArguments(args);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleReservaDiaFl, detalleFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					DetalleReservaActivity.class);
			intent.putExtra(DetalleReservaFragment.EXTRA_RESERVA, reserva);
			intent.putExtra(DetalleReservaFragment.EXTRA_RESERVA_DIA, true);
			startActivity(intent);
		}

	}

	public void ocultarDetalle() {
		if (mDualPane) {
			Fragment detalleFragment;

			detalleFragment = getFragmentManager().findFragmentById(
					R.id.detalleReservaDiaFl);

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

	private JSONObject cerrarReservasDia(int idTipoMenu) throws IOException,
			JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/reservas/cerrarReservasDia/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionDiaCierreReserva.JSON_FECHA,
					this.fecha);
			jsonObject.addProperty(GestionTipoMenu.JSON_ID_TIPO_MENU,
					idTipoMenu);
			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(jsonObject.toString());

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			// inputStream = httpResponse.getEntity().getContent();

			String respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.w("InputStream", e.getLocalizedMessage());

		}

		return respJSON;
	}

	private JSONObject abrirReservasDia(int idTipoMenu) throws IOException,
			JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/reservas/abrirReservasDia/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionDiaCierreReserva.JSON_FECHA,
					this.fecha);
			jsonObject.addProperty(GestionTipoMenu.JSON_ID_TIPO_MENU,
					idTipoMenu);
			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(jsonObject.toString());

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			// inputStream = httpResponse.getEntity().getContent();

			String respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.w("InputStream", e.getLocalizedMessage());

		}

		return respJSON;
	}

	public class AbrirReservas extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idTipoMenu;

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
			this.idTipoMenu = params[0];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = abrirReservasDia(this.idTipoMenu);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						// Se borra el dia de cierre
						GestionDiaCierreReserva gestor = new GestionDiaCierreReserva(
								getActivity());
						gestor.borrarDiaCierre(this.idTipoMenu, fecha);
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
					actualizarBtCerrarReserva();
				}
			}

		}
	}

	public class CerrarReservas extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idTipoMenu;

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
			this.idTipoMenu = params[0];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = cerrarReservasDia(this.idTipoMenu);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						DiaCierreReserva diaCierreReserva = GestionDiaCierreReserva
								.diaCierreReservaJson2DiaCierreReserva(respJSON
										.getJSONObject(GestionDiaCierreReserva.JSON_DIA_CIERRE_RESERVA));

						// Se borra el dia de cierre
						GestionDiaCierreReserva gestor = new GestionDiaCierreReserva(
								getActivity());
						gestor.guardarDiaCierreReserva(diaCierreReserva);
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
					actualizarBtCerrarReserva();
				}
			}

		}
	}

}
