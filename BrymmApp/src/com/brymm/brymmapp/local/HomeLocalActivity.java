package com.brymm.brymmapp.local;

import java.util.Calendar;

import com.brymm.brymmapp.Brymmapp;
import com.brymm.brymmapp.InicioActivity;
import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.bbdd.GestionArticulo;
import com.brymm.brymmapp.menu.MenuLocal;
import com.brymm.brymmapp.servicios.ServicioActualizacionLocal;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;

public class HomeLocalActivity extends Activity {

	private static final int REQUEST_CODE_ACT_LOCAL = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_local);
		pararActualizacionDatos();
		arrancarActualizacionDatos(LoginActivity.getLocal(this));
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
	protected void onDestroy() {
		// Paro el servicio si no se guarda la sesion
		if (!LoginActivity.getSessionLocal(this)) {
			this.pararActualizacionDatos();
		}
		super.onDestroy();
	}

	public void arrancarActualizacionDatos(int idLocal) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 10);

		Intent myIntent = new Intent(this, ServicioActualizacionLocal.class);
		PendingIntent pendingIntent = PendingIntent.getService(this,
				REQUEST_CODE_ACT_LOCAL, myIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
				60 * 1000, pendingIntent);
	}

	public void pararActualizacionDatos() {

		Intent stopIntent = new Intent(this, ServicioActualizacionLocal.class);
		PendingIntent stopFriday = PendingIntent.getService(this,
				REQUEST_CODE_ACT_LOCAL, stopIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager stopManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		stopManager.cancel(stopFriday);
	}
}
