package com.brymm.brymmapp.local;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.R.layout;
import com.brymm.brymmapp.R.menu;
import com.brymm.brymmapp.menu.MenuLocal;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public class HomeLocalActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_local);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.local, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (MenuLocal.gestionMenu(item.getItemId(), this)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
