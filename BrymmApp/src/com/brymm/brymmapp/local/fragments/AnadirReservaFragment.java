package com.brymm.brymmapp.local.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.adapters.MesaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionMesa;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.bbdd.GestionReserva;
import com.brymm.brymmapp.local.fragments.DetallePedidoFragment.ModificarEstadoPedido;
import com.brymm.brymmapp.local.fragments.DetalleReservaFragment.AsignarMesaReserva;
import com.brymm.brymmapp.local.fragments.DetalleReservaFragment.DesasignarMesaReserva;
import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.local.pojo.Pedido;
import com.brymm.brymmapp.local.pojo.Reserva;
import com.brymm.brymmapp.local.pojo.TipoMenu;
import com.brymm.brymmapp.local.pojo.Usuario;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AnadirReservaFragment extends Fragment {

	public static final String EXTRA_ESTADO = "extraEstado";

	private Button btAceptar, btCancelar;
	private EditText etFecha, etNumPersonas, etNombreDe, etObservaciones,
			etHora;
	private Spinner spTipoComida;
	private ListView lvMesasLibres, lvMesasAsignadas;
	private boolean mDualPane;
	private List<String> tiposMenu;
	private List<Integer> tiposMenuId;
	private String estado;
	private List<Mesa> mesasAsignadas = new ArrayList<Mesa>();
	private List<Mesa> mesasLibres = new ArrayList<Mesa>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_anadir_reserva, container,
				false);
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
		if (v.getId() == R.id.nuevaReservaLocalLvMesasLibres) {
			inflater.inflate(R.menu.context_menu_asignar_mesa, menu);
		} else {
			inflater.inflate(R.menu.context_menu_desasignar_mesa, menu);
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		MesaAdapter mesaAdapter;
		Mesa mesa;
		switch (item.getItemId()) {
		case R.id.contextMenuReservaAsignarMesa:
			mesaAdapter = (MesaAdapter) lvMesasLibres.getAdapter();
			mesa = mesaAdapter.getItem(info.position);
			asignarMesa(mesa);
			return true;

		case R.id.contextMenuReservaDesasignarMesa:
			mesaAdapter = (MesaAdapter) lvMesasAsignadas.getAdapter();
			mesa = mesaAdapter.getItem(info.position);
			desasignarMesa(mesa);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.nuevaReservaLocalBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.nuevaReservaLocalBtCancelar);

		etNombreDe = (EditText) getActivity().findViewById(
				R.id.nuevaReservaLocalEtNombreDe);

		etFecha = (EditText) getActivity().findViewById(
				R.id.nuevaReservaLocalEtFecha);

		etHora = (EditText) getActivity().findViewById(
				R.id.nuevaReservaLocalEtHora);

		etNumPersonas = (EditText) getActivity().findViewById(
				R.id.nuevaReservaLocalEtNumeroPersonas);

		etObservaciones = (EditText) getActivity().findViewById(
				R.id.nuevaReservaLocalEtObservaciones);

		spTipoComida = (Spinner) getActivity().findViewById(
				R.id.nuevaReservaLocalSpTipoMenu);

		lvMesasAsignadas = (ListView) getActivity().findViewById(
				R.id.nuevaReservaLocalLvMesasAsignadas);

		lvMesasLibres = (ListView) getActivity().findViewById(
				R.id.nuevaReservaLocalLvMesasLibres);

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

		spTipoComida.setAdapter(tipoMenuAdapter);

		// Se muestra el dialogo de fecha cuando llega el focus al edittext
		etFecha.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialogoFecha(etFecha);
				} else {
					// Cuando se cambia la fecha se muestran las mesas libres
					mostrarMesas();
				}

			}
		});

		// Se muestra el dialogo de hora cuando llega el focus al edittext
		etHora.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialogoHora(etHora);
				}

			}
		});

		// Cuando se cambia el tipo menus se muestran las mesas
		spTipoComida.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mostrarMesas();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaReservasFl);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		// Se obtiene el estado de la lista de reservas
		if (mDualPane) {
			Bundle bundle = getArguments();
			this.estado = bundle.getString(EXTRA_ESTADO);
		} else {
			Intent intent = getActivity().getIntent();
			this.estado = intent.getParcelableExtra(EXTRA_ESTADO);
		}

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarReserva er = new EnviarReserva();
				er.execute();
			}
		});

		btCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cerrarFormulario();

			}
		});

		// Se registran los menus contextuales.
		registerForContextMenu(lvMesasAsignadas);
		registerForContextMenu(lvMesasLibres);

	}

	private void mostrarMesas() {
		String fecha = etFecha.getText().toString();
		int idTipoMenu = tiposMenuId
				.get(spTipoComida.getSelectedItemPosition());
		this.mesasAsignadas = new ArrayList<Mesa>();

		if (!fecha.equals("")) {
			GestionReserva gestor = new GestionReserva(getActivity());
			this.mesasLibres = gestor.obtenerMesasLibres(fecha, idTipoMenu);
			gestor.cerrarDatabase();

			MesaAdapter mesaAdapter = new MesaAdapter(getActivity(),
					R.layout.mesa_item, this.mesasLibres);

			lvMesasLibres.setAdapter(mesaAdapter);
		}
	}

	private void asignarMesa(Mesa mesa) {
		// Se añade la mesa a las asignadas...
		this.mesasAsignadas.add(mesa);
		MesaAdapter mesaAdapter = new MesaAdapter(getActivity(),
				R.layout.mesa_item, mesasAsignadas);

		lvMesasAsignadas.setAdapter(mesaAdapter);

		// y se quita de las libres
		this.mesasLibres.remove(mesa);
		MesaAdapter mesasLibresAdapter = new MesaAdapter(getActivity(),
				R.layout.mesa_item, this.mesasLibres);

		lvMesasLibres.setAdapter(mesasLibresAdapter);
	}

	private void desasignarMesa(Mesa mesa) {
		// Se añade la mesa a las libres...
		this.mesasLibres.add(mesa);
		MesaAdapter mesaAdapter = new MesaAdapter(getActivity(),
				R.layout.mesa_item, this.mesasLibres);

		lvMesasLibres.setAdapter(mesaAdapter);

		// y se quita de las asignadas
		this.mesasAsignadas.remove(mesa);
		MesaAdapter mesasAsignadasAdapter = new MesaAdapter(getActivity(),
				R.layout.mesa_item, this.mesasAsignadas);

		lvMesasAsignadas.setAdapter(mesasAsignadasAdapter);
	}

	private void cerrarFormulario() {
		if (mDualPane) {
			Fragment anadirFragment = getFragmentManager().findFragmentById(
					R.id.detalleReservaFl);
			// Se quita el fragment que contiene el formulario
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.remove(anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_CANCELED, intent);
			getActivity().finish();
		}
	}

	private JSONObject enviarReserva() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/reservas/nuevaReservaLocal/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearReservaJson();

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(json);

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

	private String crearReservaJson() {
		Gson gson = new Gson();

		Reserva reserva = obtenerReservaFormulario();

		JsonElement jsonElement = gson.toJsonTree(reserva);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionReserva.JSON_RESERVA, jsonElement);

		return jsonObject.toString();
	}

	private Reserva obtenerReservaFormulario() {
		Reserva reserva = null;

		String fecha = etFecha.getText().toString();
		String hora = etHora.getText().toString();
		Usuario usuario = null;

		TipoMenu tipoMenu = new TipoMenu(tiposMenuId.get(spTipoComida
				.getSelectedItemPosition()), tiposMenu.get(spTipoComida
				.getSelectedItemPosition()));

		int numPersonas;
		try {
			numPersonas = Integer.parseInt(etNumPersonas.getText().toString());
		} catch (Exception e) {
			numPersonas = 0;
		}

		reserva = new Reserva(0, usuario, numPersonas, fecha, tipoMenu, hora,
				null, GestionReserva.ESTADO_ACEPTADA_LOCAL, "", etObservaciones
						.getText().toString(), etNombreDe.getText().toString(),
				this.mesasAsignadas);

		return reserva;
	}

	private void actualizarDatos(String idDetalle) {
		if (mDualPane) {
			ListaReservasFragment listaFragment;

			// Make new fragment to show this selection.
			listaFragment = (ListaReservasFragment) getFragmentManager()
					.findFragmentById(R.id.listaReservasFl);

			listaFragment.ocultarDetalle();
			listaFragment.actualizarLista(idDetalle);
		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}
	}

	private void dialogoHora(final EditText et) {
		final Dialog custom = new Dialog(getActivity());
		custom.setContentView(R.layout.dialog_hora);

		final TimePicker tp = (TimePicker) custom
				.findViewById(R.id.dialogHoraTpHora);

		Button bAceptar = (Button) custom
				.findViewById(R.id.dialogHoraBtAceptar);
		Button bCancelar = (Button) custom
				.findViewById(R.id.dialogHoraBtCancelar);
		custom.setTitle("Custom Dialog");
		bAceptar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				String minuto;
				if (tp.getCurrentMinute() < 10) {
					minuto = "0" + tp.getCurrentMinute();
				} else {
					minuto = Integer.toString(tp.getCurrentMinute());
				}
				custom.dismiss();

				et.setText(tp.getCurrentHour() + ":" + minuto);
				et.clearFocus();
			}

		});
		bCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				custom.dismiss();
				et.setText("00" + ":" + "00");
				et.clearFocus();
			}
		});
		custom.show();

	}

	private void dialogoFecha(final EditText et) {
		final Dialog custom = new Dialog(getActivity());
		custom.setContentView(R.layout.dialog_fecha);

		final DatePicker dp = (DatePicker) custom
				.findViewById(R.id.dialogFechaDpFecha);

		Button bAceptar = (Button) custom
				.findViewById(R.id.dialogFechaBtAceptar);
		Button bCancelar = (Button) custom
				.findViewById(R.id.dialogFechaBtCancelar);
		custom.setTitle("Custom Dialog");
		bAceptar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				String mes = Integer.toString(dp.getMonth() + 1);
				if (Integer.parseInt(mes) < 10) {
					mes = "0" + mes;
				}
				et.setText(dp.getYear() + "-" + mes + "-" + dp.getDayOfMonth());
				et.clearFocus();
				custom.dismiss();

			}

		});
		bCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				custom.dismiss();
				et.clearFocus();
			}
		});
		custom.show();

	}

	public class EnviarReserva extends AsyncTask<Void, Void, Resultado> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Void... params) {
			JSONObject respJSON = null;
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = enviarReserva();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {

					// Si la operacion ha ido ok se guarda en la bbdd del movil
					Reserva reserva = GestionReserva
							.reservaJson2Reserva(respJSON
									.getJSONObject(GestionReserva.JSON_RESERVA));

					GestionReserva gestor = new GestionReserva(getActivity());
					gestor.guardarReserva(reserva);
					gestor.cerrarDatabase();

				}

				res = new Resultado(
						respJSON.getInt(Resultado.JSON_OPERACION_OK), mensaje);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(Resultado resultado) {
			super.onPostExecute(resultado);

			if (resultado != null) {

				Toast.makeText(getActivity(), resultado.getMensaje(),
						Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					actualizarDatos(estado);
				}
			}

			progress.dismiss();
		}

	}

}
