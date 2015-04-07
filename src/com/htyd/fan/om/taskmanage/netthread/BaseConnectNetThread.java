package com.htyd.fan.om.taskmanage.netthread;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;

import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.ui.UItoolKit;

public abstract class BaseConnectNetThread <T extends Parcelable> extends Thread {
	
	protected abstract JSONObject getParams(T params);
	protected abstract String getUrl();
	protected abstract String getOperate();
	protected abstract boolean praseResult(String result);
	
	private Handler handler;
	protected Context context;
	private T param;
	
	public BaseConnectNetThread(Handler handler, Context context,T param) {
		super();
		this.handler = handler;
		this.context = context;
		this.param = param;
	}
	
	@Override
	public void run() {
		try {
			String result = NetOperating.getResultFromNet(context, getParams(param), getUrl(), getOperate());
			boolean success = praseResult(result);
			if (success) {
				handler.post(getSuccessRunnable());
			} else {
				handler.post(getFailRunnable());
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.post(getFailRunnable());
		}
	}
	
	public Runnable getSuccessRunnable(){
		return new Runnable() {
			@Override
			public void run() {
				UItoolKit.showToastShort(context, "保存至网络成功");
			}
		};
	}
	
	public Runnable getFailRunnable(){
		return new Runnable() {
			@Override
			public void run() {
				UItoolKit.showToastShort(context, "保存至网络失败");
			}
		};
	}
}
