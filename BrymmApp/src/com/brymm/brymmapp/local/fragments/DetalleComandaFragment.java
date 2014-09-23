package com.brymm.brymmapp.local.fragments;

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
import com.brymm.brymmapp.local.adapters.ArticuloCantidadAdapter;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.pojo.ArticuloCantidad;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.local.pojo.MenuComanda;
import com.brymm.brymmapp.local.pojo.Pedido;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DetalleComandaFragment extends Fragment {

	public static final String EXTRA_COMANDA = "extraComanda";

	private ListView lvArticulos;
	private ListView lvMenus;
	private Button btAccion, btRechazar, btCancelar;
	private TextView tvIdComanda, tvCamarero, tvPrecio, tvEstado, tvObservaciones,
			tvMesa;

	private boolean mDualPane;

	private OnClickListener oclTerminar = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Pedido pedido = (Pedido) v.getTag();

			ModificarEstadoPedido mep = new ModificarEstadoPedido();
			mep.execute(Integer.toString(pedido.getIdPedido()),
					GestionPedido.ESTADO_TERMINADO, pedido.getEstado());
		}
	};

	private OnClickListener oclTerminarCocina = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Comanda comanda = (Comanda) v.getTag();
		}
	};
	
	private OnClickListener oclCancelarCamarero = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Comanda comanda = (Comanda) v.getTag();			
		}
	};
	
	private OnClickListener oclCerrarCamarero = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Comanda comanda = (Comanda) v.getTag();			
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

	private void inicializar() {
		lvArticulos = (ListView) getActivity().findViewById(
				R.id.detalleComandaLvArticulos);
		btAccion = (Button) getActivity().findViewById(
				R.id.detalleComandaBtAccion);		
		btCancelar = (Button) getActivity().findViewById(
				R.id.detalleComandaBtCancelar);

		tvIdComanda = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvIdComanda);
		tvCamarero = (TextView) getActivity().findViewById(
				R.id.detallePedidoTvUsuario);
		tvPrecio = (TextView) getActivity().findViewById(
				R.id.detallePedidoTvPrecio);
		tvEstado = (TextView) getActivity().findViewById(
				R.id.detallePedidoTvEstado);
		tvObservaciones = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvObservaciones);
		tvMesa = (TextView) getActivity().findViewById(
				R.id.detalleComandaTvMesa);		

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.detalleComandaFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		Comanda comanda = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			comanda = bundle.getParcelable(EXTRA_COMANDA);
		} else {
			Intent intent = getActivity().getIntent();
			comanda = intent.getParcelableExtra(EXTRA_COMANDA);
		}

		tvIdComanda.setText(Integer.toString(comanda.getIdComanda()));
		tvPrecio.setText(Float.toString(comanda.getPrecio()));
		tvCamarero.setText(comanda.getCamarero().getNombre());
		tvEstado.setText(comanda.getEstado());

		// Si no hay mesa se oculta el campo
		if (comanda.getMesa() != null) {
			tvMesa.setText(comanda.getMesa().getNombre());
		} else {
			tvMesa.setVisibility(View.GONE);
		}		
		
		List<ArticuloCantidad> articulos = new ArrayList<ArticuloCantidad>();
		List<MenuComanda> menus = new ArrayList<MenuComanda>();
		
		for(DetalleComanda detalleComanda: comanda.getDetallesComanda()){
			switch (detalleComanda.getTipoComanda().getIdTipoComanda()){
			case 1:
				articulos.add(detalleComanda.getArticulo());
				break;
			case 2:
				articulos.add(detalleComanda.getArticulo());
				break;
			case 3:
				menus.add(detalleComanda.getMenuComanda());
				break;
			case 4:
				menus.add(detalleComanda.getMenuComanda());
				break;
			}
		}
		
		ArticuloCantidadAdapter articuloCantidadAdapter = new ArticuloCantidadAdapter(
				getActivity(), R.layout.articulo_cantidad_item,
				articulos);

		lvArticulos.setAdapter(articuloCantidadAdapter);

		Resources res = getActivity().getResources();

		/*
		 * Se gestiona el valor de los botones en base a el estado de la comanda, y
		 * se ocultan los botones no necesarios
		 */
		/*btCancelar.setOnClickListener(oclCancelar);
		btAccion.setTag(comanda);		
		if (comanda.getEstado().equals(GestionPedido.ESTADO_PENDIENTE)) {
			btAccion.setText(res.getString(R.string.detalle_pedido_aceptar));
			btAccion.setOnClickListener(oclAceptar);

		} else if (pedido.getEstado().equals(GestionPedido.ESTADO_ACEPTADO)) {
			btAccion.setText(res.getString(R.string.detalle_pedido_terminar));
			btAccion.setOnClickListener(oclTerminar);

		} else if (pedido.getEstado().equals(GestionPedido.ESTADO_RECHAZADO)) {
			btAccion.setVisibility(View.GONE);
			btRechazar.setVisibility(View.GONE);
		} else {
			// Terminado
			btAccion.setVisibility(View.GONE);
			btRechazar.setVisibility(View.GONE);
		}

		btCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ocultarDetalle();
			}
		});*/

	}

	private void ocultarDetalle() {
		if (mDualPane) {
			ListaComandasFragment listaFragment;

			// Make new fragment to show this selection.
			listaFragment = (ListaComandasFragment) getFragmentManager()
					.findFragmentById(R.id.listaComandasFl);

			listaFragment.ocultarDetalle();
		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_CANCELED, intent);
			getActivity().finish();
		}
	}

	private void actualizarDatos(String idDetalle) {
		if (mDualPane) {
			ListaComandasFragment listaFragment;

			// Make new fragment to show this selection.
			listaFragment = (ListaComandasFragment) getFragmentManager()
					.findFragmentById(R.id.listaComandasFl);

			listaFragment.ocultarDetalle();
			listaFragment.actualizarLista(idDetalle);
		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}
	}

	private JSONObject terminarPedido(int idPedido, String idEstado) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();
		String url = "";

		try {

			url = LoginActivity.SITE_URL
					+ "/api/pedidos/terminarPedido/idPedido/" + idPedido
					+ "/idEstado/" + idEstado + "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			respStr = EntityUtils.toString(resp.getEntity());
			Log.d("res", respStr);
			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	private JSONObject aceptarPedido(int idPedido, String idEstado,
			String fechaEntrega) throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/pedidos/aceptarPedido/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionPedido.JSON_ID_PEDIDO, idPedido);
			jsonObject.addProperty(GestionPedido.JSON_ESTADO, idEstado);
			jsonObject.addProperty(GestionPedido.JSON_FECHA_ENTREGA,
					fechaEntrega);

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(jsonObject.toString());

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

	private JSONObject rechazarPedido(int idPedido, String idEstado,
			String motivo) throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/pedidos/rechazarPedido/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionPedido.JSON_ID_PEDIDO, idPedido);
			jsonObject.addProperty(GestionPedido.JSON_ESTADO, idEstado);
			jsonObject.addProperty(GestionPedido.JSON_MOTIVO_RECHAZO, motivo);

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(jsonObject.toString());

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

	public class ModificarEstadoPedido extends
			AsyncTask<String, Void, Resultado> {

		ProgressDialog progress;
		int idPedido;
		String idEstado;
		private String estadoActual;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(String... params) {
			JSONObject respJSON = null;
			this.idPedido = Integer.parseInt(params[0]);
			this.idEstado = params[1];
			this.estadoActual = params[2];
			String mensaje = "";
			Resultado res = null;
			try {
				if (this.idEstado.equals(GestionPedido.ESTADO_TERMINADO)) {
					respJSON = terminarPedido(this.idPedido, this.idEstado);
				}
				if (this.idEstado.equals(GestionPedido.ESTADO_ACEPTADO)) {
					respJSON = aceptarPedido(this.idPedido, this.idEstado,
							params[3]);
				} else {
					respJSON = rechazarPedido(this.idPedido, this.idEstado,
							params[3]);
				}

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {
						Pedido pedido = GestionPedido
								.pedidoJson2Pedido(respJSON
										.getJSONObject(GestionPedido.JSON_PEDIDO));

						// Se modifica el pedido en la bbdd del movil.
						GestionPedido gp = new GestionPedido(getActivity());
						gp.guardarPedido(pedido);
						gp.cerrarDatabase();

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
					actualizarDatos(this.estadoActual);
				}
			}

		}
	}

}
