package com.brymm.brymmapp.local.adapters;

import java.io.IOException;
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
import com.brymm.brymmapp.local.bbdd.GestionComanda;
import com.brymm.brymmapp.local.interfaces.Detalle;
import com.brymm.brymmapp.local.pojo.Comanda;
import com.brymm.brymmapp.local.pojo.DetalleComanda;
import com.brymm.brymmapp.local.pojo.Ingrediente;
import com.brymm.brymmapp.local.pojo.PlatoComanda;
import com.brymm.brymmapp.util.Resultado;
import com.google.gson.JsonObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class DetalleComandaAdapter extends ArrayAdapter<DetalleComanda> {
	private Context context;
	private List<DetalleComanda> detallesComanda;

	private TextView tvNombre, tvPrecio, tvCantidad, tvPrecioUnitario,
			tvIngredientes;

	private OnClickListener oclTerminarPlato = new OnClickListener() {

		@Override
		public void onClick(View v) {
			PlatoComanda platoComanda = (PlatoComanda) v.getTag();

			TerminarPlato tp = new TerminarPlato();
			tp.execute(platoComanda.getidComandaMenu());
		}
	};

	public DetalleComandaAdapter(Context context, int textViewResourceId,
			List<DetalleComanda> detallesComanda) {
		super(context, textViewResourceId, detallesComanda);
		this.context = context;
		this.detallesComanda = detallesComanda;
	}

	private void inicializarMenuComanda(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaMenuTvNombre);

		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaMenuTvPrecio);

		tvPrecioUnitario = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaMenuTvPrecioUnitario);

		tvCantidad = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaMenuTvCantidad);

		// se pone focusable a false para que funcione el menu contextual
		tvNombre.setFocusable(false);
		tvPrecio.setFocusable(false);
		tvCantidad.setFocusable(false);
		tvPrecioUnitario.setFocusable(false);
	}

	private void inicializarArticuloComanda(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaArticuloTvNombre);

		tvPrecio = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaArticuloTvPrecioTotal);

		tvPrecioUnitario = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaArticuloTvPrecioUnidad);

		tvCantidad = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaArticuloTvCantidad);

		tvIngredientes = (TextView) convertView
				.findViewById(R.id.itemDetalleComandaArticuloTvIngredientes);

		// se pone focusable a false para que funcione el menu contextual
		tvNombre.setFocusable(false);
		tvPrecio.setFocusable(false);
		tvCantidad.setFocusable(false);
		tvPrecioUnitario.setFocusable(false);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Obtener un objeto a partir del layout (xml)
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Obtengo el detalle
		DetalleComanda detalleComanda = detallesComanda.get(position);

		Resources res = convertView.getResources();

		// Compruebo el tipo de detalle para cargar una vista o otra.
		switch (detalleComanda.getTipoComanda().getIdTipoComanda()) {
		case 1:
		case 2:
			// Asigno el xml de los articulos
			convertView = li.inflate(R.layout.detalle_comanda_articulo_item,
					parent, false);

			inicializarArticuloComanda(convertView);

			// Asigno los valores.
			tvNombre.setText(detalleComanda.getArticulo().getNombre());
			tvCantidad.setText(Integer.toString(detalleComanda.getArticulo()
					.getCantidad()));
			tvPrecio.setText(Float.toString(detalleComanda.getPrecio()
					* detalleComanda.getCantidad()));
			tvPrecioUnitario
					.setText(Float.toString(detalleComanda.getPrecio()));

			StringBuilder stringIngredientes = new StringBuilder();
			for (Ingrediente ingrediente : detalleComanda.getArticulo()
					.getIngredientes()) {
				if (stringIngredientes.length() > 0) {
					stringIngredientes.append(", ");
				}
				stringIngredientes.append(ingrediente.getNombre());
			}

			// Si no tiene ingredientes no se oculta el textview
			if (detalleComanda.getArticulo().getIngredientes().size() > 0) {
				tvIngredientes.setText(stringIngredientes);
			} else {
				tvIngredientes.setVisibility(View.GONE);
			}
			break;
		case 3:
		case 4:
			// Asigno el xml de los menus
			convertView = li.inflate(R.layout.detalle_comanda_menu_item,
					parent, false);
			inicializarMenuComanda(convertView);

			tvNombre.setText(detalleComanda.getMenuComanda().getMenu()
					.getNombre());

			tvPrecioUnitario.setText(res
					.getString(R.string.item_articulo_precio)
					+ Float.toString((detalleComanda.getPrecio())));
			tvPrecio.setText(res.getString(R.string.item_articulo_precio)
					+ Float.toString((detalleComanda.getPrecio() * detalleComanda
							.getCantidad())));
			tvCantidad.setText(Integer.toString(detalleComanda.getCantidad()));

			// Obtengo el linearlayout principal.
			LinearLayout llPrincipal = (LinearLayout) convertView
					.findViewById(R.id.itemMenuComandaLlPrincipal);

			// Generar dinamicamente los platos.
			for (PlatoComanda platoComanda : detalleComanda.getMenuComanda()
					.getPlatos()) {
				LinearLayout llPlatosComanda = new LinearLayout(this.context);

				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						1.0f);

				// Creo los textview
				TextView tvNombrePlato = new TextView(context);
				tvNombrePlato.setText(platoComanda.getNombre());

				TextView tvCantidadPlato = new TextView(context);
				tvCantidadPlato.setText(Integer.toString(platoComanda
						.getCantidad()));

				tvNombrePlato.setLayoutParams(param);
				tvCantidadPlato.setLayoutParams(param);

				// Añado los textview al layout creado
				llPlatosComanda.addView(tvNombrePlato);
				llPlatosComanda.addView(tvCantidadPlato);

				// Si el estado es enviado cocina, creo el boton terminar cocina
				if (platoComanda.getEstado().equals(
						GestionComanda.ESTADO_ENVIADO_COCINA)) {
					Button btTerminarCocina = new Button(context);
					btTerminarCocina.setLayoutParams(param);
					llPlatosComanda.addView(btTerminarCocina);
					btTerminarCocina.setText(res
							.getString(R.string.item_comanda_terminar));
					btTerminarCocina.setTag(platoComanda);
					btTerminarCocina.setOnClickListener(oclTerminarPlato);
				}

				// Añado el layout creado al layout principal, en la parte de
				// abajo.
				LinearLayout.LayoutParams paramLayoutPrincipal = (LayoutParams) llPlatosComanda
						.getLayoutParams();

				paramLayoutPrincipal.gravity = Gravity.BOTTOM;

				llPrincipal.addView(llPlatosComanda, paramLayoutPrincipal);
			}

			break;
		}

		return convertView;
	}

	private JSONObject enviarTerminarPlato(int idComandaMenu)
			throws IOException, JSONException {
		JSONObject respJSON = null;

		String url;

		url = new String(LoginActivity.SITE_URL
				+ "/api/comandas/terminarPlatoComanda/format/json");

		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			String json = "";

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(GestionComanda.JSON_ID_COMANDA_MENU,
					idComandaMenu);

			jsonObject.addProperty(GestionArticulo.JSON_ID_LOCAL,
					LoginActivity.getLocal(context));

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

	public class TerminarPlato extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idComandaMenu;

		@Override
		protected void onPreExecute() {
			Resources res = context.getResources();
			progress = ProgressDialog.show(context, "",
					res.getString(R.string.progress_dialog_pedido));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			String mensaje = "";
			this.idComandaMenu = params[0];
			Resultado res = null;
			try {
				respJSON = enviarTerminarPlato(this.idComandaMenu);

				boolean operacionOk = respJSON
						.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
						: true;

				mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

				if (operacionOk) {

					// Si la operacion ha ido ok se guarda en la bbdd del movil
					Comanda comanda = GestionComanda
							.comandaJson2Comanda(respJSON
									.getJSONObject(GestionComanda.JSON_COMANDA));

					GestionComanda gestor = new GestionComanda(context);
					gestor.guardarComanda(comanda);
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

				Toast.makeText(context, resultado.getMensaje(),
						Toast.LENGTH_LONG).show();

				if (resultado.getCodigo() == 1) {
					FragmentManager fragmentManager = ((FragmentActivity) context)
							.getSupportFragmentManager();

					Detalle detalleFragment = (Detalle) fragmentManager
							.findFragmentById(R.id.detalleComandaFl);

					detalleFragment.actualizarDetalle();

				}
			}

			progress.dismiss();
		}
	}

}
