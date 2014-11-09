package com.brymm.brymmapp.usuario;

import java.util.Calendar;

import com.brymm.brymmapp.LoginActivity;
import com.brymm.brymmapp.R;
import com.brymm.brymmapp.servicios.ServicioActualizacionUsuario;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.Menu;

public class HomeUsuarioActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_usuario);
		pararActualizacionDatos();
		arrancarActualizacionDatos();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.usuario, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// Paro el servicio si no se guarda la sesion
		if (!LoginActivity.getSessionLocal(this)) {
			this.pararActualizacionDatos();
		}
		super.onDestroy();
	}

	public void arrancarActualizacionDatos() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 10);

		Intent myIntent = new Intent(this, ServicioActualizacionUsuario.class);		
		PendingIntent pendingIntent= PendingIntent.getService(this,
				111111, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);		

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
				60 * 1000, pendingIntent);
	}

	public void pararActualizacionDatos() {

		Intent stopIntent = new Intent(this, ServicioActualizacionUsuario.class);
		PendingIntent stopFriday = PendingIntent.getService(this, 123098,
				stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager stopManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		stopManager.cancel(stopFriday);
	}
}
