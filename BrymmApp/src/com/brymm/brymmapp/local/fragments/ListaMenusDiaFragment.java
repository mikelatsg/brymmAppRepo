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
import com.brymm.brymmapp.local.DetalleMenuDiaActivity;
import com.brymm.brymmapp.local.adapters.MenuAdapter;
import com.brymm.brymmapp.local.adapters.MenuDiaAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionMenu;
import com.brymm.brymmapp.local.bbdd.GestionMenuDia;
import com.brymm.brymmapp.local.fragments.ListaMenusFragment.BorrarMenu;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.interfaces.ListaEstado;
import com.brymm.brymmapp.local.pojo.MenuDia;
import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ListaMenusDiaFragment extends Fragment implements ListaEstado {

	public static final String EXTRA_FECHA = "extraFecha";
	public static final int REQUEST_CODE_DETALLE = 1;

	private ListView lvMenus;
	private ListView lvSeleccionMenu;
	private Button btAnadirMenuDia;

	private boolean mDualPane;
	private String fecha;

	private OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long arg3) {
			MenuDia menuDia = (MenuDia) adapter.getItemAtPosition(position);

			mostrarDetalleMenu(menuDia);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_menus_dia,
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
		inflater.inflate(R.menu.context_menu_menu_dia, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();						

		switch (item.getItemId()) {
		case R.id.contextMenuMenuDiaBorrar:
			MenuDiaAdapter menuDiaAdapter = (MenuDiaAdapter) lvMenus.getAdapter();
			MenuDia menuDia = menuDiaAdapter.getItem(info.position);
			
			BorrarMenuDia bm = new BorrarMenuDia();
			bm.execute(menuDia.getIdMenuDia());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvMenus = (ListView) getActivity().findViewById(
				R.id.listaMenusDiaLvLista);

		btAnadirMenuDia = (Button) getActivity().findViewById(
				R.id.listaMenusDiaBtAnadirMenu);
		btAnadirMenuDia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogoSeleccionarMenu();
			}
		});

		/* Se guarda si esta el fragmento de añadir */
		View detalleFrame = getActivity().findViewById(R.id.detalleMenuDiaFl);
		mDualPane = detalleFrame != null
				&& detalleFrame.getVisibility() == View.VISIBLE;

		// Se recoge el estado pasado
		Intent intent = getActivity().getIntent();
		this.fecha = intent.getStringExtra(EXTRA_FECHA);

		lvMenus.setOnItemClickListener(oicl);

		actualizarLista(this.fecha);

		registerForContextMenu(lvMenus);

	}

	@SuppressLint("NewApi")
	private void dialogoSeleccionarMenu() {
		final Dialog custom = new Dialog(getActivity());
		custom.setContentView(R.layout.dialog_seleccionar_menu_dia);

		lvSeleccionMenu = (ListView) custom
				.findViewById(R.id.listaMenusDialogoListaMenus);

		custom.setTitle("Custom Dialog");
		List<MenuLocal> menus = new ArrayList<MenuLocal>();
		GestionMenu gestor = new GestionMenu(getActivity());
		menus = gestor.obtenerMenus();
		gestor.cerrarDatabase();

		MenuAdapter menuAdapter = new MenuAdapter(getActivity(),
				R.layout.menu_item, menus);

		lvSeleccionMenu.setAdapter(menuAdapter);

		lvSeleccionMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3) {

				MenuLocal menuLocal = (MenuLocal) adapter
						.getItemAtPosition(position);

				EnviarMenuDia emd = new EnviarMenuDia();
				emd.execute(menuLocal.getIdMenu());

				custom.dismiss();

			}
		});

		custom.show();

	}

	public void actualizarLista(String fecha) {
		List<MenuDia> menus = new ArrayList<MenuDia>();
		GestionMenuDia gestor = new GestionMenuDia(getActivity());
		menus = gestor.obtenerMenusDia(fecha);
		gestor.cerrarDatabase();

		MenuDiaAdapter menuDiaAdapter = new MenuDiaAdapter(getActivity(),
				R.layout.menu_item, menus);

		lvMenus.setAdapter(menuDiaAdapter);
	}

	private void mostrarDetalleMenu(MenuDia menuDia) {
		if (mDualPane) {
			DetalleMenuDiaFragment detalleFragment;

			// Make new fragment to show this selection.
			detalleFragment = new DetalleMenuDiaFragment();

			Bundle args = new Bundle();
			args.putParcelable(DetalleMenuDiaFragment.EXTRA_MENU_DIA, menuDia);
			detalleFragment.setArguments(args);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleMenuDiaFl, detalleFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(),
					DetalleMenuDiaActivity.class);
			intent.putExtra(DetalleMenuDiaFragment.EXTRA_MENU_DIA, menuDia);
			startActivity(intent);
		}

	}

	public void ocultarDetalle() {
		if (mDualPane) {
			Fragment detalleFragment;

			detalleFragment = getFragmentManager().findFragmentById(
					R.id.detalleMenuDiaFl);

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

	private JSONObject anadirMenuDia(int idMenu) throws IOException,
			JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/menus/anadirMenuDia/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));
			jsonObject.addProperty(GestionMenu.JSON_ID_MENU, idMenu);
			jsonObject.addProperty(GestionMenuDia.JSON_FECHA, this.fecha);

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

	public class EnviarMenuDia extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idMenu;

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
			String mensaje = "";
			this.idMenu = params[0];
			Resultado res = null;
			try {
				respJSON = anadirMenuDia(idMenu);

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {

					// Si la operacion ha ido ok se guarda en la bbdd del movil
					MenuDia menuDia = GestionMenuDia
							.menuDiaJson2MenuDia((respJSON
									.getJSONObject(GestionMenuDia.JSON_MENU_DIA)));

					GestionMenuDia gestor = new GestionMenuDia(getActivity());
					gestor.guardarMenuDia(menuDia);
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

						ListaEstado listaFragment = (ListaEstado) getFragmentManager()
								.findFragmentById(R.id.listaMenusDiaFr);

						listaFragment.actualizarLista(fecha);
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
	
	private JSONObject borrarMenuDia(int idMenuDia) {
		String respStr;
		JSONObject respJSON = null;

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/menus/borrarMenuDia/format/json";

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));
			jsonObject.addProperty(GestionMenuDia.JSON_ID_MENU_DIA, idMenuDia);

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(jsonObject.toString());

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			// inputStream = httpResponse.getEntity().getContent();

			respStr = EntityUtils.toString(httpResponse.getEntity());
			Log.d("resultado", respStr);
			respJSON = new JSONObject(respStr);

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}


	public class BorrarMenuDia extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idMenuDia;

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
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarMenuDia(this.idMenuDia);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						GestionMenuDia gestor = new GestionMenuDia(getActivity());
						gestor.borrarMenuDia(this.idMenuDia);
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
					actualizarLista(fecha);
				}
			}

		}
	}

}
