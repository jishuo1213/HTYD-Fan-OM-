package com.htyd.fan.om.util.base;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {

	private static final String PREFERENCENAME = "om.preference";
	private static final String ISAUTOLOGIN = "isautologin";

	private static final String USERID = "userid";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ACCOUNT = "account";
	private static final String REMBERPWD = "remberpwd";
	private static final String USERINFO = "userinformation";
	private static final String SERVERURL = "serverurl";

	private static SharedPreferences sp;
	private static Editor editor;

	public static void setUserId(Context context, String UserId) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(USERID, UserId);
		editor.apply();
	}

	public static String getUserId(Context context) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sp.getString(USERID, "");
	}

	public static void setUserName(Context context, String name) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(USERNAME, name);
		editor.apply();
	}

	public static String getUserName(Context context) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sp.getString(USERNAME, "");
	}

	public static boolean getIsAutoLogin(Context context) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sp.getBoolean(ISAUTOLOGIN, false);
	}

	public static void setAutoLogin(Context context, boolean isAutoLogin) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putBoolean(ISAUTOLOGIN, isAutoLogin);
		editor.apply();
	}

	public static String getLastLoginPassword(Context context) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return DES.decryptDES(sp.getString(PASSWORD, ""),"19911213");
	}

	public static void setLastLoginPassword(Context context, String password) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(PASSWORD, password);
		editor.apply();
	}

	public static String getLastLoginAccount(Context context) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sp.getString(ACCOUNT, "");
	}

	public static void setLastLoginAccount(Context context, String account) {
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(ACCOUNT, account);
		editor.apply();
	}
	
	public static void setRememberPwd(Context context,boolean isrember){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putBoolean(REMBERPWD, isrember);
		editor.apply();
	}
	
	public static boolean getIsRemPwd(Context context){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sp.getBoolean(REMBERPWD, false);
	}
	
	public static void setUserInfo(Context context, String userInfo){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(USERINFO, userInfo);
		editor.apply();
	}
	
	public static String getUserinfo(Context context,String key){
		String userInfo = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE).getString(USERINFO, "");
		if(userInfo.length() == 0){
			return "";
		}
		try {
			JSONObject json = new JSONObject(userInfo);
			return json.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static void setServerAddress(Context context, String address){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(SERVERURL, "http://"+address+":8080/zdyw/");
		editor.apply();
	}
	
	public static String getServerAddress(Context context){
		sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
		return sp.getString(SERVERURL, "");
	}
}
