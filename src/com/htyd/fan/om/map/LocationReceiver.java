package com.htyd.fan.om.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Location loc = intent
				.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		Log.i("fanjishuo____onReceive", "onReceive");
		if (loc != null) {
			if (loc.getProvider().equals(LocationManager.GPS_PROVIDER)) {
				Log.i("fanjishuo____onReceive", "GPS_PROVIDER");
				onGPSLocationReceived(context, loc);
			} else if (loc.getProvider().equals(
					LocationManager.NETWORK_PROVIDER)) {
				Log.i("fanjishuo____onReceive", "NETWORK_PROVIDER");
				onNetWorkLocationReceived(context, loc);
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

	protected void onNetWorkLocationReceived(Context context, Location loc) {
	}

	protected void onGPSLocationReceived(Context context, Location loc) {

	}
}
