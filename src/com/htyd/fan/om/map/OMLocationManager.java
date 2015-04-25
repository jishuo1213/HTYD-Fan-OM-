package com.htyd.fan.om.map;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.htyd.fan.om.model.OMLocationBean;

public class OMLocationManager {

	public static final String ACTION_LOCATION = "com.htyd.fan.om.map.ACTION_LOCATION";

	private static OMLocationManager sLocationManager;
	private Context mAppContext;
	LocationClient mLocClient;
	private MyLocationListener myListener = new MyLocationListener();

	private OMLocationManager(Context appContext) {
		mAppContext = appContext;
		mLocClient = new LocationClient(appContext);
	}

	public static OMLocationManager get(Context c) {
		if (sLocationManager == null) {
			sLocationManager = new OMLocationManager(c.getApplicationContext());
		}
		return sLocationManager;
	}

	public void setLocCilentOption(LocationClientOption lco) {
		if (lco == null) {
			lco = new LocationClientOption();
			lco.setOpenGps(true);// 打开gps
			lco.setCoorType("bd09ll"); // 设置坐标类型
			lco.setIsNeedAddress(true);
			lco.setLocationMode(LocationMode.Battery_Saving);
			mLocClient.setLocOption(lco);
			return;
		}
		mLocClient.setLocOption(lco);
	}

	public void startLocationUpdate() {
		mLocClient.registerLocationListener(myListener);
		mLocClient.start();
	}

	public void stopLocationUpdate() {
		if (mLocClient.isStarted()) {
			mLocClient.unRegisterLocationListener(myListener);
			mLocClient.stop();
		}
	}

	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			Log.i("fanjishuo____onReceiveLocation", "receive");
			OMLocationBean mBean = new OMLocationBean();
			mBean.setValue(arg0);
			Intent broadcast = new Intent(ACTION_LOCATION);
			broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, mBean);
			mAppContext.sendBroadcast(broadcast);
		}
	}
}
