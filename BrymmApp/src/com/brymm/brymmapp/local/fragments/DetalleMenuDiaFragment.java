package com.brymm.brymmapp.local.fragments;

import java.io.IOException;
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
import com.brymm.brymmapp.local.adapters.MesaAdapter;
import com.brymm.brymmapp.local.adapters.PlatoAdapter;
import com.brymm.brymmapp.local.bbdd.GestionMenuDia;
import com.brymm.brymmapp.local.bbdd.GestionMesa;
import com.brymm.brymmapp.local.bbdd.GestionPedido;
import com.brymm.brymmapp.local.bbdd.GestionPlato;
import com.brymm.brymmapp.local.bbdd.GestionReserva;
import com.brymm.brymmapp.local.fragments.DetalleReservaFragment.AsignarMesaReserva;
import com.brymm.brymmapp.local.fragments.DetalleReservaFragment.DesasignarMesaReserva;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.Mesa;
import com.brymm.brymmapp.local.pojo.Pedido;
import com.brymm.brymmapp.local.pojo.Plato;
import com.brymm.brymmapp.local.pojo.Reserva;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class DetalleMenuDiaFragment extends Fragment {

	public static final String EXTRA_MENU_DIA = "extraMenuDia";

	private ListView lvPlatosMenu, lvRestoPlatos;
	private TextView tvNombre, tvTipoMenu, tvPrecio;
	//private RadioButton rbCarta, rbMenu;
	private Button btCancelar;

	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_detalle_menu_dia,
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
		if (v.getId() == R.id.detalleMenuDiaLvPlatosMenu) {
			inflater.inflate(R.menu.context_menu_desasignar_plato, menu);
		} else {
			inflater.inflate(R.menu.context_menu_asignar_plato, menu);
		}

	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		MenuDia menuDia = (MenuDia) lvPlatosMenu.getTag();
		PlatoAdapter platoAdapter;
		Plato plato;
		switch (item.getItemId()) {
		case R.id.contextMenuMenuDiaAsignarPlato:
			platoAdapter = (PlatoAdapter) lvRestoPlatos.getAdapter();
			plato = platoAdapter.getItem(info.position);
			AsignarPlatoMenu amr = new AsignarPlatoMenu();
			amr.execute(menuDia.getIdMenuDia(), plato.getIdPlato());
			return true;

		case R.id.contextMenuMenuDiaDesasignarPlato:
			platoAdapter = (PlatoAdapter) lvPlatosMenu.getAdapter();
			plato = platoAdapter.getItem(info.position);
			DesasignarPlatoMenu dpr = new DesasignarPlatoMenu();
			dpr.execute(menuDia.getIdMenuDia(), plato.getIdPlato());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvPlatosMenu = (ListView) getActivity().findViewById(
				R.id.detalleMenuDiaLvPlatosMenu);
		lvRestoPlatos = (ListView) getActivity().findViewById(
				R.id.detalleMenuDiaLvPlatosNoMenu);

		tvNombre = (TextView) getActivity().findViewById(
				R.id.detalleMenuDiaTvNombre);
		tvTipoMenu = (TextView) getActivity().findViewById(
				R.id.detalleMenuDiaTvTipoMenu);
		tvPrecio = (TextView) getActivity().findViewById(
				R.id.detalleMenuDiaTvPrecio);

		btCancelar = (Button) getActivity().findViewById(
				R.id.detalleMenuDiaBtCancelar);

		/* Se guarda si esta el fragmento de lista */
		View listaFrame = getActivity().findViewById(R.id.listaMenusDiaFr);
		mDualPane = listaFrame != null
				&& listaFrame.getVisibility() == View.VISIBLE;

		MenuDia menuDia = null;
		if (mDualPane) {
			Bundle bundle = getArguments();
			menuDia = bundle.getParcelable(EXTRA_MENU_DIA);
		} else {
			Intent intent = getActivity().getIntent();
			menuDia = intent.getParcelableExtra(EXTRA_MENU_DIA);
		}

		tvNombre.setText(menuDia.getMenu().getNombre());
		tvTipoMenu.setText(menuDia.getMenu().getTipoMenu().getDescripcion());
		tvPrecio.setText(Float.toString(menuDia.getMenu().getPrecio()));

		//Se muestran los platos del menu
		actualizarPlatosMenuDia(menuDia);

		lvPlatosMenu.setTag(menuDia);
		registerForContextMenu(lvPlatosMenu);
		
		lvRestoPlatos.setTag(menuDia);
		registerForContextMenu(lvRestoPlatos);
		
		btCancelar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ocultarDetalle();
			}
		});

	}

	private void ocultarDetalle() {
		if (mDualPane) {
			ListaEstado listaFragment;

			// Make new fragment to show this selection.
			listaFragment = (ListaEstado) getFragmentManager()
					.findFragmentById(R.id.listaMenusDiaFr);

			listaFragment.ocultarDetalle();
		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_CANCELED, intent);
			getActivity().finish();
		}
	}

	private void actualizarDatos(String fecha) {
		if (mDualPane) {
			ListaMenusDiaFragment listaFragment;

			// Make new fragment to show this selection.
			listaFragment = (ListaMenusDiaFragment) getFragmentManager()
					.findFragmentById(R.id.listaMenusDiaFr);

			listaFragment.ocultarDetalle();
			listaFragment.actualizarLista(fecha);
		} else {
			Intent intent = new Intent();
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}
	}
	
	private void actualizarPlatosMenuDia(MenuDia menuDia) {
		GestionMenuDia gestor = new GestionMenuDia(getActivity());
		List<Plato> platosMenu = gestor.obtenerPlatosMenuDia(menuDia
				.getIdMenuDia());
		List<Plato> platosNoMenu = gestor.obtenerPlatosNoMenuDia(menuDia
				.getIdMenuDia());
		gestor.cerrarDatabase();

		PlatoAdapter platoAdapter = new PlatoAdapter(getActivity(),
				R.layout.plato_item, platosMenu);

		lvPlatosMenu.setAdapter(platoAdapter);

		PlatoAdapter platoNoMenuAdapter = new PlatoAdapter(getActivity(),
				R.layout.plato_item, platosNoMenu);

		lvRestoPlatos.setAdapter(platoNoMenuAdapter);
	}

	private JSONObject asignarPlatoMenu(int idMenuDia, int idPlato)
			throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/menus/asignarPlatoMenu/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionMenuDia.JSON_ID_MENU_DIA, idMenuDia);
			jsonObject.addProperty(GestionPlato.JSON_ID_PLATO, idPlato);

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
			Log.w("respStr",respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}
	
	private JSONObject desasignarPlatoMenu(int idMenuDia, int idPlato)
			throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/menus/desasignarPlatoMenu/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionMenuDia.JSON_ID_MENU_DIA, idMenuDia);
			jsonObject.addProperty(GestionPlato.JSON_ID_PLATO, idPlato);

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
			Log.w("respStr",respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return respJSON;
	}
	
	public class AsignarPlatoMenu extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idMenuDia;
		int idPlato;

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
			this.idMenuDia = params[0];
			this.idPlato = params[1];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = asignarPlatoMenu(this.idMenuDia, this.idPlato);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {						
						MenuDia menuDia = GestionMenuDia
								.menuDiaJson2MenuDia(respJSON
										.getJSONObject(GestionMenuDia.JSON_MENU_DIA));

						// Se modifica el menu dia en la bbdd del movil.
						GestionMenuDia gestor = new GestionMenuDia(
								getActivity());
						gestor.guardarMenuDia(menuDia);
						gestor.cerrarDatabase();

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
					// Se actualiza la reserva en la vista.
					GestionMenuDia gestor = new GestionMenuDia(getActivity());
					MenuDia menuDia = gestor.obtenerMenuDia(this.idMenuDia);
					gestor.cerrarDatabase();
					actualizarPlatosMenuDia(menuDia);
				}
			}

		}
	}
	
	public class DesasignarPlatoMenu extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idMenuDia;
		int idPlato;

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
			this.idMenuDia = params[0];
			this.idPlato = params[1];
			String mensaje = "";
			Resultado res = null;
			try {

				respJSON = desasignarPlatoMenu(this.idMenuDia, this.idPlato);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {						
						
						GestionMenuDia gestor = new GestionMenuDia(
								getActivity());
						
						if (respJSON.isNull(GestionMenuDia.JSON_MENU_DIA)){
							gestor.borrarMenuDia(this.idMenuDia);	
						}else{
							MenuDia menuDia = GestionMenuDia
									.menuDiaJson2MenuDia(respJSON
											.getJSONObject(GestionMenuDia.JSON_MENU_DIA));

							// Se modifica el menu dia en la bbdd del movil.							
							gestor.guardarMenuDia(menuDia);
							
						}
												
						gestor.cerrarDatabase();

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
					// Se actualiza la reserva en la vista.
					GestionMenuDia gestor = new GestionMenuDia(getActivity());
					MenuDia menuDia = gestor.obtenerMenuDia(this.idMenuDia);
					gestor.cerrarDatabase();
					if (menuDia == null){
						ocultarDetalle();
					}else{
						actualizarPlatosMenuDia(menuDia);	
					}
					
				}
			}

		}
	}
}
