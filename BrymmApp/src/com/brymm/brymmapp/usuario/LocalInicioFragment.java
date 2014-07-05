package com.brymm.brymmapp.usuario;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.bbdd.GestionDireccion;
import com.brymm.brymmapp.usuario.bbdd.GestionLocalFavorito;
import com.brymm.brymmapp.usuario.pojo.Direccion;
import com.brymm.brymmapp.usuario.pojo.Local;
import com.brymm.brymmapp.usuario.pojo.LocalFavorito;
import com.brymm.brymmapp.util.Resultado;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;
import android.widget.Toast;

public class LocalInicioFragment extends Fragment {

	private TextView tvNombre, tvTipoComida, tvDireccion, tvTelefono,
			tvLocalidad, tvFavorito;

	private OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			NuevoFavorito nf = new NuevoFavorito();
			int idLocal = (Integer) tvFavorito.getTag();
			nf.execute(idLocal);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_local_inicio, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		inicializar();
		super.onActivityCreated(savedInstanceState);
	}

	private void inicializar() {
		tvNombre = (TextView) getActivity().findViewById(
				R.id.mostrarLocalTvNombre);
		tvTipoComida = (TextView) getActivity().findViewById(
				R.id.mostrarLocalTvTipoComida);
		tvDireccion = (TextView) getActivity().findViewById(
				R.id.mostrarLocalTvDireccion);
		tvTelefono = (TextView) getActivity().findViewById(
				R.id.mostrarLocalTvTelefono);
		tvLocalidad = (TextView) getActivity().findViewById(
				R.id.mostrarLocalTvLocalidad);
		tvFavorito = (TextView) getActivity().findViewById(
				R.id.mostrarLocalTvFavorito);

		Intent intent = getActivity().getIntent();
		Local local = intent
				.getParcelableExtra(MostrarLocalActivity.EXTRA_LOCAL);

		tvNombre.setText(local.getNombre());
		tvTipoComida.setText(local.getTipoComida());
		tvDireccion.setText(local.getDireccion());
		tvTelefono.setText(local.getTelefono());
		tvLocalidad.setText(local.getLocalidad());
		GestionLocalFavorito glf = new GestionLocalFavorito(getActivity());
		if (glf.comprobarEsFavorito(local.getIdLocal())) {
			tvFavorito.setVisibility(View.GONE);
		} else {
			tvFavorito.setTag(local.getIdLocal());
			tvFavorito.setOnClickListener(ocl);
		}
		glf.cerrarDatabase();

	}

	private JSONObject anadirFavorito(int idLocal, int idUsuario) {
		String respStr;
		JSONObject respJSON = null;
		HttpClient httpClient = new DefaultHttpClient();

		try {
			String url = LoginActivity.SITE_URL
					+ "/api/usuarios/anadirFavorito/idLocal/" + idLocal
					+ "/idUsuario/" + idUsuario + "/format/json";

			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("content-type", "application/json");
			HttpResponse resp = httpClient.execute(httpGet);

			/*
			 * Si el codigo de retorno es diferente de 200 se devuelve el objeto
			 * nulo
			 */
			if (resp.getStatusLine().getStatusCode() == LoginActivity.CODE_LOGIN_OK) {
				respStr = EntityUtils.toString(resp.getEntity());
				Log.d("res", respStr);
				respJSON = new JSONObject(respStr);
			}

		} catch (Exception ex) {
			respJSON = null;
		}
		return respJSON;
	}

	public class NuevoFavorito extends AsyncTask<Integer, Void, Resultado> {

		ProgressDialog progress;
		int idLocal;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(getActivity(), "",
					res.getString(R.string.progress_dialog_realizando_operacion));
			super.onPreExecute();
		}

		@Override
		protected Resultado doInBackground(Integer... params) {
			JSONObject respJSON = null;
			int idUsuario = LoginActivity.getUsuario(getActivity());
			this.idLocal = params[0];
			String mensaje = "";
			Resultado res = null;
			try {
				respJSON = anadirFavorito(idLocal, idUsuario);

				if (respJSON != null) {

					boolean operacionOk = respJSON
							.getInt(Resultado.JSON_OPERACION_OK) == 0 ? false
							: true;

					mensaje = respJSON.getString(Resultado.JSON_MENSAJE);

					if (operacionOk) {

						LocalFavorito favorito = GestionLocalFavorito
								.favoritoJson2LocalFavorito(respJSON
										.getJSONObject(GestionLocalFavorito.JSON_LOCAL_FAVORITO));

						GestionLocalFavorito glf = new GestionLocalFavorito(
								getActivity());
						glf.guardarLocalFavorito(favorito);
						glf.cerrarDatabase();
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

			} else {
				Toast.makeText(getActivity(), R.string.toast_error_operacion,
						Toast.LENGTH_LONG).show();
			}

		}
	}

}
