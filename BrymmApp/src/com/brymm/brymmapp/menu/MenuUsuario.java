package com.brymm.brymmapp.menu;

import android.content.Context;
import android.content.Intent;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.usuario.BuscadorLocalesActivity;
import com.brymm.brymmapp.usuario.DireccionesActivity;
import com.brymm.brymmapp.usuario.LocalesFavoritosActivity;
import com.brymm.brymmapp.usuario.UltimasReservasActivity;
import com.brymm.brymmapp.usuario.UltimosPedidosActivity;

public class MenuUsuario {

	public static boolean gestionMenuUsuario(int itemId, Context context){
		switch(itemId){
		case R.id.menuUsuarioUltimosPedidos:
			MenuUsuario.irUltimosPedidos(context);
			return true;
		case R.id.menuUsuarioDirecciones:
			MenuUsuario.irDirecciones(context);
			return true;
			
		case R.id.menuUsuarioBuscador:
			MenuUsuario.irBuscador(context);
			return true;
			
		case R.id.menuUsuarioLocalesFavoritos:
			MenuUsuario.irLocalesFavoritos(context);
			return true;
		case R.id.menuUsuarioUltimasReservas:
			MenuUsuario.irUltimasReservas(context);
			return true;
		}
		return false;
	}
	
	public static void irUltimosPedidos(Context context){
		Intent intent = new Intent(context,UltimosPedidosActivity.class);
		context.startActivity(intent);
	}
	
	public static void irDirecciones(Context context){
		Intent intent = new Intent(context,DireccionesActivity.class);
		context.startActivity(intent);
	}
	
	public static void irBuscador(Context context){
		Intent intent = new Intent(context,BuscadorLocalesActivity.class);
		context.startActivity(intent);
	}
	
	public static void irLocalesFavoritos(Context context){
		Intent intent = new Intent(context,LocalesFavoritosActivity.class);
		context.startActivity(intent);
	}
	
	public static void irUltimasReservas(Context context){
		Intent intent = new Intent(context,UltimasReservasActivity.class);
		context.startActivity(intent);
	}
	
}
