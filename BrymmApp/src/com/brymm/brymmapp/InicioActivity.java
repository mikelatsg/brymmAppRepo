package com.brymm.brymmapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class InicioActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio);
		irLogin();
	}
	
	private void irLogin(){
		Intent intent = new Intent(this,LoginActivity.class);
		startActivity(intent);
	}

}
