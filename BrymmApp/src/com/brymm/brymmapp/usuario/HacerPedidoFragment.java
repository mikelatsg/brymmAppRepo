package com.brymm.brymmapp.usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.usuario.adapters.ArticuloLocalAdapter;
import com.brymm.brymmapp.usuario.bbdd.GestionDireccion;
import com.brymm.brymmapp.usuario.bbdd.GestionPedidoUsuario;
import com.brymm.brymmapp.usuario.bbdd.GestionServicioLocal;
import com.brymm.brymmapp.usuario.bbdd.GestionTiposArticulo;
import com.brymm.brymmapp.usuario.interfaces.ListaArticulosPedido;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalCantidad;
import com.brymm.brymmapp.usuario.pojo.ArticuloPersonalizado;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.Local;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

public class HacerPedidoFragment extends Fragment {

	private static final int REQUEST_ANADIR_ARTICULO = 1;
	private static final int REQUEST_ANADIR_ARTICULO_PERSONALIZADO = 2;

	private static final String JSON_OBSERVACIONES = "observaciones";
	private static final String JSON_ID_DIRECCION = "idDireccion";
	private static final String JSON_ID_LOCAL = "idLocal";
	private static final String JSON_ID_USUARIO = "idUsuario";
	private static final String JSON_FECHA = "fechaPedido";
	private static final String JSON_ARTICULOS = "articulos";
	private static final String JSON_PEDIDO_OK = "pedidoOK";
	private static final String JSON_PEDIDO = "pedido";
	private static final String JSON_MENSAJE = "mensaje";

	// Datos a mantener
	private static final String ENVIAR = "enviar";
	private static final String DIRECCION = "direccion";
	private static final String FECHA = "fecha";
	private static final String RETRASAR_PEDIDO = "retrasarPedido";
	private static final String OBSERVACIONES = "observaciones";
	private static final String ARTICULOS = "articulos";
	private static final String ARTICULOS_PERSONALIZADOS = "articulosPersonalizados";

	private Button bAnadirArticulo, bTerminarPedido,
			bAnadirArticuloPersonalizado;
	private ListView lvArticulos;
	private Spinner spDirecciones;
	private EditText etObservaciones, etFecha;
	protected CheckBox cbEnviar, cbFecha;

	private List<ListaArticulosPedido> articulosPedido = new ArrayList<ListaArticulosPedido>();
	private Bundle datosConservar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_hacer_pedido, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inicializar();

		// Compruebo si se han guardado datos para conservar (rotacion)
		if (savedInstanceState != null) {
			cbEnviar.setChecked(savedInstanceState.getBoolean(ENVIAR));
			cbFecha.setChecked(savedInstanceState.getBoolean(RETRASAR_PEDIDO));
			etFecha.setText(savedInstanceState.getString(FECHA));
			spDirecciones.setSelection(savedInstanceState.getInt(DIRECCION));
			etObservaciones
					.setText(savedInstanceState.getString(OBSERVACIONES));
			ArrayList<ArticuloLocalCantidad> articulos = savedInstanceState
					.getParcelableArrayList(ARTICULOS);
			ArrayList<ArticuloPersonalizado> articulosPersonalizados = savedInstanceState
					.getParcelableArrayList(ARTICULOS_PERSONALIZADOS);
			if (articulos != null) {
				this.articulosPedido.addAll(articulos);
			}
			articulosPersonalizados = savedInstanceState
					.getParcelableArrayList(ARTICULOS_PERSONALIZADOS);
			if (articulosPersonalizados != null) {
				this.articulosPedido.addAll(articulosPersonalizados);
			}
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu_pedido, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		if (item.getItemId() == R.id.contextMenuPedidoBorrar) {
			// lvArticulos.getAdapter();
			ArticuloLocalAdapter articulosAdapter = (ArticuloLocalAdapter) lvArticulos
					.getAdapter();
			articulosAdapter.remove(articulosAdapter.getItem(info.position));
			articulosAdapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		bAnadirArticulo = (Button) getActivity().findViewById(
				R.id.hacerPedidoBtAnadirArticulo);
		bTerminarPedido = (Button) getActivity().findViewById(
				R.id.hacerPedidoBtTerminarPedido);
		lvArticulos = (ListView) getActivity().findViewById(
				R.id.hacerPedidoLvListaArticulos);
		etObservaciones = (EditText) getActivity().findViewById(
				R.id.hacerPedidoEtObservaciones);
		etFecha = (EditText) getActivity()
				.findViewById(R.id.hacerPedidoEtFecha);
		cbFecha = (CheckBox) getActivity()
				.findViewById(R.id.hacerPedidoCbfecha);

		ArticuloLocalAdapter articulosAdapter = new ArticuloLocalAdapter(
				getActivity(), R.layout.articulo_pedido_item, articulosPedido);

		lvArticulos.setAdapter(articulosAdapter);
		registerForContextMenu(lvArticulos);

		// Si existe algun tipo de articulo personalizable se a�ade el boton
		GestionTiposArticulo gta = new GestionTiposArticulo(getActivity());
		if (gta.hayArticuloPersonalizable()) {
			LinearLayout llBotones = (LinearLayout) getActivity().findViewById(
					R.id.hacerPedidoLlBotones);
			Resources res = getResources();
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
			bAnadirArticuloPersonalizado = new Button(getActivity());
			bAnadirArticuloPersonalizado
					.setText(res
							.getString(R.string.hacer_pedido_anadir_articulo_personalizado));
			bAnadirArticuloPersonalizado.setLayoutParams(param);
			bAnadirArticuloPersonalizado.setTextSize(12);
			bAnadirArticulo.setLayoutParams(param);
			bTerminarPedido.setLayoutParams(param);
			llBotones.setWeightSum(3);
			llBotones.addView(bAnadirArticuloPersonalizado, 1);

			bAnadirArticuloPersonalizado
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							irAnadirArticuloPersonalizado();
						}
					});

		}
		gta.cerrarDatabase();

		// Se comprueba si hay envio de pedidos en el local.
		GestionServicioLocal gsl = new GestionServicioLocal(getActivity());
		if (gsl.hayEnvioPedidos() >= 0) {
			gsl.cerrarDatabase();

			GestionDireccion gd = new GestionDireccion(getActivity());
			List<Direccion> direcciones = gd.obtenerDireccionesUsuario();
			gd.cerrarDatabase();

			ArrayAdapter<Direccion> direccionAdapter = new ArrayAdapter<Direccion>(
					getActivity(), android.R.layout.simple_spinner_item,
					direcciones);

			LinearLayout llEnvioPedido = new LinearLayout(getActivity());

			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);

			spDirecciones = new Spinner(getActivity());
			spDirecciones.setAdapter(direccionAdapter);

			cbEnviar = new CheckBox(getActivity());
			cbEnviar.setText(R.string.hacer_pedido_enviar_a);
			cbEnviar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cbEnviar.isChecked()) {
						spDirecciones.setEnabled(true);
					} else {
						spDirecciones.setEnabled(false);
					}

				}
			});

			cbEnviar.setLayoutParams(param);
			spDirecciones.setLayoutParams(param);
			spDirecciones.setEnabled(false);

			llEnvioPedido.addView(cbEnviar);
			llEnvioPedido.addView(spDirecciones);

			LinearLayout llPrincipal = (LinearLayout) getActivity()
					.findViewById(R.id.hacerPedidoLlPrincipal);
			llPrincipal.addView(llEnvioPedido, 2);

		}
		gsl.cerrarDatabase();

		bAnadirArticulo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				irAnadirArticulo();
			}
		});

		bTerminarPedido.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CrearPedido crearPedido = new CrearPedido();
				crearPedido.execute();
			}
		});

		etFecha.setEnabled(false);
		/*
		 * etFecha.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { dialogoFecha(); } });
		 */

		etFecha.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialogoFecha();
				}

			}
		});

		cbFecha.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cbFecha.isChecked()) {
					etFecha.setEnabled(true);
					if (etFecha.getText().toString().equals("")) {
						dialogoFecha();
					}
				} else {
					etFecha.setEnabled(false);
				}

			}
		});

	}

	private void irAnadirArticulo() {
		Intent intent = new Intent(getActivity(), AnadirArticulo.class);
		startActivityForResult(intent, REQUEST_ANADIR_ARTICULO);
	}

	private void irAnadirArticuloPersonalizado() {
		Intent intent = new Intent(getActivity(),
				AnadirArticuloPersonalizado.class);
		startActivityForResult(intent, REQUEST_ANADIR_ARTICULO_PERSONALIZADO);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ANADIR_ARTICULO) {
			if (resultCode == Activity.RESULT_OK) {
				ArticuloLocalCantidad articulo = data
						.getParcelableExtra(AnadirArticulo.EXTRA_ARTICULO_CANTIDAD);
				articulosPedido.add(articulo);
			}
		} else if (requestCode == REQUEST_ANADIR_ARTICULO_PERSONALIZADO) {
			if (resultCode == Activity.RESULT_OK) {
				ArticuloPersonalizado articulo = data
						.getParcelableExtra(AnadirArticuloPersonalizado.EXTRA_ARTICULO_PERSONALIZADO);
				articulosPedido.add(articulo);
			}
		}

		((ArticuloLocalAdapter) lvArticulos.getAdapter())
				.notifyDataSetChanged();
	}

	private JSONObject enviarPedido() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url = new String(LoginActivity.SITE_URL
				+ "/api/pedidos/nuevoPedido/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			json = crearPedidoJson();

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

			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}

	private String crearPedidoJson() {
		Gson gson = new Gson();

		// Se a�ade el idLocal y el idUsuario
		Intent intent = getActivity().getIntent();
		Local local = intent
				.getParcelableExtra(MostrarLocalActivity.EXTRA_LOCAL);

		JsonElement jsonElementArticulos = gson.toJsonTree(articulosPedido);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(JSON_OBSERVACIONES, etObservaciones.getText()
				.toString());
		jsonObject.addProperty(JSON_ID_LOCAL, local.getIdLocal());
		jsonObject.addProperty(JSON_ID_USUARIO,
				LoginActivity.getUsuario(getActivity()));
		if (cbEnviar.isChecked()) {
			Direccion direccion = (Direccion) spDirecciones.getSelectedItem();
			jsonObject.addProperty(JSON_ID_DIRECCION,
					direccion.getIdDireccion());
		}
		if (cbFecha.isChecked()) {
			jsonObject.addProperty(JSON_FECHA, etFecha.getText().toString());
		}
		jsonObject.add(JSON_ARTICULOS, jsonElementArticulos);

		return jsonObject.toString();
	}

	public class CrearPedido extends AsyncTask<Void, Void, Resultado> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			Resources res = getActivity().getResources();
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
				respJSON = enviarPedido();

				boolean pedidoOk = respJSON.getInt(JSON_PEDIDO_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(JSON_MENSAJE);

				if (pedidoOk) {
					PedidoUsuario pedido = GestionPedidoUsuario
							.pedidoJson2PedidoUsuario(respJSON
									.getJSONObject(JSON_PEDIDO));

					GestionPedidoUsuario gpu = new GestionPedidoUsuario(
							getActivity());
					gpu.guardarPedido(pedido);
					gpu.cerrarDatabase();
				}

				res = new Resultado(respJSON.getInt(JSON_PEDIDO_OK), mensaje);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(Resultado resultado) {
			super.onPostExecute(resultado);
			progress.dismiss();
			Toast.makeText(getActivity(), resultado.getMensaje(),
					Toast.LENGTH_LONG).show();

			if (resultado.getCodigo() == 1) {
				MenuUsuario.irUltimosPedidos(getActivity());
			}
		}

	}

	@SuppressLint("NewApi")
	private void dialogoFecha() {
		final Dialog custom = new Dialog(getActivity());
		custom.setContentView(R.layout.dialog_fecha_pedido);

		final TimePicker tp = (TimePicker) custom
				.findViewById(R.id.dialogFechaPedidoTpHora);
		final DatePicker dp = (DatePicker) custom
				.findViewById(R.id.dialogFechaPedidoDpFecha);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			dp.setCalendarViewShown(false);
		}

		Button bAceptar = (Button) custom
				.findViewById(R.id.dialogFechaPedidoBtAceptar);
		Button bCancelar = (Button) custom
				.findViewById(R.id.dialogFechaPedidoBtCancelar);
		custom.setTitle("Custom Dialog");
		bAceptar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				int mes = dp.getMonth() + 1;
				etFecha.setText(dp.getYear() + "-" + mes + "-"
						+ dp.getDayOfMonth() + " " + tp.getCurrentHour() + ":"
						+ tp.getCurrentMinute());
				etFecha.clearFocus();
				custom.dismiss();
			}

		});
		bCancelar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				etFecha.clearFocus();
				custom.dismiss();
			}
		});
		custom.show();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(ENVIAR, cbEnviar.isChecked());
		outState.putBoolean(RETRASAR_PEDIDO, cbFecha.isChecked());
		outState.putInt(DIRECCION, spDirecciones.getSelectedItemPosition());
		outState.putString(FECHA, etFecha.getText().toString());
		outState.putString(OBSERVACIONES, etObservaciones.getText().toString());

		ArrayList<ArticuloLocalCantidad> articulos = new ArrayList<ArticuloLocalCantidad>();
		ArrayList<ArticuloPersonalizado> articulosPersonalizados = new ArrayList<ArticuloPersonalizado>();
		for (ListaArticulosPedido articulo : this.articulosPedido) {
			if (articulo instanceof ArticuloPersonalizado) {
				articulosPersonalizados.add((ArticuloPersonalizado) articulo);
			} else if (articulo instanceof ArticuloLocalCantidad) {
				articulos.add((ArticuloLocalCantidad) articulo);
			}
		}
		outState.putParcelableArrayList(ARTICULOS, articulos);
		outState.putParcelableArrayList(ARTICULOS_PERSONALIZADOS,  articulosPersonalizados);
	}

}
