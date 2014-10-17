package com.brymm.brymmapp.local;


import com.brymm.brymmapp.R;



import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class AnadirModificarIngredienteActivity extends FragmentActivity {

	public static final String EXTRA_INGREDIENTE = "extraIngrediente";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anadir_modificar_ingrediente);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
