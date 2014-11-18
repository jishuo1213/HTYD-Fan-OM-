package com.htyd.fan.om.map;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

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

	private PendingIntent getLocationPendingIntent(boolean shouldCreate,
			String provider) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
		if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
			return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
		} else {
			Log.i("fanjishuo____getLocationPendingIntent", provider);
			return PendingIntent.getBroadcast(mAppContext, 1, broadcast, flags);
		}
	}

	public void startLocationUpdates() {
		String providerNet = LocationManager.NETWORK_PROVIDER;
		String providerGps = LocationManager.GPS_PROVIDER;

		if (!mLocationManager.isProviderEnabled(providerGps)) {
		}

		if (!mLocationManager.isProviderEnabled(providerNet)) {
		}

		Location lastKnown = mLocationManager.getLastKnownLocation(providerNet);
		if (lastKnown != null) {
//			broadcastLocation(lastKnown);
		}

		PendingIntent piNet = getLocationPendingIntent(true, providerNet);
		Log.i("fanjishuo____startLocationUpdates", "start");
		PendingIntent piGps = getLocationPendingIntent(true, providerGps);
		mLocationManager.requestSingleUpdate(providerNet, piNet);
		mLocationManager.requestLocationUpdates(providerGps, 0, 0, piGps);
	}

/*	private void broadcastLocation(Location lastKnown) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, lastKnown);
		mAppContext.sendBroadcast(broadcast);
	}*/

	public void stopNetWorkLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false,
				LocationManager.NETWORK_PROVIDER);
		if (pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}

	public void stopGPSLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false,
				LocationManager.GPS_PROVIDER);
		if (pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}

	public boolean isNetTracking() {
		return getLocationPendingIntent(false, LocationManager.NETWORK_PROVIDER) != null;
	}

	public boolean isGpsTracking() {
		return getLocationPendingIntent(false, LocationManager.GPS_PROVIDER) != null;
	}
	/*
	 * private class MyLocationListner implements LocationListener{
	 * 
	 * @Override public void onLocationChanged(Location location) { // Called
	 * when a new location is found by the location provider. Log.v("GPSTEST",
	 * "Got New Location of provider:"+location.getProvider());
	 * if(currentLocation!=null){ if(isBetterLocation(location,
	 * currentLocation)){ Log.v("GPSTEST", "It's a better location");
	 * currentLocation=location; showLocation(location); } else{
	 * Log.v("GPSTEST", "Not very good!"); } } else{ Log.v("GPSTEST",
	 * "It's first location"); currentLocation=location; showLocation(location);
	 * } //移除基于LocationManager.NETWORK_PROVIDER的监听器
	 * if(LocationManager.NETWORK_PROVIDER.equals(location.getProvider())){
	 * locationManager.removeUpdates(this); } }
	 * 
	 * //后3个方法此处不做处理 public void onStatusChanged(String provider, int status,
	 * Bundle extras) { }
	 * 
	 * public void onProviderEnabled(String provider) { }
	 * 
	 * public void onProviderDisabled(String provider) { } }; Location
	 * currentLocation; private void showLocation(Location location){ //纬度
	 * Log.v("GPSTEST","Latitude:"+location.getLatitude()); //经度
	 * Log.v("GPSTEST","Longitude:"+location.getLongitude()); //精确度
	 * Log.v("GPSTEST","Accuracy:"+location.getAccuracy());
	 * //Location还有其它属性，请自行探索 } private static final int CHECK_INTERVAL = 1000 *
	 * 30; protected boolean isBetterLocation(Location location, Location
	 * currentBestLocation) { if (currentBestLocation == null) { // A new
	 * location is always better than no location return true; }
	 * 
	 * // Check whether the new location fix is newer or older long timeDelta =
	 * location.getTime() - currentBestLocation.getTime(); boolean
	 * isSignificantlyNewer = timeDelta > CHECK_INTERVAL; boolean
	 * isSignificantlyOlder = timeDelta < -CHECK_INTERVAL; boolean isNewer =
	 * timeDelta > 0;
	 * 
	 * // If it's been more than two minutes since the current location, // use
	 * the new location // because the user has likely moved if
	 * (isSignificantlyNewer) { return true; // If the new location is more than
	 * two minutes older, it must // be worse } else if (isSignificantlyOlder) {
	 * return false; }
	 * 
	 * // Check whether the new location fix is more or less accurate int
	 * accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
	 * .getAccuracy()); boolean isLessAccurate = accuracyDelta > 0; boolean
	 * isMoreAccurate = accuracyDelta < 0; boolean isSignificantlyLessAccurate =
	 * accuracyDelta > 200;
	 * 
	 * // Check if the old and new location are from the same provider boolean
	 * isFromSameProvider = isSameProvider(location.getProvider(),
	 * currentBestLocation.getProvider());
	 * 
	 * // Determine location quality using a combination of timeliness and //
	 * accuracy if (isMoreAccurate) { return true; } else if (isNewer &&
	 * !isLessAccurate) { return true; } else if (isNewer &&
	 * !isSignificantlyLessAccurate && isFromSameProvider) { return true; }
	 * return false; }
	 *//** Checks whether two providers are the same */
	/*
	 * private boolean isSameProvider(String provider1, String provider2) { if
	 * (provider1 == null) { return provider2 == null; } return
	 * provider1.equals(provider2); }
	 */

}
