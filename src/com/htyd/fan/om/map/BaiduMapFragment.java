package com.htyd.fan.om.map;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.htyd.fan.om.R;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.util.ui.UItoolKit;

public class BaiduMapFragment extends Fragment {

	private OMLocationManager mLocationManager;
    MapView mapView;
    LinearLayout progressLayout;
    ViewStub vs;
	LocationRecListener mListener;
	BaiduMap bMap;
	boolean isFirstLoc = true;
	BitmapDescriptor bitmap ;
	

	public interface LocationRecListener {
		public void onLocationReceiveListener(OMLocationBean loc);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = (LocationRecListener) activity;
	}

	@Override
	public void onDetach() {
		mListener = null;
		super.onDetach();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().registerReceiver(mLocationReceiver,
				new IntentFilter(OMLocationManager.ACTION_LOCATION));
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocationReceiver);
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationManager = OMLocationManager.get(getActivity());
		mLocationManager.setLocCilentOption(null);
		mLocationManager.startLocationUpdate();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.map_fragment_layout, container,
				false);
		initView(v);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mapView != null)
			mapView.onResume();
	}

	@Override
	public void onPause() {
		if (mapView != null)
			mapView.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (mapView != null)
			mapView.onDestroy();
		mLocationManager.stopLocationUpdate();
		super.onDestroy();
	}

	private void initView(View v) {
		vs = (ViewStub) v.findViewById(R.id.map_sub);
		progressLayout = (LinearLayout) v.findViewById(R.id.line_progress);
	}

	private void initMapView() {

		bMap = mapView.getMap();
		bMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		bMap.setMyLocationEnabled(true);
		MapStatus.Builder statusBuilder = new MapStatus.Builder();
		statusBuilder.zoom(25);
		MapStatus mapStatus = statusBuilder.build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mapStatus);
		bMap.setMapStatus(mapStatusUpdate);
		bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);
	}

	public void setListener(LocationRecListener listener) {
		mListener = listener;
	}

	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onProviderEnabledChanged(boolean enabled) {
		}

		@Override
		protected void onGPSLocationReceived(Context context, OMLocationBean loc) {
			UItoolKit.showToastShort(getActivity(),
					"loc.getLatitude()" + loc.latitude
							+ "loc.getLongitude()" + loc.longitude);
			if (progressLayout.isShown()) {
				progressLayout.setVisibility(View.GONE);
			}
			if (isFirstLoc) {
				mapView = (MapView) vs.inflate();
				initMapView();
			}
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(50)
			// 此处设置开发者获取到的方向信息，顺时针0-360
			.direction(loc.direction).latitude(loc.latitude)
			.longitude(loc.longitude).build();
			bMap.setMyLocationData(locData);
			if(isFirstLoc){
				isFirstLoc = false;
				LatLng ll = new LatLng(loc.latitude,
						loc.longitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				bMap.animateMapStatus(u);
			}
			mListener.onLocationReceiveListener(loc);
		}

		@Override
		protected void onNetWorkLocationReceived(Context context,
				final OMLocationBean loc) {
			Log.i("fanjishuo_____onNetWorkLocationReceived",
					"loc.getLatitude()" + loc.latitude
							+ "loc.getLongitude()" + loc.longitude);
			if (progressLayout.isShown()) {
				progressLayout.setVisibility(View.GONE);
			}
			if (isFirstLoc) {
				mapView = (MapView) vs.inflate();
				initMapView();
			}
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(10)
			// 此处设置开发者获取到的方向信息，顺时针0-360
			.direction(loc.direction).latitude(loc.latitude)
			.longitude(loc.longitude).build();
			bMap.setMyLocationData(locData);
			if(isFirstLoc){
				isFirstLoc = false;
				LatLng ll = new LatLng(loc.latitude,
						loc.longitude);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				bMap.animateMapStatus(u);
				bMap.setMyLocationConfigeration(new MyLocationConfiguration(
						LocationMode.NORMAL, true, bitmap));
			}
			mListener.onLocationReceiveListener(loc);
		}
	};
}
