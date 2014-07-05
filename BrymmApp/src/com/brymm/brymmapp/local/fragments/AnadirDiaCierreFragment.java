package com.brymm.brymmapp.local.fragments;

import java.io.IOException;

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
import com.brymm.brymmapp.local.bbdd.GestionDiaCierre;
import com.brymm.brymmapp.local.pojo.DiaCierre;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AnadirDiaCierreFragment extends Fragment {

	private Button btAceptar, btCancelar;
	private EditText etFecha;
	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_anadir_dia_cierre, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirDiaCierreBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirDiaCierreBtCancelar);

		etFecha = (EditText) getActivity().findViewById(
				R.id.anadirDiaCierreEtFecha);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaHorariosFl);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		etFecha.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialogoFecha(etFecha);
				}

			}
		});

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarDiaCierre edc = new EnviarDiaCierre();
				edc.execute();
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

	private JSONObject enviarDiaCierre() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/horarios/nuevoDiaCierre/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearDiaCierreJson();

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

				int mes = dp.getMonth() + 1;
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

	private String crearDiaCierreJson() {
		Gson gson = new Gson();

		DiaCierre diaCierre = obtenerDiaCierreFormulario();

		JsonElement jsonElement = gson.toJsonTree(diaCierre);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionDiaCierre.JSON_DIA_CIERRE, jsonElement);

		return jsonObject.toString();
	}

	private DiaCierre obtenerDiaCierreFormulario() {
		DiaCierre diaCierre = null;

		diaCierre = new DiaCierre(0, etFecha.getText().toString());

		return diaCierre;
	}

	public class EnviarDiaCierre extends AsyncTask<Void, Void, Resultado> {

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
				respJSON = enviarDiaCierre();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {

					// Si la operacion ha ido ok se guarda en la bbdd del movil
					DiaCierre diaCierre = GestionDiaCierre
							.diaCierreJson2DiaCierre(respJSON
									.getJSONObject(GestionDiaCierre.JSON_DIA_CIERRE));

					GestionDiaCierre gestor = new GestionDiaCierre(
							getActivity());
					gestor.guardarDiaCierre(diaCierre);
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

						ListaDiasCierreFragment listaFragment = (ListaDiasCierreFragment) getFragmentManager()
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
