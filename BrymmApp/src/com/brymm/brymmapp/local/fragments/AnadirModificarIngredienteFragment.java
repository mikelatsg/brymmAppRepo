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
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AnadirModificarIngredienteFragment extends Fragment {

	public final static String EXTRA_INGREDIENTE = "extraIngrediente";

	private Button btAceptar, btCancelar;
	private EditText etNombre, etDescripcion, etPrecio;
	private boolean modificarIngrediente = false;
	private int idIngrediente = 0;
	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(
				R.layout.fragment_anadir_modificar_ingrediente, container,
				false);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {
		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirModificarIngredienteBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirModificarIngredienteBtCancelar);
		etNombre = (EditText) getActivity().findViewById(
				R.id.anadirModificarIngredienteEtNombre);
		etDescripcion = (EditText) getActivity().findViewById(
				R.id.anadirModificarIngredienteEtDescripcion);
		etPrecio = (EditText) getActivity().findViewById(
				R.id.anadirModificarIngredienteEtPrecio);
		
		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaArticulosFl);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		Ingrediente ingrediente = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			if (bundle != null) {
				ingrediente = bundle.getParcelable(EXTRA_INGREDIENTE);
			}
		} else {
			Intent intent = getActivity().getIntent();
			ingrediente = intent.getParcelableExtra(EXTRA_INGREDIENTE);
		}

		/*
		 * Modificar direccion, se guarda en la variable de clase idDireccion el
		 * id de la direcci�n para poder modificar en el servidor.
		 */

		if (ingrediente != null) {
			this.modificarIngrediente = true;
			this.idIngrediente = ingrediente.getIdIngrediente();
			etNombre.setText(ingrediente.getNombre());
			etDescripcion.setText(ingrediente.getDescripcion());
			etPrecio.setText(Float.toString(ingrediente.getPrecio()));
		}		

		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NuevoIngrediente ni = new NuevoIngrediente();
				ni.execute();

			}
		});

		btCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cerrarFormulario();

			}
		});

	}

	private JSONObject enviarIngrediente() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		if (modificarIngrediente) {
			url = new String(LoginActivity.SITE_URL
					+ "/api/ingredientes/modificarIngrediente/format/json");
		} else {
			url = new String(LoginActivity.SITE_URL
					+ "/api/ingredientes/nuevoIngrediente/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearIngredienteJson();

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

	private String crearIngredienteJson() {
		Gson gson = new Gson();

		Ingrediente ingrediente = obtenerIngredienteFormulario();

		JsonElement jsonElementIngrediente = gson.toJsonTree(ingrediente);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionIngrediente.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionIngrediente.JSON_INGREDIENTE,
				jsonElementIngrediente);

		return jsonObject.toString();
	}

	private Ingrediente obtenerIngredienteFormulario() {
		Ingrediente ingrediente = null;

		ingrediente = new Ingrediente(this.idIngrediente, etNombre.getText()
				.toString(), etDescripcion.getText().toString(),
				Float.parseFloat(etPrecio.getText().toString()));

		return ingrediente;
	}

	private void cerrarFormulario() {
		if (mDualPane) {
			Fragment anadirFragment = getFragmentManager().findFragmentById(
					R.id.anadirArticulosFl);
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

	public class NuevoIngrediente extends AsyncTask<Void, Void, Resultado> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(
					AnadirModificarIngredienteFragment.this.getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Void... params) {
			JSONObject respJSON = null;
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = enviarIngrediente();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {
					Ingrediente ingrediente = GestionIngrediente
							.ingredienteJson2Ingrediente(respJSON
									.getJSONObject(GestionIngrediente.JSON_INGREDIENTE));

					GestionIngrediente gi = new GestionIngrediente(
							AnadirModificarIngredienteFragment.this
									.getActivity());

					gi.guardarIngrediente(ingrediente);
					gi.cerrarDatabase();

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

				Toast.makeText(
						AnadirModificarIngredienteFragment.this.getActivity(),
						resultado.getMensaje(), Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					if (mDualPane) {
						Fragment anadirFragment = getFragmentManager()
								.findFragmentById(R.id.anadirArticulosFl);
						// Se quita el fragment que contiene el formulario
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.remove(anadirFragment);

						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();

						Lista listaFragment = (Lista) getFragmentManager()
								.findFragmentById(R.id.listaArticulosFl);

						listaFragment.actualizarLista();
					} else {
						Intent intent = new Intent();
						getActivity().setResult(Activity.RESULT_OK, intent);
						getActivity().finish();
					}
				}
			}

		}

	}

}
