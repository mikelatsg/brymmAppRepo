package com.brymm.brymmapp.usuario;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.menu.MenuUsuario;
import com.brymm.brymmapp.usuario.DireccionesActivity.BorrarDireccion;
import com.brymm.brymmapp.usuario.adapters.DireccionAdapter;
import com.brymm.brymmapp.usuario.adapters.ReservaAdapter;
import com.brymm.brymmapp.usuario.bbdd.GestionDireccion;
import com.brymm.brymmapp.usuario.bbdd.GestionReservaUsuario;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.ReservaUsuario;
import com.brymm.brymmapp.util.Resultado;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class UltimasReservasActivity extends ListActivity {

	private static final String RESERVA_ESTADO_PENDIENTE = "P";
	private static final String RESERVA_ESTADO_ACEPTADA_LOCAL = "AL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inicializar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usuario, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (MenuUsuario.gestionMenuUsuario(item.getItemId(), this)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_reserva, menu);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		int position = info.position;

		/*
		 * Si el estado de la reserva no es pendiente o aceptada local se
		 * desabilita la opcion de anular.
		 */

		ReservaUsuario reserva = (ReservaUsuario) getListView().getAdapter()
				.getItem(position);

		if (!reserva.getEstado().equals(RESERVA_ESTADO_ACEPTADA_LOCAL)
				&& !reserva.getEstado().equals(RESERVA_ESTADO_PENDIENTE)) {
			menu.getItem(0).setEnabled(false);
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ReservaAdapter reservaAdapter = (ReservaAdapter) getListView()
				.getAdapter();
		ReservaUsuario reserva = reservaAdapter.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.contextMenuReservaAnular:
			AnularReserva ar = new AnularReserva();
			ar.execute(reserva.getIdReserva());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void inicializar() {
		actualizarListaReservas();

		// Se registra el menu contextural
		registerForContextMenu(getListView());
	}

	private void actualizarListaReservas() {
		GestionReservaUsuario gru = new GestionReservaUsuario(this);
		List<ReservaUsuario> reservas = gru.obtenerReservasUsuario();
		gru.cerrarDatabase();

		ReservaAdapter reservaAdapter = new ReservaAdapter(this,
				R.layout.reserva_item, reservas);
		getListView().setAdapter(reservaAdapter);
	}

	private JSONObject anularReserva(int idReserva) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/reservas/anularReserva/idReserva/" + idReserva
					+ "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respStr = EntityUtils.toString(resp.getEntity());
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	public class AnularReserva extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idReserva;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(UltimasReservasActivity.this, "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			this.idReserva = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = anularReserva(this.idReserva);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						ReservaUsuario reserva = GestionReservaUsuario
								.reservaJson2ReservaUsuario((respJSON
										.getJSONObject(GestionReservaUsuario.JSON_RESERVA)));

						// Si todo Ok se guarda la reserva
						GestionReservaUsuario gru = new GestionReservaUsuario(
								UltimasReservasActivity.this);
						gru.guardarReserva(reserva);
						gru.cerrarDatabase();
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
				Toast.makeText(UltimasReservasActivity.this,
						resultado.getMensaje(), Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					actualizarListaReservas();
				}
			}

		}
	}

}
