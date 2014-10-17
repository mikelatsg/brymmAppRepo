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
import com.brymm.brymmapp.local.adapters.DetalleComandaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionComanda;
import com.brymm.brymmapp.local.interfaces.Detalle;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class DetalleComandaFragment extends Fragment implements Detalle {

	public static final String EXTRA_COMANDA = "extraComanda";

	private ListView lvDetalles;
	private Button btCerrarComanda, btTerminarCocina, btCancelarComanda;
	private TextView tvIdComanda, tvCamarero, tvPrecio, tvEstado,
			tvObservaciones, tvMesa;
	private Comanda comanda;

	private boolean mDualPane;

	private OnClickListener oclCancelarComanda = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Comanda comanda = (Comanda) v.getTag();

			CancelarComanda cc = new CancelarComanda();
			cc.execute(comanda.getIdComanda());
		}
	};

	private OnClickListener oclTerminarCocina = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Comanda comanda = (Comanda) v.getTag();

			TerminarComandaCocina tcc = new TerminarComandaCocina();
			tcc.execute(comanda.getIdComanda());
		}
	};

	private OnClickListener oclCerrarCamarero = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Comanda comanda = (Comanda) v.getTag();

			CerrarComandaCamarero ccc = new CerrarComandaCamarero();
			ccc.execute(comanda.getIdComanda());
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_detalle_comanda,
				container, false);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu_detalle_comanda, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		DetalleComandaAdapter detalleComandaAdapter = (DetalleComandaAdapter) lvDetalles
				.getAdapter();

		DetalleComanda detalleComanda = detalleComandaAdapter
				.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuDetalleComandaTerminar:
			TerminarDetalleComanda tdc = new TerminarDetalleComanda();
			tdc.execute(detalleComanda.getIdDetalleComanda(),
					this.comanda.getIdComanda());
			return true;

		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvDetalles = (ListView) getActivity().findViewById(
				R.id.detalleComandaLvDetalles);
		btTerminarCocina = (Button) getActivity().findViewById(
				R.id.detalleComandaBtTerminarCocina);
		btCancelarComanda = (Button) getActivity().findViewById(
				R.id.detalleComandaBtCancelarComanda);
		btCerrarComanda = (Button) getActivity().findViewById(
				R.id.detalleComandaBtCerrarComanda);

		tvIdComanda = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvIdComanda);
		tvCamarero = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvCamarero);
		tvPrecio = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvPrecio);
		tvEstado = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvEstado);
		tvObservaciones = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvObservaciones);
		tvMesa = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvMesa);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.detalleComandaFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		if (mDualPane) {
			Bundle bundle = getArguments();
			this.comanda = bundle.getParcelable(EXTRA_COMANDA);
		} else {
			Intent intent = getActivity().getIntent();
			this.comanda = intent.getParcelableExtra(EXTRA_COMANDA);
		}

		// Asigno los valores
		tvIdComanda.setText(Integer.toString(this.comanda.getIdComanda()));
		tvCamarero.setText(this.comanda.getCamarero().getNombre());
		tvObservaciones.setText(this.comanda.getObservaciones());
		
		//Compruebo si la mesa es nula
		if (this.comanda.getMesa() != null) {
			tvMesa.setText(this.comanda.getMesa().getNombre());
		}else{
			tvMesa.setText("");
		}
		tvEstado.setText(this.comanda.getEstado());
		tvPrecio.setText(Float.toString(this.comanda.getPrecio()));

		// Creo los detalles
		DetalleComandaAdapter detalleComandaAdapter = new DetalleComandaAdapter(
				getActivity(), 1, this.comanda.getDetallesComanda());

		lvDetalles.setAdapter(detalleComandaAdapter);

		registerForContextMenu(lvDetalles);

		// Asigno los listeners a los botones
		btCancelarComanda.setOnClickListener(oclCancelarComanda);
		btCancelarComanda.setTag(this.comanda);
		btTerminarCocina.setOnClickListener(oclTerminarCocina);
		btTerminarCocina.setTag(this.comanda);
		btCerrarComanda.setOnClickListener(oclCerrarCamarero);
		btCerrarComanda.setTag(this.comanda);

	}

	private void ocultarDetalle() {
		if (mDualPane) {
			ListaComandasFragment listaFragment;

			// Make new fragment to show this selection.
			listaFragment = (ListaComandasFragment) getFragmentManager()
					.findFragmentById(R.id.listaComandasFl);

			// listaFragment.actualizarLista(this.comanda.getEstado());
			listaFragment.ocultarDetalle();

		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_CANCELED, intent);
			getActivity().finish();
		}
	}

	public void actualizarDetalle(int idComanda) {
		GestionComanda gc = new GestionComanda(getActivity());
		this.comanda = gc.obtenerComanda(idComanda);
		gc.cerrarDatabase();
		actualizarDetalle();
	}

	private void actualizarDetalle() {
		tvIdComanda.setText(Integer.toString(this.comanda.getIdComanda()));
		tvPrecio.setText(Float.toString(this.comanda.getPrecio()));
		tvCamarero.setText(this.comanda.getCamarero().getNombre());
		tvEstado.setText(this.comanda.getEstado());
		tvObservaciones.setText(this.comanda.getObservaciones());

		// Si no hay mesa se oculta el campo
		if (this.comanda.getMesa() != null) {
			tvMesa.setText(this.comanda.getMesa().getNombre());
		} else {
			tvMesa.setVisibility(View.GONE);
		}

		List<DetalleComanda> detallesComanda = new ArrayList<DetalleComanda>();

		detallesComanda = this.comanda.getDetallesComanda();

		DetalleComandaAdapter detalleComandaAdapter = new DetalleComandaAdapter(
				getActivity(), R.layout.detalle_comanda_menu_item,
				detallesComanda);

		lvDetalles.setAdapter(detalleComandaAdapter);

		registerForContextMenu(lvDetalles);
	}

	private void actualizarLista() {
		if (mDualPane) {
			ListaComandasFragment listaFragment;

			// Make new fragment to show this selection.
			listaFragment = (ListaComandasFragment) getFragmentManager()
					.findFragmentById(R.id.listaComandasFl);

			listaFragment.actualizarLista(GestionComanda.ESTADO_ACTIVO);
		}
	}

	private JSONObject enviarCancelarComanda(int idComanda) {
		JSONObject respJSON = null;
		String url = "";

		url = LoginActivity.SITE_URL
				+ "/api/comandas/cancelarComanda/format/json";

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionComanda.JSON_ID_COMANDA, idComanda);

			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));

			json = jsonObject.toString();

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

			String respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private JSONObject enviarTerminarComandaCocina(int idComanda) {
		JSONObject respJSON = null;
		String url = "";

		url = LoginActivity.SITE_URL
				+ "/api/comandas/terminarComandaCocina/format/json";

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionComanda.JSON_ID_COMANDA, idComanda);

			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));

			json = jsonObject.toString();

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

			String respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private JSONObject enviarCerrarComandaCamarero(int idComanda) {
		JSONObject respJSON = null;
		String url = "";

		url = LoginActivity.SITE_URL
				+ "/api/comandas/cerrarComandaCamarero/format/json";

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionComanda.JSON_ID_COMANDA, idComanda);

			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));

			json = jsonObject.toString();

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

			String respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private JSONObject enviarTerminarDetalle(int idDetalleComanda)
			throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/comandas/terminarDetalleComanda/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionComanda.JSON_ID_DETALLE_COMANDA,
					idDetalleComanda);

			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));

			json = jsonObject.toString();

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

			String respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	public class CancelarComanda extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idComanda;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idComanda = params[0];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = enviarCancelarComanda(idComanda);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						Comanda comanda = GestionComanda
								.comandaJson2Comanda(respJSON
										.getJSONObject(GestionComanda.JSON_COMANDA));

						// Se modifica el pedido en la bbdd del movil.
						GestionComanda gc = new GestionComanda(getActivity());
						gc.guardarComanda(comanda);
						gc.cerrarDatabase();

					}

					res = new Resultado(
							respJSON.getInt(Resultado.JSON_OPERACION_OK),
							mensaje);

				}
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
					actualizarLista();
					ocultarDetalle();
				}
			}

		}
	}

	public class TerminarComandaCocina extends
			AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idComanda;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idComanda = params[0];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = enviarTerminarComandaCocina(idComanda);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						Comanda comanda = GestionComanda
								.comandaJson2Comanda(respJSON
										.getJSONObject(GestionComanda.JSON_COMANDA));

						// Se modifica el pedido en la bbdd del movil.
						GestionComanda gc = new GestionComanda(getActivity());
						gc.guardarComanda(comanda);
						gc.cerrarDatabase();

					}

					res = new Resultado(
							respJSON.getInt(Resultado.JSON_OPERACION_OK),
							mensaje);

				}
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
					actualizarDetalle(idComanda);
					actualizarLista();
				}
			}

		}
	}

	public class CerrarComandaCamarero extends
			AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idComanda;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idComanda = params[0];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = enviarCerrarComandaCamarero(idComanda);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						Comanda comanda = GestionComanda
								.comandaJson2Comanda(respJSON
										.getJSONObject(GestionComanda.JSON_COMANDA));

						// Se modifica el pedido en la bbdd del movil.
						GestionComanda gc = new GestionComanda(getActivity());
						gc.guardarComanda(comanda);
						gc.cerrarDatabase();

					}

					res = new Resultado(
							respJSON.getInt(Resultado.JSON_OPERACION_OK),
							mensaje);

				}
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
					// actualizarDetalle(idComanda);
					actualizarLista();
					ocultarDetalle();
				}
			}

		}
	}

	public class TerminarDetalleComanda extends
			AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idDetalleComanda;
		int idComanda;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idDetalleComanda = params[0];
			this.idComanda = params[1];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = enviarTerminarDetalle(idDetalleComanda);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						Comanda comanda = GestionComanda
								.comandaJson2Comanda(respJSON
										.getJSONObject(GestionComanda.JSON_COMANDA));

						// Se modifica el pedido en la bbdd del movil.
						GestionComanda gc = new GestionComanda(getActivity());
						gc.guardarComanda(comanda);
						gc.cerrarDatabase();

					}

					res = new Resultado(
							respJSON.getInt(Resultado.JSON_OPERACION_OK),
							mensaje);

				}
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
					actualizarDetalle(idComanda);
					actualizarLista();
				}
			}

		}
	}

}
