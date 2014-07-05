package com.brymm.brymmapp.usuario;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.bbdd.GestionPedidoUsuario;
import com.brymm.brymmapp.usuario.bbdd.GestionReservaUsuario;
import com.brymm.brymmapp.usuario.pojo.Local;
import com.brymm.brymmapp.usuario.pojo.PedidoUsuario;
import com.brymm.brymmapp.usuario.pojo.ReservaUsuario;
import com.brymm.brymmapp.util.Resultado;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MenuFragment extends Fragment {

	private static final String JSON_MENU = "menus";
	private static final String JSON_NOMBRE_MENU = "nombreMenu";
	private static final String JSON_FECHA_MENU = "fechaMenu";
	private static final String JSON_TIPO_MENU = "tipoMenu";
	private static final String JSON_DETALLE_MENU = "detalleMenu";
	private static final String JSON_ID_TIPO_PLATO = "idTipoPlato";
	private static final String JSON_TIPO_PLATO = "tipoPlato";
	private static final String JSON_NOMBRE_PLATO = "nombrePlato";
	private static final String JSON_PRECIO_MENU = "precioMenu";

	private EditText etFecha;
	private Spinner spTipoMenu;
	private Button btVerMenu;
	private List<String> tiposMenu;
	private List<Integer> tiposMenuId;

	private OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ObtenerMenu om = new ObtenerMenu();
			
			//Se obtienen los datos necesarios para enviar la petición
			String idTipoMenu = Integer.toString(tiposMenuId.get(spTipoMenu
					.getSelectedItemPosition()));
			
			String fechaMenu;
			if (etFecha.getText().toString().equals("")) {
				fechaMenu = Resultado.NO_DATA;
			} else {
				fechaMenu = etFecha.getText().toString().replace("/", "-");
			}
			
			// Se obtiene el local del intent
			Intent intent = getActivity().getIntent();
			Local local = intent
					.getParcelableExtra(MostrarLocalActivity.EXTRA_LOCAL);
			
			om.execute(fechaMenu,Integer.toString(local.getIdLocal()),idTipoMenu);

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_menu, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {
		etFecha = (EditText) getActivity()
				.findViewById(R.id.obtenerMenuEtFecha);
		spTipoMenu = (Spinner) getActivity().findViewById(
				R.id.obtenerMenuSpTipoMenu);
		btVerMenu = (Button) getActivity().findViewById(
				R.id.obtenerMenuBtVerMenu);

		String[] tiposMenuArray = getResources().getStringArray(
				R.array.tipoMenuArray);
		tiposMenu = new ArrayList<String>();
		tiposMenuId = new ArrayList<Integer>();
		for (int i = 0; i < tiposMenuArray.length; i++) {
			String[] stringTipoMenu = tiposMenuArray[i].split("#");
			tiposMenu.add(stringTipoMenu[0]);
			tiposMenuId.add(Integer.parseInt(stringTipoMenu[1]));
		}

		ArrayAdapter<String> tipoMenuAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, tiposMenu);
		spTipoMenu.setAdapter(tipoMenuAdapter);

		etFecha.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dialogoFecha();
				}

			}
		});

		btVerMenu.setOnClickListener(ocl);
	}

	@SuppressLint("NewApi")
	private void dialogoFecha() {
		final Dialog custom = new Dialog(getActivity());
		custom.setContentView(R.layout.dialog_fecha);

		final DatePicker dp = (DatePicker) custom
				.findViewById(R.id.dialogFechaDpFecha);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			dp.setCalendarViewShown(false);
		}

		Button bAceptar = (Button) custom
				.findViewById(R.id.dialogFechaBtAceptar);
		Button bCancelar = (Button) custom
				.findViewById(R.id.dialogFechaBtCancelar);
		custom.setTitle("Custom Dialog");
		bAceptar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				int mes = dp.getMonth() + 1;
				etFecha.setText(dp.getDayOfMonth() + "/" + mes + "/"
						+ dp.getYear());
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

	private void mostrarMenu(JSONArray menusJson) throws JSONException {
		TextView tvMenu = new TextView(getActivity());
		LinearLayout ll = (LinearLayout) getActivity().findViewById(
				R.id.obtenerMenuLlprincipal);

		StringBuilder sb = new StringBuilder();

		// Se genera el texto a mostrar
		for (int i = 0; i < menusJson.length(); i++) {
			int idTipoPlatoAnterior = 0;
			JSONObject menuJson = menusJson.getJSONObject(i);
			sb.append("<b>" + menuJson.getString(JSON_NOMBRE_MENU) + "</b>");
			sb.append("<br>");
			sb.append(menuJson.getString(JSON_TIPO_MENU) + "-"
					+ menuJson.getString(JSON_FECHA_MENU));
			sb.append("<br>");
			JSONArray detallesJson = menuJson.getJSONArray(JSON_DETALLE_MENU);
			for (int j = 0; j < detallesJson.length(); j++) {
				JSONObject detalleJson = detallesJson.getJSONObject(j);
				boolean tipoPlatoNuevo = false;
				if (idTipoPlatoAnterior != detalleJson
						.getInt(JSON_ID_TIPO_PLATO)) {
					idTipoPlatoAnterior = detalleJson
							.getInt(JSON_ID_TIPO_PLATO);
					sb.append(detalleJson.getString(JSON_TIPO_PLATO));
					sb.append("<br>");
					tipoPlatoNuevo = true;
				}
				sb.append(detalleJson.getString(JSON_NOMBRE_PLATO) + "<br>");

			}
			sb.append(menuJson.getString(JSON_PRECIO_MENU));
			sb.append("<br>");
		}

		tvMenu.setText(Html.fromHtml(sb.toString()));
		ll.addView(tvMenu);
	}

	private JSONObject obtenerMenu(String fechaMenu, String idLocal,
			String idTipoMenu) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();				

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/menus/obtenerMenu/idLocal/" + idLocal + "/fecha/"
					+ fechaMenu + "/idTipoMenu/" + idTipoMenu + "/format/json";

			Log.d("url", url);

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respStr = EntityUtils.toString(resp.getEntity());
				Log.d("resultado", respStr);
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
			ex.printStackTrace();
		}
		return respJSON;
	}

	public class ObtenerMenu extends AsyncTask<String, Void, Resultado> {

		ProgressDialog progress;
		JSONObject respJSON = null;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(String... params) {

			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = obtenerMenu(params[0],params[1],params[2]);

				if (respJSON != null) {

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

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
				
				if (resultado.getCodigo() == Resultado.RES_OK) {
					try {
						mostrarMenu(respJSON.getJSONArray(JSON_MENU));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}

			

		}
	}

}
