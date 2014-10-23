package com.brymm.brymmapp;

import android.app.Application;
import android.content.Context;

public class Brymmapp extends Application {

	private static Context context;

	public void onCreate() {
		super.onCreate();
		Brymmapp.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return Brymmapp.context;
	}

}
