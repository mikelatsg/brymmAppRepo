package com.brymm.brymmapp;

import java.util.Calendar;

import com.brymm.brymmapp.local.HomeLocalActivity;
import com.brymm.brymmapp.servicios.ServicioActualizacionLocal;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class InicioActivity extends Activity {	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio);		
		irLogin();
	}

	private void irLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

}
