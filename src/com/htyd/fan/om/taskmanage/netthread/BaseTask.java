package com.htyd.fan.om.taskmanage.netthread;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcelable;

import com.htyd.fan.om.util.https.NetOperating;

public abstract class BaseTask<T extends Parcelable> extends AsyncTask<T, Void, Boolean> {
	
	protected abstract JSONObject getParams(T params);
	protected abstract String getUrl();
	protected abstract String getOperate();
	protected abstract boolean praseResult(String result);
	
	private Context context;
	
	public BaseTask(Context context) {
		super();
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(@SuppressWarnings("unchecked") T... params) {
		try {
			String result = NetOperating.getResultFromNet(context, getParams(params[0]), getUrl(), getOperate());
			return praseResult(result);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {

		} else {

		}
	}
}
