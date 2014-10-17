package com.brymm.brymmapp.menu;

import android.content.Context;
import android.content.Intent;

import com.brymm.brymmapp.R;
import com.brymm.brymmapp.local.ArticulosActivity;
import com.brymm.brymmapp.local.ComandasActivity;
import com.brymm.brymmapp.local.HorariosActivity;
import com.brymm.brymmapp.local.MenusActivity;
import com.brymm.brymmapp.local.PedidosActivity;
import com.brymm.brymmapp.local.ReservasActivity;
import com.brymm.brymmapp.local.ServiciosActivity;

public class MenuLocal {

	public static boolean gestionMenu(int itemId, Context context) {
		switch (itemId) {
		case R.id.menuLocalArticulos:
			MenuLocal.irArticulos(context);
			return true;
		case R.id.menuLocalPedidos:
			MenuLocal.irPedidos(context);
			return true;
		case R.id.menuLocalServicios:
			MenuLocal.irServicios(context);
			return true;
		case R.id.menuLocalHorarios:
			MenuLocal.irHorarios(context);
			return true;
		case R.id.menuLocalReservas:
			MenuLocal.irReservas(context);
			return true;
		case R.id.menuLocalMenus:
			MenuLocal.irMenus(context);
			return true;
		case R.id.menuLocalComandas:
				MenuLocal.irComandas(context);
				return true;			
		}
		return false;
	}

	public static void irArticulos(Context context) {
		Intent intent = new Intent(context, ArticulosActivity.class);
		context.startActivity(intent);
	}

	public static void irPedidos(Context context) {
		Intent intent = new Intent(context, PedidosActivity.class);
		context.startActivity(intent);
	}

	public static void irServicios(Context context) {
		Intent intent = new Intent(context, ServiciosActivity.class);
		context.startActivity(intent);
	}

	public static void irHorarios(Context context) {
		Intent intent = new Intent(context, HorariosActivity.class);
		context.startActivity(intent);
	}

	public static void irReservas(Context context) {
		Intent intent = new Intent(context, ReservasActivity.class);
		context.startActivity(intent);
	}

	public static void irMenus(Context context) {
		Intent intent = new Intent(context, MenusActivity.class);
		context.startActivity(intent);
	}

	public static void irComandas(Context context) {
		Intent intent = new Intent(context, ComandasActivity.class);
		context.startActivity(intent);
	}

}
