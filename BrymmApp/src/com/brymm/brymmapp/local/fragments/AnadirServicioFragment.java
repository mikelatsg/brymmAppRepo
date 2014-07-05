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
import com.brymm.brymmapp.local.bbdd.GestionServicioLocal;
import com.brymm.brymmapp.local.bbdd.GestionTipoServicio;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.ServicioLocal;
import com.brymm.brymmapp.local.pojo.TipoServicio;
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

public class AnadirServicioFragment extends Fragment {

	public static final String EXTRA_SERVICIO = "extraServicio";

	private Button btAceptar, btCancelar;
	private Spinner spTipoServicio;
	private EditText etPrecio, etImporteMinimo;
	private boolean modificarServicio = false;
	private int idServicio = 0;
	private boolean activo = false;
	private List<TipoServicio> tiposServicio;
	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_anadir_servicio, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirServicioBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirServicioBtCancelar);

		spTipoServicio = (Spinner) getActivity().findViewById(
				R.id.anadirServicioSpTipoServicio);

		etPrecio = (EditText) getActivity().findViewById(
				R.id.anadirServicioEtPrecio);

		etImporteMinimo = (EditText) getActivity().findViewById(
				R.id.anadirServicioEtImporteMinimo);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaServiciosFr);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		GestionTipoServicio gts = new GestionTipoServicio(getActivity());
		this.tiposServicio = gts.obtenerTiposServicio();
		gts.cerrarDatabase();

		/*
		 * Modificar servicio, se guarda en la variable de clase idServicio el
		 * id del servicio para poder modificar en el servidor.
		 */
		ServicioLocal servicio = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			if (bundle != null) {
				servicio = bundle.getParcelable(EXTRA_SERVICIO);
			}
		} else {
			Intent intent = getActivity().getIntent();
			servicio = intent.getParcelableExtra(EXTRA_SERVICIO);
		}

		if (servicio != null) {
			this.modificarServicio = true;
			this.idServicio = servicio.getIdServicio();
			this.activo = servicio.isActivo();

			etPrecio.setText(Float.toString(servicio.getPrecio()));
			etImporteMinimo
					.setText(Float.toString(servicio.getImporteMinimo()));
		}

		List<String> tiposServicioString = new ArrayList<String>();
		int contador = 0;
		int mostrarIndice = 0;
		for (TipoServicio tipoServicio : this.tiposServicio) {
			tiposServicioString.add(tipoServicio.getServicio());

			if (servicio != null) {
				if (servicio.getTipoServicio().getIdTipoServicio() == tipoServicio
						.getIdTipoServicio()) {
					mostrarIndice = contador;
				}
			}
			contador++;
		}

		ArrayAdapter<String> tiposServicioAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				tiposServicioString);

		spTipoServicio.setAdapter(tiposServicioAdapter);
		spTipoServicio.setSelection(mostrarIndice);

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarServicio ea = new EnviarServicio();
				ea.execute();
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
					R.id.anadirServicioFl);
			// Se quita el fragment que contiene el formulario
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.remove(anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

			/*
			 * ListaArticulosFragment listaFragment = (ListaArticulosFragment)
			 * getFragmentManager() .findFragmentById(R.id.listaArticulosFr);
			 */

		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_CANCELED, intent);
			getActivity().finish();
		}
	}

	private JSONObject enviarServicio() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		if (modificarServicio) {
			url = new String(LoginActivity.SITE_URL
					+ "/api/servicios/modificarServicio/format/json");
		} else {
			url = new String(LoginActivity.SITE_URL
					+ "/api/servicios/nuevoServicio/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearServicioJson();

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

	private String crearServicioJson() {
		Gson gson = new Gson();

		ServicioLocal servicio = obtenerServicioFormulario();

		JsonElement jsonElementArticulo = gson.toJsonTree(servicio);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionServicioLocal.JSON_SERVICIO_LOCAL,
				jsonElementArticulo);

		return jsonObject.toString();
	}

	private ServicioLocal obtenerServicioFormulario() {
		ServicioLocal servicio = null;

		TipoServicio tipoServicio = this.tiposServicio.get(spTipoServicio
				.getSelectedItemPosition());

		// Si no se da valor al campo precio se envia 0
		Float precio = (float) 0;
		if (!etPrecio.getText().toString().equals("")) {
			precio = Float.parseFloat(etPrecio.getText().toString());
		}

		// Si no se da valor al campo importe minimo se envia 0
		Float importeMinimo = (float) 0;
		if (!etImporteMinimo.getText().toString().equals("")) {
			importeMinimo = Float.parseFloat(etImporteMinimo.getText()
					.toString());
		}

		servicio = new ServicioLocal(this.idServicio, tipoServicio,
				this.activo, importeMinimo, precio);

		return servicio;
	}

	public class EnviarServicio extends AsyncTask<Void, Void, Resultado> {

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
				respJSON = enviarServicio();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {
					// Si la operacion ha ido ok se guarda en la bbdd del movil
					ServicioLocal servicio = GestionServicioLocal.servicioLocalJson2ServicioLocal(respJSON
									.getJSONObject(GestionServicioLocal.JSON_SERVICIO_LOCAL));				

					GestionServicioLocal gsl = new GestionServicioLocal(getActivity());
					gsl.guardarServicioLocal(servicio);
					gsl.cerrarDatabase();

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
								.findFragmentById(R.id.anadirServicioFl);
						// Se quita el fragment que contiene el formulario
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.remove(anadirFragment);

						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();

						ListaServiciosFragment listaFragment = (ListaServiciosFragment) getFragmentManager()
								.findFragmentById(R.id.listaServiciosFr);

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
