package com.htyd.fan.om.service;

import android.app.IntentService;
import android.content.Intent;

import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskAccessoryCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;

public class OMService extends IntentService {

	
	
	public OMService() {
		super("OMService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		TaskAccessoryCursor cursor = (TaskAccessoryCursor) OMUserDatabaseManager
				.getInstance(this).queryUnLoadAccessory();
		if(cursor.getCount() > 0){
			
		}
	}

}
