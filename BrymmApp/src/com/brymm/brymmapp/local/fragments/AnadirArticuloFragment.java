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
import com.brymm.brymmapp.local.bbdd.GestionIngrediente;
import com.brymm.brymmapp.local.bbdd.GestionTipoArticulo;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.TipoArticulo;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class AnadirArticuloFragment extends Fragment {

	public static final String EXTRA_ARTICULO = "extraArticulo";

	private Button btAceptar, btCancelar;
	private Spinner spTipoArticulo;
	private EditText etPrecio, etDescripcion, etNombre;
	private CheckBox cbValidoPedidos;
	private boolean modificarArticulo = false;
	private int idArticulo = 0;
	private List<TipoArticulo> tiposArticulo;
	private boolean mDualPane;
	private List<CheckBox> checkIngredientes = new ArrayList<CheckBox>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_anadir_articulo, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirArticuloLocalBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirArticuloLocalBtCancelar);

		spTipoArticulo = (Spinner) getActivity().findViewById(
				R.id.anadirArticuloLocalSpTipoArticulo);

		etPrecio = (EditText) getActivity().findViewById(
				R.id.anadirArticuloLocalEtPrecio);

		etDescripcion = (EditText) getActivity().findViewById(
				R.id.anadirArticuloLocalEtDescripcion);

		etNombre = (EditText) getActivity().findViewById(
				R.id.anadirArticuloLocalEtNombre);

		cbValidoPedidos = (CheckBox) getActivity().findViewById(
				R.id.anadirArticuloLocalCbValidoPedidos);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaArticulosFl);
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
		Articulo articulo = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			if (bundle != null) {
				articulo = bundle.getParcelable(EXTRA_ARTICULO);
			}
		} else {
			Intent intent = getActivity().getIntent();
			articulo = intent.getParcelableExtra(EXTRA_ARTICULO);
		}

		if (articulo != null) {
			this.modificarArticulo = true;
			this.idArticulo = articulo.getIdArticulo();
			cbValidoPedidos.setChecked(false);
			if (articulo.isValidoPedidos()) {
				cbValidoPedidos.setChecked(true);
			}
			etPrecio.setText(Float.toString(articulo.getPrecio()));
			etNombre.setText(articulo.getNombre());
			etDescripcion.setText(articulo.getDescripcion());
		}

		List<String> tiposArticuloString = new ArrayList<String>();
		int contador = 0;
		int mostrarIndice = 0;
		for (TipoArticulo tipoArticulo : this.tiposArticulo) {
			tiposArticuloString.add(tipoArticulo.getTipoArticulo());

			if (articulo != null) {
				if (articulo.getTipoArticulo().getIdTipoArticulo() == tipoArticulo
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

		// Se obtienen los ingredientes del local
		GestionIngrediente gi = new GestionIngrediente(getActivity());
		List<Ingrediente> ingredientes = gi.obtenerIngredientes();
		gi.cerrarDatabase();

		// Se obtienen el linea layout que contiene los ingredientes

		LinearLayout ll = (LinearLayout) getActivity().findViewById(
				R.id.anadirArticuloLocalLlIngredientes);

		/*
		 * Se añaden dinamicamente los ingredientes ,se añade un checkbox por
		 * cada ingrediente
		 */
		for (Ingrediente ingrediente : ingredientes) {
			CheckBox cb = new CheckBox(getActivity());
			cb.setText(ingrediente.getNombre());
			cb.setTag(ingrediente);

			// Si es modificar se checkean los ingredientes
			if (this.modificarArticulo) {
				for (Ingrediente ingrediente2 : articulo.getIngredientes()) {
					if (ingrediente.getIdIngrediente() == ingrediente2
							.getIdIngrediente()) {
						cb.setChecked(true);
					}
				}

			}
			ll.addView(cb);
			checkIngredientes.add(cb);
		}

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarArticulo ea = new EnviarArticulo();
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

	private JSONObject enviarArticulo() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		if (modificarArticulo) {
			url = new String(LoginActivity.SITE_URL
					+ "/api/articulos/modificarArticulo/format/json");
		} else {
			url = new String(LoginActivity.SITE_URL
					+ "/api/articulos/nuevoArticulo/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearArticuloJson();

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

	private String crearArticuloJson() {
		Gson gson = new Gson();

		Articulo articulo = obtenerArticuloFormulario();

		JsonElement jsonElementArticulo = gson.toJsonTree(articulo);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionArticulo.JSON_ARTICULO, jsonElementArticulo);

		return jsonObject.toString();
	}

	private Articulo obtenerArticuloFormulario() {
		Articulo articulo = null;

		TipoArticulo tipoArticulo = this.tiposArticulo.get(spTipoArticulo
				.getSelectedItemPosition());

		// Si no se da valor al campo precio se envia 0
		Float precio = (float) 0;
		if (!etPrecio.getText().toString().equals("")) {
			precio = Float.parseFloat(etPrecio.getText().toString());
		}

		// Se obtienen los ingredientes marcados
		List<Ingrediente> ingredientes = new ArrayList<Ingrediente>();
		for (CheckBox check : checkIngredientes) {
			if (check.isChecked()) {
				ingredientes.add((Ingrediente) check.getTag());
			}
		}

		articulo = new Articulo(this.idArticulo, tipoArticulo, etNombre
				.getText().toString(), etDescripcion.getText().toString(),
				precio, cbValidoPedidos.isChecked(), ingredientes);

		return articulo;
	}

	public class EnviarArticulo extends AsyncTask<Void, Void, Resultado> {

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
				respJSON = enviarArticulo();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {
					// Si la operacion ha ido ok se guarda en la bbdd del movil
					Articulo articulo = GestionArticulo
							.articuloJson2Articulo(respJSON
									.getJSONObject(GestionArticulo.JSON_ARTICULO));

					GestionArticulo ga = new GestionArticulo(getActivity());

					ga.guardarArticulo(articulo);
					ga.cerrarDatabase();

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
								.findFragmentById(R.id.anadirArticulosFl);
						// Se quita el fragment que contiene el formulario
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.remove(anadirFragment);

						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();

						ListaArticulosFragment listaFragment = (ListaArticulosFragment) getFragmentManager()
								.findFragmentById(R.id.listaArticulosFl);

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
