package com.htyd.fan.om.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.baidu.location.BDLocation;
import com.htyd.fan.om.model.OMLocationBean;

public abstract class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		OMLocationBean loc = intent
				.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		if (loc != null) {
			if (loc.result == BDLocation.TypeGpsLocation) {
				onGPSLocationReceived(context, loc);
			} else if (loc.result == BDLocation.TypeNetWorkLocation) {
				onNetWorkLocationReceived(context, loc);
			} else {
				onNetDisableReceived(context);
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
	
	protected abstract void onNetDisableReceived(Context context);
	
	protected void onGPSLocationReceived(Context context, OMLocationBean loc) {

	}
}
