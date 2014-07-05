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
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionDiaSemana;
import com.brymm.brymmapp.local.bbdd.GestionHorarioPedido;
import com.brymm.brymmapp.local.pojo.DiaSemana;
import com.brymm.brymmapp.local.pojo.HorarioPedido;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AnadirHorarioPedidoFragment extends Fragment {

	private Button btAceptar, btCancelar;
	private Spinner spDia;
	private EditText etHoraInicio, etHoraFin;
	private List<DiaSemana> diasSemana;
	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_anadir_horario_pedido,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirHorarioPedidoBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirHorarioPedidoBtCancelar);

		spDia = (Spinner) getActivity().findViewById(
				R.id.anadirHorarioPedidoSpDia);

		etHoraInicio = (EditText) getActivity().findViewById(
				R.id.anadirHorarioPedidoEtHoraInicio);

		etHoraFin = (EditText) getActivity().findViewById(
				R.id.anadirHorarioPedidoEtHoraFin);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaHorariosFl);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		GestionDiaSemana gestor = new GestionDiaSemana(getActivity());
		this.diasSemana = gestor.obtenerDiaSemana();
		gestor.cerrarDatabase();

		List<String> diasSemanaString = new ArrayList<String>();
		for (DiaSemana diaSemana : this.diasSemana) {
			diasSemanaString.add(diaSemana.getDia());
		}

		ArrayAdapter<String> diasSemanaAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				diasSemanaString);

		spDia.setAdapter(diasSemanaAdapter);
		spDia.setSelection(0);

		etHoraInicio.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialogoHora(etHoraInicio);
				}

			}
		});

		etHoraFin.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialogoHora(etHoraFin);
				}

			}
		});

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarHorarioPedido ehl = new EnviarHorarioPedido();
				ehl.execute();
			}
		});

		btCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cerrarFormulario();

			}
		});

	}

	private void cerrarFormulario() {
		if (mDualPane) {
			Fragment anadirFragment = getFragmentManager().findFragmentById(
					R.id.anadirHorariosFl);
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

	private JSONObject enviarHorarioPedido() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/horarios/nuevoHorarioPedido/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearHorarioPedidoJson();

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
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
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

	private String crearHorarioPedidoJson() {
		Gson gson = new Gson();

		HorarioPedido horarioPedido = obtenerHorarioPedidoFormulario();

		JsonElement jsonElement = gson.toJsonTree(horarioPedido);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionHorarioPedido.JSON_HORARIO_PEDIDO, jsonElement);

		return jsonObject.toString();
	}

	private HorarioPedido obtenerHorarioPedidoFormulario() {
		HorarioPedido horarioPedido = null;

		DiaSemana diaSemana = this.diasSemana.get(spDia
				.getSelectedItemPosition());

		horarioPedido = new HorarioPedido(0, diaSemana,
				etHoraInicio.getText().toString(), etHoraFin.getText()
						.toString());

		return horarioPedido;
	}

	public class EnviarHorarioPedido extends AsyncTask<Void, Void, Resultado> {

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
				respJSON = enviarHorarioPedido();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {

					// Si la operacion ha ido ok se guarda en la bbdd del movil
					HorarioPedido horarioPedido = GestionHorarioPedido
							.horarioPedidoJson2HorarioPedido(respJSON
									.getJSONObject(GestionHorarioPedido.JSON_HORARIO_PEDIDO));

					GestionHorarioPedido gestor = new GestionHorarioPedido(
							getActivity());
					gestor.guardarHorarioPedido(horarioPedido);
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
					if (mDualPane) {
						Fragment anadirFragment = getFragmentManager()
								.findFragmentById(R.id.anadirHorariosFl);
						// Se quita el fragment que contiene el formulario
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.remove(anadirFragment);

						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();

						ListaHorariosPedidoFragment listaFragment = (ListaHorariosPedidoFragment) getFragmentManager()
								.findFragmentById(R.id.listaHorariosFl);

						listaFragment.actualizarLista();
					} else {
						Intent intent = new Intent();
						getActivity().setResult(Activity.RESULT_OK, intent);
						getActivity().finish();
					}

				}
			}

			progress.dismiss();
		}

	}

}
