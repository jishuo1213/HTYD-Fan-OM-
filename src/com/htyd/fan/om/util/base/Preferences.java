package com.htyd.fan.om.util.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

public class Preferences {
	
	private static final String PREFERENCENAME = "om.preference";
	private static final String LASTLOCATIONLATITUDE = "lastlocationlatitude";
	private static final String LASTLOCATIONLONGITUDE = "lastlocationlongtitude";
	
	private static final String USERID = "userid";
	private static final String USERNAME = "username";
	
	private static SharedPreferences sp;
	private static Editor editor;
	
	
	public static void setLastLocation( Context context,Location loc){
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
	
	public static void setUserId(Context context, int UserId){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putInt(USERID, UserId);
		editor.apply();
	}
	
	public static int getUserId(Context context){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		Log.i("fanjishuo____getUserId", sp.getInt(USERID, -1)+"");
		return sp.getInt(USERID, -1);
	}
	
	public static void setUserName(Context context, String name){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(USERNAME, name);
		editor.apply();
	}
	
	public static String getUserName(Context context){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sp.getString(USERNAME, null);
	}
}
