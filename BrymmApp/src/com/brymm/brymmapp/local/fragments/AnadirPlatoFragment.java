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
import com.brymm.brymmapp.local.bbdd.GestionPlato;
import com.brymm.brymmapp.local.bbdd.GestionTipoPlato;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.Plato;
import com.brymm.brymmapp.local.pojo.TipoPlato;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AnadirPlatoFragment extends Fragment {

	public static final String EXTRA_PLATO = "extraPlato";

	private Button btAceptar, btCancelar;
	private EditText etNombre, etPrecio;
	private Spinner spTipoPlato;
	private int idPlato = 0;
	private boolean mDualPane;
	private boolean modificarPlato = false;
	private List<TipoPlato> tiposPlato;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_anadir_plato, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirPlatoBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirPlatoBtCancelar);

		etNombre = (EditText) getActivity().findViewById(
				R.id.anadirPlatoEtNombre);

		etPrecio = (EditText) getActivity().findViewById(
				R.id.anadirPlatoEtPrecio);

		spTipoPlato = (Spinner) getActivity().findViewById(
				R.id.anadirPlatoSpTipoPlato);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaMenusFl);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;
		
		Plato plato = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			if (bundle != null) {
				plato = bundle.getParcelable(EXTRA_PLATO);
			}
		} else {
			Intent intent = getActivity().getIntent();
			plato = intent.getParcelableExtra(EXTRA_PLATO);
		}

		if (plato != null) {
			this.modificarPlato = true;
			this.idPlato = plato.getIdPlato();
			etPrecio.setText(Float.toString(plato.getPrecio()));
			etNombre.setText(plato.getNombre());
		}else{
			Log.w("plato" , "nulo");
		}

		// Se obtienen los tipos de plato
		GestionTipoPlato gestor = new GestionTipoPlato(getActivity());
		this.tiposPlato = gestor.obtenerTiposPlato();
		gestor.cerrarDatabase();

		List<String> tiposPlatoString = new ArrayList<String>();
		int contador = 0;
		int mostrarIndice = 0;
		for (TipoPlato tipoPlato : this.tiposPlato) {
			tiposPlatoString.add(tipoPlato.getDescripcion());
			if (plato != null) {
				if (plato.getTipoPlato().getIdTipoPlato() == tipoPlato
						.getIdTipoPlato()) {
					mostrarIndice = contador;
				}
			}
			contador++;
		}

		ArrayAdapter<String> tiposPlatoAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				tiposPlatoString);

		spTipoPlato.setAdapter(tiposPlatoAdapter);
		spTipoPlato.setSelection(mostrarIndice);	

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarPlato ehl = new EnviarPlato();
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

		if (modificarPlato) {

			url = new String(LoginActivity.SITE_URL
					+ "/api/menus/modificarPlato/format/json");

		} else {

			url = new String(LoginActivity.SITE_URL
					+ "/api/menus/nuevoPlato/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearPlatoJson();

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

	private String crearPlatoJson() {
		Gson gson = new Gson();

		Plato plato = obtenerPlatoFormulario();

		JsonElement jsonElement = gson.toJsonTree(plato);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionPlato.JSON_PLATO, jsonElement);

		return jsonObject.toString();
	}

	private Plato obtenerPlatoFormulario() {
		Plato plato = null;
		float precio = 0;
		if (!etPrecio.getText().toString().equals("")){
			precio = Float.parseFloat(etPrecio.getText().toString());
		}

		plato = new Plato(this.idPlato, this.tiposPlato.get(spTipoPlato
				.getSelectedItemPosition()), etNombre.getText().toString(),
				precio
				);

		return plato;
	}

	public class EnviarPlato extends AsyncTask<Void, Void, Resultado> {

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
					Plato plato = GestionPlato.platoJson2Plato(respJSON
							.getJSONObject(GestionPlato.JSON_PLATO));

					GestionPlato gestor = new GestionPlato(getActivity());
					gestor.guardarPlato(plato);
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
								.findFragmentById(R.id.detalleMenuFl);
						// Se quita el fragment que contiene el formulario
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.remove(anadirFragment);

						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();

						Lista listaFragment = (Lista) getFragmentManager()
								.findFragmentById(R.id.listaMenusFl);

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
