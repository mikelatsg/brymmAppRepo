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
import com.brymm.brymmapp.local.AnadirArticuloComandaActivity;
import com.brymm.brymmapp.local.AnadirArticuloPerComandaActivity;
import com.brymm.brymmapp.local.adapters.DetalleComandaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionMesa;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.local.pojo.Articulo;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.util.Resultado;

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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CrearComandaFragment extends Fragment implements ListaEstado {

	public static final String EXTRA_COMANDA = "extraComanda";

	public static final int REQUEST_CODE_ANADIR_ARTICULO = 1;
	public static final int REQUEST_CODE_ANADIR_ARTICULO_PER = 2;

	private Button btEnviar, btCancelar, btAnadirArticulo, btAnadirArticuloPer,
			btAnadirMenu;
	private Spinner spMesas;
	private EditText etNombreDe, etObservaciones;
	private TextView tvPrecio;
	private ListView lvDetalles;
	private boolean mDualPane;
	private RadioButton rbMesa, rbNombreDe;
	private Comanda comanda;

	private OnClickListener oclEnviarComanda = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};

	private OnClickListener oclMostrarAnadirArticulo = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mostrarAnadirArticulo();
		}
	};
	
	private OnClickListener oclMostrarAnadirArticuloPer = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mostrarAnadirArticuloPer();
		}
	};

	private OnClickListener oclRadio = new OnClickListener() {

		@Override
		public void onClick(View v) {
			cambioRadioButton();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_crear_comanda, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {

		List<DetalleComanda> detallesComanda = new ArrayList<DetalleComanda>();

		this.comanda = new Comanda(0, "", null, "", (float) 0, null, "", "",
				detallesComanda);

		btEnviar = (Button) getActivity().findViewById(
				R.id.crearComandaBtEnviarComanda);
		btCancelar = (Button) getActivity().findViewById(
				R.id.crearComandaBtCancelarComanda);
		btAnadirArticulo = (Button) getActivity().findViewById(
				R.id.crearComandaBtAnadirArticulo);
		btAnadirArticuloPer = (Button) getActivity().findViewById(
				R.id.crearComandaBtAnadirArticuloPer);
		btAnadirMenu = (Button) getActivity().findViewById(
				R.id.crearComandaBtAnadirMenu);

		spMesas = (Spinner) getActivity()
				.findViewById(R.id.crearComandaSpMesas);

		tvPrecio = (TextView) getActivity().findViewById(
				R.id.crearComandaTvPrecio);

		etObservaciones = (EditText) getActivity().findViewById(
				R.id.crearComandaEtObservaciones);

		etNombreDe = (EditText) getActivity().findViewById(
				R.id.crearComandaEtNombreDe);

		rbMesa = (RadioButton) getActivity().findViewById(
				R.id.crearComandaRbMesa);

		rbNombreDe = (RadioButton) getActivity().findViewById(
				R.id.crearComandaRbNombreDe);

		lvDetalles = (ListView) getActivity().findViewById(
				R.id.crearComandaLvDetalles);

		/*
		 * Se guarda si esta en modo dual (con la lista y el formulario a la
		 * vez)
		 */
		View listaFragment = getActivity().findViewById(R.id.listaComandasFl);
		mDualPane = listaFragment != null
				&& listaFragment.getVisibility() == View.VISIBLE;

		List<Mesa> mesas = new ArrayList<Mesa>();

		GestionMesa gm = new GestionMesa(getActivity());
		mesas = gm.obtenerMesas();
		gm.cerrarDatabase();

		ArrayAdapter<Mesa> mesasAdapter = new ArrayAdapter<Mesa>(getActivity(),
				android.R.layout.simple_spinner_item, mesas);

		spMesas.setAdapter(mesasAdapter);

		// Se envian los datos al servidor cuando se hace click en enviar
		btEnviar.setOnClickListener(oclEnviarComanda);

		rbMesa.setOnClickListener(oclRadio);
		rbNombreDe.setOnClickListener(oclRadio);

		rbMesa.setChecked(true);
		cambioRadioButton();

		btAnadirArticulo.setOnClickListener(oclMostrarAnadirArticulo);
		btAnadirArticuloPer.setOnClickListener(oclMostrarAnadirArticuloPer);

	}

	public void actualizarComanda() {
		tvPrecio.setText(Float.toString(this.comanda.getPrecio()));
		// Marco si es mesa o envio.
		rbMesa.setChecked(true);
		if (this.comanda.getMesa() == null) {
			rbNombreDe.setChecked(true);
		}
		List<DetalleComanda> detallesComanda = this.comanda
				.getDetallesComanda();
		if (detallesComanda != null) {
			DetalleComandaAdapter detalleComandaAdapter = new DetalleComandaAdapter(
					getActivity(), R.layout.detalle_comanda_articulo_item,
					detallesComanda);
			
			lvDetalles.setAdapter(detalleComandaAdapter);
		}
	}

	public void setComanda(Comanda comanda) {
		this.comanda = comanda;
	}

	@Override
	public void actualizarLista(String estado) {
		actualizarComanda();

	}

	@Override
	public void ocultarDetalle() {
		if (mDualPane) {
			Fragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = (Fragment) getFragmentManager()
					.findFragmentById(R.id.detalleComandaFl);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			if (detalleFragment != null) {

				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.remove(detalleFragment);

				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}
		}

	}

	private void cambioRadioButton() {
		spMesas.setVisibility(View.GONE);
		etNombreDe.setVisibility(View.GONE);
		// Muestro la lista de mesas o el campo a nombre de...
		if (rbMesa.isChecked()) {
			spMesas.setVisibility(View.VISIBLE);
		}

		if (rbNombreDe.isChecked()) {
			etNombreDe.setVisibility(View.VISIBLE);
		}
	}

	private void mostrarAnadirArticulo() {
		if (mDualPane) {
			AnadirArticuloComandaFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirArticuloComandaFragment();

			Bundle bundle = new Bundle();
			bundle.putParcelable(EXTRA_COMANDA, this.comanda);
			anadirFragment.setArguments(bundle);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleComandaFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirArticuloComandaActivity.class);
			intent.putExtra(EXTRA_COMANDA, this.comanda);
			startActivityForResult(intent, REQUEST_CODE_ANADIR_ARTICULO);
		}

	}
	
	private void mostrarAnadirArticuloPer() {
		if (mDualPane) {
			AnadirArticuloPerComandaFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirArticuloPerComandaFragment();

			Bundle bundle = new Bundle();
			bundle.putParcelable(EXTRA_COMANDA, this.comanda);
			anadirFragment.setArguments(bundle);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleComandaFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();

		} else {

			Intent intent = new Intent(getActivity(),
					AnadirArticuloPerComandaActivity.class);
			intent.putExtra(EXTRA_COMANDA, this.comanda);
			startActivityForResult(intent, REQUEST_CODE_ANADIR_ARTICULO_PER);
		}

	}

	private JSONObject enviarArticulo() throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/articulos/nuevoArticulo/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			// 4. convert JSONObject to JSON to String
			// json = crearArticuloJson();

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
								.findFragmentById(R.id.listaArticulosFr);

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
