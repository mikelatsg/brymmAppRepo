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
import com.brymm.brymmapp.local.bbdd.GestionMenu;
import com.brymm.brymmapp.local.bbdd.GestionTipoMenu;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.local.pojo.TipoMenu;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AnadirMenuFragment extends Fragment {

	public static final String EXTRA_MENU = "extraMenu";

	private Button btAceptar, btCancelar;
	private EditText etNombre, etPrecio;
	private RadioButton rbCarta, rbMenu;	
	private Spinner spTipoMenu;
	private int idMenu = 0;
	private boolean mDualPane;
	private boolean modificarMenu = false;
	private List<TipoMenu> tiposMenu;
	
	private OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			rbCarta.setChecked(false);
			rbMenu.setChecked(false);
			RadioButton rb = (RadioButton) v;
			rb.setChecked(true);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater
				.inflate(R.layout.fragment_anadir_menu, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		btAceptar = (Button) getActivity().findViewById(
				R.id.anadirMenuBtAceptar);
		btCancelar = (Button) getActivity().findViewById(
				R.id.anadirMenuBtCancelar);

		etNombre = (EditText) getActivity().findViewById(
				R.id.anadirMenuEtNombre);

		etPrecio = (EditText) getActivity().findViewById(
				R.id.anadirMenuEtPrecio);

		spTipoMenu = (Spinner) getActivity().findViewById(
				R.id.anadirMenuSpTipoMenu);

		rbCarta = (RadioButton) getActivity().findViewById(
				R.id.anadirMenuRbCarta);
		rbMenu = (RadioButton) getActivity()
				.findViewById(R.id.anadirMenuRbMenu);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaMenusFl);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		MenuLocal menu = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			if (bundle != null) {
				menu = bundle.getParcelable(EXTRA_MENU);
			}
		} else {
			Intent intent = getActivity().getIntent();
			menu = intent.getParcelableExtra(EXTRA_MENU);
		}

		if (menu != null) {
			this.modificarMenu = true;
			this.idMenu = menu.getIdMenu();
			etPrecio.setText(Float.toString(menu.getPrecio()));
			etNombre.setText(menu.getNombre());
			if (menu.isCarta()) {
				rbCarta.setChecked(true);
			} else {
				rbMenu.setChecked(true);
			}
		}

		// Se obtienen los tipos de plato
		GestionTipoMenu gestor = new GestionTipoMenu(getActivity());
		this.tiposMenu = gestor.obtenerTiposMenu();
		gestor.cerrarDatabase();

		List<String> tiposMenuString = new ArrayList<String>();
		int contador = 0;
		int mostrarIndice = 0;
		for (TipoMenu tipoMenu : this.tiposMenu) {
			tiposMenuString.add(tipoMenu.getDescripcion());
			if (menu != null) {
				if (menu.getTipoMenu().getIdTipoMenu() == tipoMenu
						.getIdTipoMenu()) {
					mostrarIndice = contador;
				}
			}
			contador++;
		}

		ArrayAdapter<String> tiposMenuAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				tiposMenuString);

		spTipoMenu.setAdapter(tiposMenuAdapter);
		spTipoMenu.setSelection(mostrarIndice);

		// Se envian los datos al servidor cuando se hace click en aceptar
		btAceptar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EnviarMenu ehl = new EnviarMenu();
				ehl.execute();
			}
		});

		btCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cerrarFormulario();

			}
		});
		
		rbCarta.setOnClickListener(ocl);
		rbMenu.setOnClickListener(ocl);

	}

	private void cerrarFormulario() {
		if (mDualPane) {
			Fragment anadirFragment = getFragmentManager().findFragmentById(
					R.id.detalleMenuFl);
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

	private JSONObject enviarMenu() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		if (modificarMenu) {

			url = new String(LoginActivity.SITE_URL
					+ "/api/menus/modificarMenu/format/json");

		} else {

			url = new String(LoginActivity.SITE_URL
					+ "/api/menus/nuevoMenu/format/json");
		}

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearMenuJson();

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

	private String crearMenuJson() {
		Gson gson = new Gson();

		MenuLocal menu = obtenerMenuFormulario();

		JsonElement jsonElement = gson.toJsonTree(menu);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
				LoginActivity.getLocal(getActivity()));

		jsonObject.add(GestionMenu.JSON_MENU, jsonElement);

		return jsonObject.toString();
	}

	private MenuLocal obtenerMenuFormulario() {
		MenuLocal menu = null;
		float precio = 0;
		if (!etPrecio.getText().toString().equals("")) {
			precio = Float.parseFloat(etPrecio.getText().toString());
		}

		menu = new MenuLocal(this.idMenu, etNombre.getText().toString(),
				precio, rbCarta.isChecked(), this.tiposMenu.get(spTipoMenu
						.getSelectedItemPosition()));

		return menu;
	}

	public class EnviarMenu extends AsyncTask<Void, Void, Resultado> {

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
				respJSON = enviarMenu();

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {

					// Si la operacion ha ido ok se guarda en la bbdd del movil
					MenuLocal menu = GestionMenu.menuJson2Menu(respJSON
							.getJSONObject(GestionMenu.JSON_MENU));

					GestionMenu gestor = new GestionMenu(getActivity());
					gestor.guardarMenu(menu);
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
