package com.brymm.brymmapp.local;

import com.brymm.brymmapp.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class DetalleComandaActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalle_comanda);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
