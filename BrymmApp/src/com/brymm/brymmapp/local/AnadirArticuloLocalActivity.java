package com.brymm.brymmapp.local;

import com.brymm.brymmapp.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class AnadirArticuloLocalActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anadir_articulo_local);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.anadir_articulo_local, menu);
		return true;
	}		

}
