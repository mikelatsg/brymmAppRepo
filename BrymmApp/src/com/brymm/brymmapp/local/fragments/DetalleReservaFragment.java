package com.brymm.brymmapp.local.fragments;

import java.io.IOException;
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
import com.brymm.brymmapp.local.adapters.ArticuloCantidadAdapter;
import com.brymm.brymmapp.local.adapters.MesaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionMesa;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.bbdd.GestionReserva;
import com.brymm.brymmapp.local.fragments.ListaMesasFragment.BorrarMesa;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.local.pojo.Pedido;
import com.brymm.brymmapp.local.pojo.Reserva;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class DetalleReservaFragment extends Fragment {

	public static final String EXTRA_RESERVA = "extraReserva";
	public static final String EXTRA_RESERVA_DIA = "extraReservaDia";

	private ListView lvMesasAsignadas, lvMesasLibres;
	private Button btAceptar, btRechazar, btCancelar;
	private TextView tvFecha, tvEstado, tvHoraInicio, tvIdReserva,
			tvObservaciones, tvNumeroPersonas, tvTipoMenu, tvUsuario, tvMotivo;

	private boolean mDualPane;
	private boolean reservaDia = false;

	private OnClickListener oclRechazar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Reserva reserva = (Reserva) v.getTag();

			dialogoRechazar(reserva);

		}
	};

	private OnClickListener oclAceptar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Reserva reserva = (Reserva) v.getTag();

			ModificarEstadoReserva mer = new ModificarEstadoReserva();
			/*
			 * Si se trata de la lista de reservas de un dia se pasa la fecha
			 * para poder actualizar la lista
			 */

			if (reservaDia) {
				mer.execute(Integer.toString(reserva.getIdReserva()),
						GestionReserva.ESTADO_ACEPTADA_LOCAL,
						reserva.getFecha());
			} else {
				mer.execute(Integer.toString(reserva.getIdReserva()),
						GestionReserva.ESTADO_ACEPTADA_LOCAL,
						reserva.getEstado());
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_detalle_reserva,
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
		if (v.getId() == R.id.detalleReservaLvMesasLibres) {
			inflater.inflate(R.menu.context_menu_asignar_mesa, menu);
		} else {
			inflater.inflate(R.menu.context_menu_desasignar_mesa, menu);
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		Reserva reserva = (Reserva) lvMesasLibres.getTag();
		MesaAdapter mesaAdapter;
		Mesa mesa;
		switch (item.getItemId()) {
		case R.id.contextMenuReservaAsignarMesa:
			mesaAdapter = (MesaAdapter) lvMesasLibres.getAdapter();
			mesa = mesaAdapter.getItem(info.position);
			AsignarMesaReserva amr = new AsignarMesaReserva();
			amr.execute(reserva.getIdReserva(), mesa.getIdMesa());
			return true;

		case R.id.contextMenuReservaDesasignarMesa:
			mesaAdapter = (MesaAdapter) lvMesasAsignadas.getAdapter();
			mesa = mesaAdapter.getItem(info.position);
			DesasignarMesaReserva dmr = new DesasignarMesaReserva();
			dmr.execute(reserva.getIdReserva(), mesa.getIdMesa());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvMesasAsignadas = (ListView) getActivity().findViewById(
				R.id.detalleReservaLvMesasAsignadas);
		lvMesasLibres = (ListView) getActivity().findViewById(
				R.id.detalleReservaLvMesasLibres);
		btAceptar = (Button) getActivity().findViewById(
				R.id.detalleReservaBtAceptar);
		btRechazar = (Button) getActivity().findViewById(
				R.id.detalleReservaBtRechazar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.detalleReservaBtCancelar);

		tvIdReserva = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvIdReserva);
		tvUsuario = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvUsuario);
		tvFecha = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvFecha);
		tvEstado = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvEstado);
		tvHoraInicio = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvHoraInicio);
		tvMotivo = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvMotivo);
		tvNumeroPersonas = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvNumeroPersonas);
		tvObservaciones = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvObservaciones);
		tvTipoMenu = (TextView) getActivity().findViewById(
				R.id.detalleReservaTvTipoMenu);

		/* Se guarda si esta el frame layout de lista */
		View listaFragment = getActivity().findViewById(R.id.listaReservasFl);

		// Si es nulo se comprueba si esta la lista de reservas dia
		if (listaFragment == null) {
			listaFragment = getActivity().findViewById(R.id.listaReservasDiaFr);
		}

		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		Reserva reserva = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			reserva = bundle.getParcelable(EXTRA_RESERVA);
			this.reservaDia = bundle.getBoolean(EXTRA_RESERVA_DIA, false);
		} else {
			Intent intent = getActivity().getIntent();
			reserva = intent.getParcelableExtra(EXTRA_RESERVA);
			this.reservaDia = intent.getBooleanExtra(EXTRA_RESERVA_DIA, false);
		}

		tvIdReserva.setText(Integer.toString(reserva.getIdReserva()));
		if (reserva.getUsuario() != null) {
			tvUsuario.setText(reserva.getUsuario().getNombre() + " "
					+ reserva.getUsuario().getApellido());
		} else {
			tvUsuario.setText(reserva.getNombreEmisor());
		}

		tvFecha.setText(reserva.getFecha());
		tvHoraInicio.setText(reserva.getHoraInicio());
		tvEstado.setText(reserva.getEstado());
		tvTipoMenu.setText(reserva.getTipoMenu().getDescripcion());

		// Si no hay motivo se oculta el campo
		if (reserva.getMotivo().equals("")
				|| reserva.getMotivo().toLowerCase().equals("null")) {
			tvMotivo.setVisibility(View.GONE);
		} else {
			tvMotivo.setText(reserva.getMotivo());
		}

		// Si no hay observaciones se oculta el campo
		if (reserva.getObservaciones().equals("")
				|| reserva.getObservaciones().toLowerCase().equals("null")) {
			tvObservaciones.setVisibility(View.GONE);
		} else {
			tvObservaciones.setText(reserva.getObservaciones());
		}

		tvNumeroPersonas.setText(Integer.toString(reserva.getNumeroPersonas()));

		actualizarMesas(reserva);

		// Se ocultan los botones dependiendo del estado de la reserva
		if (reserva.getEstado().equals(GestionReserva.ESTADO_ACEPTADA_LOCAL)) {
			btAceptar.setVisibility(View.GONE);
		} else if (reserva.getEstado().equals(
				GestionReserva.ESTADO_RECHAZADA_LOCAL)) {
			btAceptar.setVisibility(View.GONE);
			btRechazar.setVisibility(View.GONE);
		}

		btAceptar.setTag(reserva);
		btRechazar.setTag(reserva);
		btAceptar.setOnClickListener(oclAceptar);
		btRechazar.setOnClickListener(oclRechazar);

		btCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ocultarDetalle();
			}
		});

		lvMesasLibres.setTag(reserva);
		registerForContextMenu(lvMesasLibres);

		lvMesasAsignadas.setTag(reserva);
		registerForContextMenu(lvMesasAsignadas);

	}

	private void actualizarMesas(Reserva reserva) {
		Resources res = getActivity().getResources();
		MesaAdapter mesaAdapter = new MesaAdapter(getActivity(),
				R.layout.mesa_item, reserva.getMesas());

		lvMesasAsignadas.setAdapter(mesaAdapter);

		GestionReserva gestor = new GestionReserva(getActivity());
		List<Mesa> mesasLibres = gestor.obtenerMesasLibres(reserva.getFecha(),
				reserva.getTipoMenu().getIdTipoMenu());
		gestor.cerrarDatabase();

		mesaAdapter = new MesaAdapter(getActivity(), R.layout.mesa_item,
				mesasLibres);

		lvMesasLibres.setAdapter(mesaAdapter);
	}

	private void ocultarDetalle() {
		if (mDualPane) {
			// ListaReservasFragment listaFragment;
			ListaEstado listaFragment;

			if (this.reservaDia) {
				listaFragment = (ListaEstado) getFragmentManager()
						.findFragmentById(R.id.listaReservasDiaFr);
			} else {
				// Make new fragment to show this selection.
				listaFragment = (ListaEstado) getFragmentManager()
						.findFragmentById(R.id.listaReservasFl);
			}

			listaFragment.ocultarDetalle();
		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_CANCELED, intent);
			getActivity().finish();
		}
	}

	private void actualizarDatos(String idDetalle) {
		if (mDualPane) {

			if (this.reservaDia) {
				ListaReservasDiaFragment listaFragment;
				listaFragment = (ListaReservasDiaFragment) getFragmentManager()
						.findFragmentById(R.id.listaReservasDiaFr);

				listaFragment.ocultarDetalle();
				listaFragment.actualizarLista(idDetalle);
			} else {
				// Make new fragment to show this selection.
				ListaReservasFragment listaFragment;
				listaFragment = (ListaReservasFragment) getFragmentManager()
						.findFragmentById(R.id.listaReservasFl);

				listaFragment.ocultarDetalle();
				listaFragment.actualizarLista(idDetalle);
			}

		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}
	}

	@SuppressLint("NewApi")
	private void dialogoRechazar(final Reserva reserva) {
		final Dialog custom = new Dialog(getActivity());
		custom.setContentView(R.layout.dialog_rechazar_reserva);

		final EditText etMotivo = (EditText) custom
				.findViewById(R.id.dialogRechazarReservaEtMotivo);

		Button bAceptar = (Button) custom
				.findViewById(R.id.dialogRechazarReservaBtAceptar);
		Button bCancelar = (Button) custom
				.findViewById(R.id.dialogRechazarReservaBtCancelar);
		custom.setTitle("Custom Dialog");
		bAceptar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				custom.dismiss();

				ModificarEstadoReserva mep = new ModificarEstadoReserva();

				if (reservaDia) {
					mep.execute(Integer.toString(reserva.getIdReserva()),
							GestionPedido.ESTADO_RECHAZADO, reserva.getFecha(),
							etMotivo.getText().toString());
				} else {
					mep.execute(Integer.toString(reserva.getIdReserva()),
							GestionPedido.ESTADO_RECHAZADO,
							reserva.getEstado(), etMotivo.getText().toString());
				}

			}

		});
		bCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				custom.dismiss();
			}
		});
		custom.show();

	}

	private JSONObject aceptarReserva(int idReserva) throws IOException,
			JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/reservas/aceptarReserva/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionReserva.JSON_ID_RESERVA, idReserva);

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
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private JSONObject rechazarReserva(int idReserva, String idEstado,
			String motivo) throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/reservas/rechazarReserva/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionReserva.JSON_ID_RESERVA, idReserva);
			jsonObject.addProperty(GestionReserva.JSON_MOTIVO, motivo);

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
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private JSONObject asignarMesa(int idReserva, int idMesa)
			throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/reservas/asignarMesaReserva/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionReserva.JSON_ID_RESERVA, idReserva);
			jsonObject.addProperty(GestionMesa.JSON_ID_MESA, idMesa);

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
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private JSONObject desasignarMesa(int idReserva, int idMesa)
			throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/reservas/desasignarMesaReserva/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionReserva.JSON_ID_RESERVA, idReserva);
			jsonObject.addProperty(GestionMesa.JSON_ID_MESA, idMesa);

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

			String respStr = EntityUtils.toString(httpResponse.getEntity());
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	public class ModificarEstadoReserva extends
			AsyncTask<String, Void, Resultado> {

		ProgressDialog progress;
		int idReserva;
		String idEstado;
		private String estadoActual;

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
			this.idReserva = Integer.parseInt(params[0]);
			this.idEstado = params[1];
			this.estadoActual = params[2];
			String mensaje = "";
			Resultado res = null;
			try {
				if (this.idEstado.equals(GestionReserva.ESTADO_ACEPTADA_LOCAL)) {
					respJSON = aceptarReserva(this.idReserva);
				} else {
					respJSON = rechazarReserva(this.idReserva, this.idEstado,
							params[3]);
				}

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						Reserva reserva = GestionReserva
								.reservaJson2Reserva(respJSON
										.getJSONObject(GestionReserva.JSON_RESERVA));

						// Se modifica la reserva en la bbdd del movil.
						GestionReserva gestor = new GestionReserva(
								getActivity());
						gestor.guardarReserva(reserva);
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
					actualizarDatos(this.estadoActual);
				}
			}

		}
	}

	public class AsignarMesaReserva extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idReserva;
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
			this.idReserva = params[0];
			this.idMesa = params[1];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = asignarMesa(this.idReserva, this.idMesa);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						Reserva reserva = GestionReserva
								.reservaJson2Reserva(respJSON
										.getJSONObject(GestionReserva.JSON_RESERVA));

						// Se modifica la reserva en la bbdd del movil.
						GestionReserva gestor = new GestionReserva(
								getActivity());
						gestor.guardarReserva(reserva);
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
					// Se actualiza la reserva en la vista.
					GestionReserva gestor = new GestionReserva(getActivity());
					Reserva reserva = gestor.obtenerReserva(this.idReserva);
					gestor.cerrarDatabase();
					actualizarMesas(reserva);
				}
			}

		}
	}

	public class DesasignarMesaReserva extends
			AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idReserva;
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
			this.idReserva = params[0];
			this.idMesa = params[1];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = desasignarMesa(this.idReserva, this.idMesa);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						Reserva reserva = GestionReserva
								.reservaJson2Reserva(respJSON
										.getJSONObject(GestionReserva.JSON_RESERVA));

						// Se modifica la reserva en la bbdd del movil.
						GestionReserva gestor = new GestionReserva(
								getActivity());
						gestor.guardarReserva(reserva);
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
					// Se actualiza la reserva en la vista.
					GestionReserva gestor = new GestionReserva(getActivity());
					Reserva reserva = gestor.obtenerReserva(this.idReserva);
					gestor.cerrarDatabase();
					actualizarMesas(reserva);
				}
			}

		}
	}

}
