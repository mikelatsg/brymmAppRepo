package com.brymm.brymmapp.local.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.AnadirMenuActivity;
import com.brymm.brymmapp.local.MenusActivity;
import com.brymm.brymmapp.local.adapters.MenuAdapter;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.local.bbdd.GestionMenu;
import com.brymm.brymmapp.local.interfaces.Lista;
import com.brymm.brymmapp.local.pojo.MenuLocal;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListaMenusFragment extends Fragment implements Lista {

	private ListView lvMenus;

	private Button btAnadir;

	private boolean mDualPane;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View root = inflater.inflate(R.layout.fragment_lista_menus, container,
				false);
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
		inflater.inflate(R.menu.context_menu_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		MenuAdapter menuAdapter = (MenuAdapter) lvMenus.getAdapter();
		MenuLocal menu = menuAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuMenuBorrar:
			BorrarMenu bm = new BorrarMenu();
			bm.execute(menu.getIdMenu());
			return true;

		case R.id.contextMenuMenuModificar:
			mostrarModificarMenu(menu);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		lvMenus = (ListView) getActivity().findViewById(
				R.id.listaMenusLvLista);
		btAnadir = (Button) getActivity()
				.findViewById(R.id.listaMenusBtanadir);

		/* Se guarda si esta el fragmento de añadir */
		View anadirFrame = getActivity().findViewById(R.id.detalleMenuFl);
		mDualPane = anadirFrame != null
				&& anadirFrame.getVisibility() == View.VISIBLE;

		btAnadir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mostrarNuevoMenu();
			}
		});

		actualizarLista();

		registerForContextMenu(lvMenus);

	}

	public void actualizarLista() {
		List<MenuLocal> menus = new ArrayList<MenuLocal>();
		GestionMenu gestor = new GestionMenu(getActivity());
		menus = gestor.obtenerMenus();
		gestor.cerrarDatabase();

		MenuAdapter menuAdapter = new MenuAdapter(getActivity(),
				R.layout.menu_item, menus);

		lvMenus.setAdapter(menuAdapter);
	}

	private void mostrarNuevoMenu() {
		if (mDualPane) {
			AnadirMenuFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirMenuFragment();

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleMenuFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(), AnadirMenuActivity.class);
			startActivityForResult(intent, MenusActivity.REQUEST_ANADIR_MENU);
		}

	}

	private void mostrarModificarMenu(MenuLocal menu) {
		if (mDualPane) {
			AnadirMenuFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = new AnadirMenuFragment();

			Bundle bundle = new Bundle();
			bundle.putParcelable(AnadirMenuFragment.EXTRA_MENU, menu);

			anadirFragment.setArguments(bundle);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.detalleMenuFl, anadirFragment);

			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			// }

		} else {

			Intent intent = new Intent(getActivity(), AnadirMenuActivity.class);
			intent.putExtra(AnadirMenuFragment.EXTRA_MENU, menu);
			startActivityForResult(intent, MenusActivity.REQUEST_ANADIR_PLATO);
		}

	}

	private JSONObject borrarMenu(int idMenu) {
		String respStr;
		JSONObject respJSON = null;

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/menus/borrarMenu/format/json";

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(getActivity()));
			jsonObject.addProperty(GestionMenu.JSON_ID_MENU, idMenu);

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

	public class BorrarMenu extends AsyncTask<Integer, Void, Resultado> {

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
			this.idMenu = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = borrarMenu(this.idMenu);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						GestionMenu gestor = new GestionMenu(getActivity());
						gestor.borrarMenu(this.idMenu);
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
					actualizarLista();
				}
			}

		}
	}

	public void ocultarDetalle() {
		if (mDualPane) {
			AnadirMesaFragment anadirFragment;

			// Make new fragment to show this selection.
			anadirFragment = (AnadirMesaFragment) getFragmentManager()
					.findFragmentById(R.id.detalleMenuFl);

			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			if (anadirFragment != null) {

				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.remove(anadirFragment);

				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}
		}
	}

}
