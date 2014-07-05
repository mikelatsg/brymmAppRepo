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
import com.brymm.brymmapp.local.bbdd.GestionMesa;
import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AnadirMesaFragment extends Fragment {

	public static final String EXTRA_MESA = "extraMesa";

	private Button btAceptar, btCancelar;
	private EditText etNombre, etCapacidad;
	private int idMesa = 0;
	private boolean mDualPane;
	private boolean modificarMesa = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater
				.inflate(R.layout.fragment_anadir_mesa, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirMesaBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirMesaBtCancelar);

		etNombre = (EditText) getActivity().findViewById(
				R.id.anadirMesaEtNombre);

		etCapacidad = (EditText) getActivity().findViewById(
				R.id.anadirMesaEtCapacidad);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaReservasFl);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		Mesa mesa = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			if (bundle != null) {
				mesa = bundle.getParcelable(EXTRA_MESA);
			}
		} else {
			Intent intent = getActivity().getIntent();
			mesa = intent.getParcelableExtra(EXTRA_MESA);
		}

		if (mesa != null) {
			this.modificarMesa = true;
			this.idMesa = mesa.getIdMesa();
			etCapacidad.setText(Integer.toString(mesa.getCapacidad()));
			etNombre.setText(mesa.getNombre());
		}

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarMesa ehl = new EnviarMesa();
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

	private JSONObject enviarMesa() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		if (modificarMesa) {

			url = new String(LoginActivity.SITE_URL
					+ "/api/reservas/modificarMesa/format/json");
			
		} else {

			url = new String(LoginActivity.SITE_URL
					+ "/api/reservas/nuevaMesa/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearMesaJson();

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

	private String crearMesaJson() {
		Gson gson = new Gson();

		Mesa mesa = obtenerMesaFormulario();

		JsonElement jsonElement = gson.toJsonTree(mesa);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionMesa.JSON_MESA, jsonElement);

		return jsonObject.toString();
	}

	private Mesa obtenerMesaFormulario() {
		Mesa mesa = null;

		mesa = new Mesa(this.idMesa, Integer.parseInt(etCapacidad.getText()
				.toString()), etNombre.getText().toString());

		return mesa;
	}

	public class EnviarMesa extends AsyncTask<Void, Void, Resultado> {

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
				respJSON = enviarMesa();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {

					// Si la operacion ha ido ok se guarda en la bbdd del movil
					Mesa mesa = GestionMesa.mesaJson2Mesa(respJSON
							.getJSONObject(GestionMesa.JSON_MESA));

					GestionMesa gestor = new GestionMesa(getActivity());
					gestor.guardarMesa(mesa);
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
								.findFragmentById(R.id.detalleReservaFl);
						// Se quita el fragment que contiene el formulario
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.remove(anadirFragment);

						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();

						ListaMesasFragment listaFragment = (ListaMesasFragment) getFragmentManager()
								.findFragmentById(R.id.listaReservasFl);

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
