package com.htyd.fan.om.util.netthread;

import org.json.JSONException;
import org.json.JSONObject;

import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;

import android.content.Context;
import android.os.Handler;

public class ModifyPasswordThread extends Thread {

	private Handler handler;
	private String oldPassword,newPassword;
	private Runnable successRunnable,failRunnable;
	private Context context;
	
	
	public ModifyPasswordThread(Handler handler,Context context,String oldPassword,String newPassword) {
		super();
		this.handler = handler;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.context = context;
	}


	@Override
	public void run() {
		JSONObject param = new JSONObject();
		String result = null;
		try {
			param.put("DLZH", Preferences.getUserinfo(context, "DLZH"));
			param.put("DLMM", oldPassword);
			param.put("XMM", newPassword);
			param.put("RZ_MKID", 13);
			result =  NetOperating.getResultFromNet(context, param, Urls.USERURL, "Operate=updateDlmmByDlzh");
		} catch (Exception e) {
			e.printStackTrace();
		}
		praseResult(result);
	}

	private void praseResult(String result) {
		if (result == null) {
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
			handler.post(failRunnable);
		}
	}


	public void setRunnable(Runnable successRunnable,Runnable failRunnable){
		this.successRunnable = successRunnable;
		this.failRunnable = failRunnable;
	}
	
}
