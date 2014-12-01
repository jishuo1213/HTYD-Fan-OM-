package com.htyd.fan.om.util.base;

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
		return sp.getString(USERNAME, null);
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
}
