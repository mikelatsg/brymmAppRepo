package com.brymm.brymmapp.local;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.fragments.ListaArticulosFragment;
import com.brymm.brymmapp.menu.MenuLocal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ArticulosActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_articulos);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.local, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (MenuLocal.gestionMenu(item.getItemId(), this)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			ListaArticulosFragment listaFragment = (ListaArticulosFragment) getSupportFragmentManager()
					.findFragmentById(R.id.listaArticulosFr);
			listaFragment.actualizarLista();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
