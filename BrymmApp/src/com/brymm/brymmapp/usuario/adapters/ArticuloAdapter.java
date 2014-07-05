package com.brymm.brymmapp.usuario.adapters;

import java.util.List;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocal;
import com.brymm.brymmapp.usuario.pojo.ArticuloLocalLista;
import com.brymm.brymmapp.usuario.pojo.Local;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ArticuloAdapter extends ArrayAdapter<ArticuloLocalLista> {
	private Context context;
	private int textViewResourceId;
	private List<ArticuloLocalLista> articulos;

	private TextView tvNombre, tvDescripcion, tvIngredientes;
	private EditText etCantidad;
	private Button bAnadir;

	public ArticuloAdapter(Context context, int textViewResourceId,
			List<ArticuloLocalLista> articulos) {
		super(context, textViewResourceId, articulos);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.articulos = articulos;
	}

	private void inicializar(View convertView) {
		tvNombre = (TextView) convertView
				.findViewById(R.id.itemArticuloTvNombre);
		tvDescripcion = (TextView) convertView
				.findViewById(R.id.itemArticuloTvDescripcion);
		tvIngredientes = (TextView) convertView
				.findViewById(R.id.itemArticuloTvIngredientes);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ArticuloLocalLista articulo = articulos.get(position);

		if (articulo.isSeccion()) {

			view = li.inflate(R.layout.section_item, null);
			// convertView = li.inflate(R.layout.section_item, parent, false);

			view.setOnClickListener(null);
			view.setOnLongClickListener(null);
			view.setLongClickable(false);

			final TextView sectionView = (TextView) view
					.findViewById(R.id.itemSectionTvSeccion);
			sectionView.setText(articulo.getTipoArticulo());
		} else {

			// Obtener un objeto a partir del layout (xml)
			view = li.inflate(textViewResourceId, parent, false);

			inicializar(view);
			Resources res = view.getResources();

			tvNombre.setText(articulo.getNombre());
			tvDescripcion.setText(articulo.getDescripcion());
			String ingredientesString = res
					.getString(R.string.anadir_articulo_ingredientes);
			boolean hayIng = false;
			for (String ingrediente : articulo.getIngredientes()) {
				ingredientesString += ingrediente;
				ingredientesString += ", ";
			}
			if (articulo.getIngredientes().size() == 0) {
				tvIngredientes.setVisibility(View.GONE);
			} else {
				tvIngredientes.setText(ingredientesString);
			}
			tvIngredientes.setText(ingredientesString);

		}
		return view;
	}
}
