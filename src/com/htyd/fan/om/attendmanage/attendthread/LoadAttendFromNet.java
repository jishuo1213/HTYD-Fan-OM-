package com.htyd.fan.om.attendmanage.attendthread;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.htyd.fan.om.attendmanage.fragment.AttendCalendarNewFragment;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;

public class LoadAttendFromNet implements Runnable {

	private int year;
	private int month;
	private Context context;
	private Handler handler;
	
	
	public LoadAttendFromNet(Context context,int year, int month,Handler handler) {
		super();
		this.year = year;
		this.month = month;
		this.context = context;
		this.handler = handler;
	}

	
	@Override
	public void run() {
		JSONObject json = new JSONObject();
		try {
			if (month > 9) {
				json.put("QDRQ", year + "-" + (month));
			} else {
				json.put("QDRQ", year + "-0" + (month));
			}
			json.put("YHID", Preferences.getUserinfo(context, "YHID"));
			String result = NetOperating.getResultFromNet(context, json,Urls.SAVEATTENDURL, "Operate=getAllKqxxByyhidAndqdrq");
			handler.sendMessage(handler.obtainMessage(AttendCalendarNewFragment.NETSUCCESS, result));
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(AttendCalendarNewFragment.NETFAILED));
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(AttendCalendarNewFragment.NETFAILED));
		}
	}
	
}
