package com.htyd.fan.om.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

public class LocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Location loc = intent
				.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		if (loc != null) {
			onLocationReceived(context, loc);
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

	protected void onLocationReceived(Context context, Location loc) {
	}

}
