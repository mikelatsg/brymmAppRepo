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
import com.brymm.brymmapp.local.bbdd.GestionTipoArticulo;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticuloLocal;
import com.brymm.brymmapp.local.pojo.TipoArticulo;
import com.brymm.brymmapp.local.pojo.TipoArticuloLocal;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AnadirTipoArticuloFragment extends Fragment {

	public static final String EXTRA_TIPO_ARTICULO = "extraTipoArticulo";

	private Button btAceptar, btCancelar;
	private Spinner spTipoArticulo;
	private EditText etPrecio;
	private CheckBox cbPersonalizar;
	private boolean modificarTipoArticulo = false;
	private int idTipoArticuloLocal = 0;
	private List<TipoArticulo> tiposArticulo;
	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_anadir_tipo_articulo,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirTipoArticuloBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirTipoArticuloBtCancelar);

		spTipoArticulo = (Spinner) getActivity().findViewById(
				R.id.anadirTipoArticuloSpTipoArticulo);

		etPrecio = (EditText) getActivity().findViewById(
				R.id.anadirTipoArticuloEtPrecio);

		cbPersonalizar = (CheckBox) getActivity().findViewById(
				R.id.anadirTipoArticuloCbPersonalizar);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(
				R.id.listaTiposArticulosFr);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		GestionTipoArticulo gta = new GestionTipoArticulo(getActivity());
		this.tiposArticulo = gta.obtenerTiposArticulo();
		gta.cerrarDatabase();

		/*
		 * Modificar tipo articulo, se guarda en la variable de clase
		 * idTipoArticulo el id del tipo articulo para poder modificar en el
		 * servidor.
		 */
		TipoArticuloLocal tipoArticuloLocal = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			if (bundle != null) {
				tipoArticuloLocal = bundle.getParcelable(EXTRA_TIPO_ARTICULO);
			}
		} else {
			Intent intent = getActivity().getIntent();
			tipoArticuloLocal = intent.getParcelableExtra(EXTRA_TIPO_ARTICULO);
		}

		if (tipoArticuloLocal != null) {
			this.modificarTipoArticulo = true;
			this.idTipoArticuloLocal = tipoArticuloLocal
					.getIdTipoArticuloLocal();
			cbPersonalizar.setChecked(false);
			if (tipoArticuloLocal.isPersonalizar()) {
				cbPersonalizar.setChecked(true);
			}
			etPrecio.setText(Float.toString(tipoArticuloLocal.getPrecio()));
			spTipoArticulo.setClickable(false);
		}

		List<String> tiposArticuloString = new ArrayList<String>();
		int contador = 0;
		int mostrarIndice = 0;
		for (TipoArticulo tipoArticulo : this.tiposArticulo) {
			tiposArticuloString.add(tipoArticulo.getTipoArticulo());

			if (tipoArticuloLocal != null) {
				if (tipoArticuloLocal.getIdTipoArticulo() == tipoArticulo
						.getIdTipoArticulo()) {
					mostrarIndice = contador;
				}
			}
			contador++;
		}

		ArrayAdapter<String> tiposArticuloAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				tiposArticuloString);

		spTipoArticulo.setAdapter(tiposArticuloAdapter);
		spTipoArticulo.setSelection(mostrarIndice);

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarTipoArticuloLocal etal = new EnviarTipoArticuloLocal();
				etal.execute();
			}
		});
		
		btCancelar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cerrarFormulario();
				
			}
		});

	}
	
	private void cerrarFormulario(){
		if (mDualPane) {
			Fragment anadirFragment = getFragmentManager()
					.findFragmentById(R.id.anadirTiposArticulosFl);
			// Se quita el fragment que contiene el formulario
			FragmentTransaction ft = getFragmentManager()
					.beginTransaction();
			ft.remove(anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

			ListaTiposArticuloFragment listaFragment = (ListaTiposArticuloFragment) getFragmentManager()
					.findFragmentById(R.id.listaTiposArticulosFr);

		} else {
			Intent intent = new Intent();
			getActivity()
					.setResult(getActivity().RESULT_CANCELED, intent);
			getActivity().finish();
		}
	}

	private JSONObject enviarTipoArticulo() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		if (modificarTipoArticulo) {
			url = new String(LoginActivity.SITE_URL
					+ "/api/articulos/modificarTipoArticulo/format/json");
		} else {
			url = new String(LoginActivity.SITE_URL
					+ "/api/articulos/nuevoTipoArticulo/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearTipoArticuloLocalJson();

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

	private String crearTipoArticuloLocalJson() {
		Gson gson = new Gson();

		TipoArticuloLocal tipoArticuloLocal = obtenerTipoArticuloLocalFormulario();

		JsonElement jsonElementTipoArticuloLocal = gson
				.toJsonTree(tipoArticuloLocal);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionTipoArticuloLocal.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionTipoArticuloLocal.JSON_TIPO_ARTICULO_LOCAL,
				jsonElementTipoArticuloLocal);

		return jsonObject.toString();
	}

	private TipoArticuloLocal obtenerTipoArticuloLocalFormulario() {
		TipoArticuloLocal tipoArticuloLocal = null;

		TipoArticulo tipoArticulo = this.tiposArticulo.get(spTipoArticulo
				.getSelectedItemPosition());

		// Si no se da valor al campo precio se envia 0
		Float precio = (float) 0;
		if (!etPrecio.getText().toString().equals("")) {
			precio = Float.parseFloat(etPrecio.getText().toString());
		}

		tipoArticuloLocal = new TipoArticuloLocal(
				tipoArticulo.getIdTipoArticulo(),
				tipoArticulo.getTipoArticulo(), tipoArticulo.getDescripcion(),
				precio, cbPersonalizar.isChecked(), idTipoArticuloLocal);

		return tipoArticuloLocal;
	}

	public class EnviarTipoArticuloLocal extends
			AsyncTask<Void, Void, Resultado> {

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
				respJSON = enviarTipoArticulo();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {

					TipoArticuloLocal tipoArticuloLocal = GestionTipoArticuloLocal
							.tipoArticuloLocalJson2TipoArticuloLocal(respJSON
									.getJSONObject(GestionTipoArticuloLocal.JSON_TIPO_ARTICULO_LOCAL));

					GestionTipoArticuloLocal gta = new GestionTipoArticuloLocal(
							getActivity());

					gta.guardarTipoArticuloLocal(tipoArticuloLocal);
					gta.cerrarDatabase();

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
								.findFragmentById(R.id.anadirTiposArticulosFl);
						// Se quita el fragment que contiene el formulario
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.remove(anadirFragment);

						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();

						ListaTiposArticuloFragment listaFragment = (ListaTiposArticuloFragment) getFragmentManager()
								.findFragmentById(R.id.listaTiposArticulosFr);

						listaFragment.actualizarLista();
					} else {
						Intent intent = new Intent();
						getActivity()
								.setResult(getActivity().RESULT_OK, intent);
						getActivity().finish();
					}

				}
			}

			progress.dismiss();
		}

	}

}
