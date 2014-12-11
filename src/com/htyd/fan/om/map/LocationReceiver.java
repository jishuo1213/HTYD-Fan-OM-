package com.htyd.fan.om.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.util.ui.UItoolKit;

public class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		OMLocationBean loc = intent
				.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		if (loc != null) {
			if (loc.result == BDLocation.TypeGpsLocation) {
				Log.i("fanjishuo____onReceive", "GPS_PROVIDER");
				onGPSLocationReceived(context, loc);
			} else if (loc.result == BDLocation.TypeNetWorkLocation) {
				Log.i("fanjishuo____onReceive", "NETWORK_PROVIDER");
				onNetWorkLocationReceived(context, loc);
			} else {
				UItoolKit.showToastShort(context, "网络连接失败");
			}
			return;
		}

		if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
			boolean enabled = intent.getBooleanExtra(
					LocationManager.KEY_PROVIDER_ENABLED, false);
			onProviderEnabledChanged(enabled);
		}

	}

	protected void onProviderEnabledChanged(boolean enabled) {
	}

	protected void onNetWorkLocationReceived(Context context, OMLocationBean loc) {
	}

	protected void onGPSLocationReceived(Context context, OMLocationBean loc) {

	}
}
