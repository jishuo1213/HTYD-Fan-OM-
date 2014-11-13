package com.htyd.fan.om.util.base;

import com.baidu.mapapi.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;

public class Preferences {
	
	private static final String PREFERENCENAME = "om.preference";
	private static final String LASTLOCATIONLATITUDE = "lastlocationlatitude";
	private static final String LASTLOCATIONLONGITUDE = "lastlocationlongtitude";
	
	private static SharedPreferences sp;
	private static Editor editor;
	
	
	
	public static void setLastLocation(Context context,Location loc){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putFloat(LASTLOCATIONLATITUDE,  (float) loc.getLatitude());
		editor.putFloat(LASTLOCATIONLONGITUDE, (float) loc.getLongitude());
		editor.apply();
	}
	
	public static LatLng getLastLocation(Context context){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return new LatLng(sp.getFloat(LASTLOCATIONLATITUDE, -1),sp.getFloat(LASTLOCATIONLONGITUDE, -1));
	}
}
