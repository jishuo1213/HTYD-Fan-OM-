package com.htyd.fan.om.util.netthread;

import org.json.JSONException;
import org.json.JSONObject;

import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;

import android.content.Context;
import android.os.Handler;

public class ModifyPersonalInfoThread extends Thread {

	private Handler handler;
	private String userName;
	private String userPhone;
	private Context context;
	private Runnable successRunnable,failRunnable;
	
	public ModifyPersonalInfoThread(Handler handler, String userName,
			String userPhone, Context context) {
		this.handler = handler;
		this.userName = userName;
		this.userPhone = userPhone;
		this.context = context;
	}

	@Override
	public void run() {
		JSONObject param = new JSONObject();
		String result = null;
		try {
			param.put("YHID", Preferences.getUserinfo(context, "YHID"));
			param.put("YHMC", userName);
			param.put("SHOUJ", userPhone);
			param.put("RZ_MKID", 18);
			result = NetOperating.getResultFromNet(context, param, Urls.USERURL, "Operate=updateYhxxPhone");
		} catch (Exception e) {
			e.printStackTrace();
		}
		parseResult(result);
	}

	public void setRunnable(Runnable successRunnable,Runnable failRunnable){
		this.successRunnable = successRunnable;
		this.failRunnable = failRunnable;
	}
	
	private void parseResult(String result) {
		if(result == null){
			handler.post(failRunnable);
			return;
		}
		try {
			JSONObject json = new JSONObject(result);
			if (json.getBoolean("RESULT")) {
				handler.post(successRunnable);
			} else {
				handler.post(failRunnable);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
}
