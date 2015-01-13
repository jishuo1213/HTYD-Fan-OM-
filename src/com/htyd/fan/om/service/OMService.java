package com.htyd.fan.om.service;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskAccessoryCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;

public class OMService extends IntentService {

	private static final int THREADNUM = 2;
	
	private ArrayList <AffiliatedFileBean> affiliatedFileList;
	private UpLoadThread [] threads;
	private UpLoadFileHandler [] handlers;
	private OMUserDatabaseManager mManager;
	private UpLoadFileHandler handler;
	
	public OMService() {
		super("OMService");
		mManager = OMUserDatabaseManager.getInstance(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		TaskAccessoryCursor cursor = (TaskAccessoryCursor) mManager.queryUnLoadAccessory();
		if (cursor.getCount() == 0 || cursor == null) {
			return;
		}
		cursor.moveToFirst();
		int num = cursor.getCount();
		affiliatedFileList = new ArrayList<AffiliatedFileBean>(num);
		do {
			affiliatedFileList.add(cursor.getAccessory());
		} while (cursor.moveToNext());
		UpLoadThread thread = new UpLoadThread();
		thread.setParam(affiliatedFileList.get(0).filePath,handler,0);
		thread.start();
	}

	@SuppressLint("HandlerLeak")
	private  class UpLoadFileHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case UpLoadThread.MESSAGEWHAT:
				Bundle bundle = (Bundle) msg.obj;
				String result = bundle.getString(UpLoadThread.SERVERRES);
				if(result.length() == 0 ){
					UpLoadThread thread = new UpLoadThread();
					thread.setParam(affiliatedFileList.get(0).filePath, handler, 0);
					thread.run();
					return;
				}
				try {
					JSONObject json  = new JSONObject(result);
					if(json.getBoolean("RESULT")){
						int num = bundle.getInt(UpLoadThread.THREADNUM);
						AffiliatedFileBean mBean = affiliatedFileList.get(num);
						mBean.fileState = 1;
						mManager.updateUploadAccessoryBean(mBean);
						affiliatedFileList.remove(num);
					}
					if(affiliatedFileList.size() > 0){
						UpLoadThread thread = new UpLoadThread();
						thread.setParam(affiliatedFileList.get(0).filePath, handler, 0);
						thread.run();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
