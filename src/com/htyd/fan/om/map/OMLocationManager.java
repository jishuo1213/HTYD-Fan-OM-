package com.htyd.fan.om.map;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

public class OMLocationManager {

	public static final String ACTION_LOCATION = "com.htyd.fan.om.map.ACTION_LOCATION";

	private static OMLocationManager sLocationManager;
	private Context mAppContext;
	private LocationManager mLocationManager;

	private OMLocationManager(Context appContext) {
		mAppContext = appContext;
		mLocationManager = (LocationManager) mAppContext
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public static OMLocationManager get(Context c) {
		if (sLocationManager == null) {
			sLocationManager = new OMLocationManager(c.getApplicationContext());
		}
		return sLocationManager;
	}

	private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
		Intent broadcasrt = new Intent(ACTION_LOCATION);
		int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
		return PendingIntent.getBroadcast(mAppContext, 0, broadcasrt, flags);
	}

	public void startLocationUpdates() {
		String provider = LocationManager.NETWORK_PROVIDER;

		Location lastKnown = mLocationManager.getLastKnownLocation(provider);
		
		PendingIntent pi = getLocationPendingIntent(true);
		mLocationManager.requestLocationUpdates(provider, 1000, 0, pi);
	}

	public void stopLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false);
		if (pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}

	public boolean isTracking() {
		return getLocationPendingIntent(false) != null;
	}
}
