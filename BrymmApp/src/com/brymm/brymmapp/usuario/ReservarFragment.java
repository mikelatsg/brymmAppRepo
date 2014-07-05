package com.brymm.brymmapp.usuario;

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
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.usuario.bbdd.GestionDireccion;
import com.brymm.brymmapp.usuario.bbdd.GestionReservaUsuario;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.Local;
import com.brymm.brymmapp.usuario.pojo.ReservaUsuario;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class ReservarFragment extends Fragment {

	private EditText etFecha, etNumeroPersonas, etObservaciones;
	private Button btAceptar;
	private Spinner spTipoMenu;
	private List<String> tiposMenu;
	private List<Integer> tiposMenuId;

	private OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			NuevaReserva nr = new NuevaReserva();
			nr.execute();

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_reservar, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {
		etFecha = (EditText) getActivity().findViewById(R.id.reservarEtFecha);
		etObservaciones = (EditText) getActivity().findViewById(
				R.id.reservarEtObservaciones);
		etNumeroPersonas = (EditText) getActivity().findViewById(
				R.id.reservarEtNumeroPersonas);
		btAceptar = (Button) getActivity().findViewById(R.id.reservarBtAceptar);
		spTipoMenu = (Spinner) getActivity().findViewById(
				R.id.reservarSpTipoMenu);

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
		btAceptar.setOnClickListener(ocl);

		etFecha.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialogoFecha();
				}

			}
		});
	}

	@SuppressLint("NewApi")
	private void dialogoFecha() {
		final Dialog custom = new Dialog(getActivity());
		custom.setContentView(R.layout.dialog_fecha_reserva);

		final TimePicker tp = (TimePicker) custom
				.findViewById(R.id.dialogFechaReservaTpHora);
		final DatePicker dp = (DatePicker) custom
				.findViewById(R.id.dialogFechaReservaDpFecha);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			dp.setCalendarViewShown(false);
		}

		Button bAceptar = (Button) custom
				.findViewById(R.id.dialogFechaReservaBtAceptar);
		Button bCancelar = (Button) custom
				.findViewById(R.id.dialogFechaReservaBtCancelar);
		custom.setTitle("Custom Dialog");
		bAceptar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				int mes = dp.getMonth() + 1;
				String minuto;
				if (tp.getCurrentMinute() < 10) {
					minuto = "0" + Integer.toString(tp.getCurrentMinute());
				} else {
					minuto = Integer.toString(tp.getCurrentMinute());
				}
				etFecha.setText(dp.getYear() + "/" + mes + "/"
						+ dp.getDayOfMonth() + " " + tp.getCurrentHour() + ":"
						+ minuto);
				etFecha.clearFocus();
				custom.dismiss();
			}

		});
		bCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				etFecha.clearFocus();
				custom.dismiss();
			}
		});
		custom.show();

	}

	private JSONObject enviarReserva() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/reservas/nuevaReserva/format/json");

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
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private String crearReservaJson() {
		Gson gson = new Gson();

		// Se a�ade el idLocal y el idUsuario
		Intent intent = getActivity().getIntent();
		Local local = intent
				.getParcelableExtra(MostrarLocalActivity.EXTRA_LOCAL);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionReservaUsuario.JSON_FECHA, etFecha
				.getText().toString());
		jsonObject.addProperty(GestionReservaUsuario.JSON_OBSERVACIONES,
				etObservaciones.getText().toString());
		jsonObject.addProperty(GestionReservaUsuario.JSON_NUMERO_PERSONAS,
				etNumeroPersonas.getText().toString());
		jsonObject.addProperty(GestionReservaUsuario.JSON_ID_LOCAL,
				local.getIdLocal());

		jsonObject.addProperty(GestionReservaUsuario.JSON_TIPO_MENU,
				tiposMenuId.get(spTipoMenu.getSelectedItemPosition()));
		jsonObject.addProperty(GestionReservaUsuario.JSON_ID_USUARIO,
				LoginActivity.getUsuario(getActivity()));

		return jsonObject.toString();
	}

	public class NuevaReserva extends AsyncTask<Void, Void, Resultado> {

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
					ReservaUsuario reserva = GestionReservaUsuario
							.reservaJson2ReservaUsuario(respJSON
									.getJSONObject(GestionReservaUsuario.JSON_RESERVA));

					GestionReservaUsuario gru = new GestionReservaUsuario(
							getActivity());
					gru.guardarReserva(reserva);
					gru.cerrarDatabase();
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
			progress.dismiss();

			if (resultado != null) {

				Toast.makeText(getActivity(), resultado.getMensaje(),
						Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					MenuUsuario.irUltimasReservas(getActivity());
				}
			}

		}

	}

}
