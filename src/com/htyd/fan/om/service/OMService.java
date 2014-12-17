package com.htyd.fan.om.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OMService extends Service {

	
	
	@Override
	public IBinder onBind(Intent intent) {
		switch(intent.getAction()){
		
		}
		return null;
	}

}
