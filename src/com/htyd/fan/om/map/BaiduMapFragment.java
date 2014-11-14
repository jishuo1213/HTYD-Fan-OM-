package com.htyd.fan.om.map;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.htyd.fan.om.R;
import com.htyd.fan.om.util.ui.UItoolKit;

public class BaiduMapFragment extends Fragment {

	public static final String LATITUDE = "latitude";
	public static final String LONGTITUDE = "longtitude";

	private MapView mapView;
	private OMLocationManager mLocationManager;
	private LinearLayout progressLayout;
	private ViewStub vs;
	private LocationRecListener mListener;

	/*
	 * public static Fragment newInstance(double latitude, double longtitude) {
	 * Bundle arg = new Bundle(); arg.putDouble(LATITUDE, latitude);
	 * arg.putDouble(LONGTITUDE, longtitude); Fragment fragment = new
	 * BaiduMapFragment(); fragment.setArguments(arg); return fragment; }
	 */

	public interface LocationRecListener {
		public void onLocationReceiveListener(Location loc);
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
			super.onPause();
		mapView.onPause();
	}

	@Override
	public void onDestroy() {
		if (mapView != null)
			mapView.onDestroy();
		mLocationManager.stopGPSLocationUpdates();
		super.onDestroy();
	}

	private void initView(View v) {
		vs = (ViewStub) v.findViewById(R.id.map_sub);
		progressLayout = (LinearLayout) v.findViewById(R.id.line_progress);
	}

	private void initMapView(Location loc) {

		double latitude = loc.getLatitude();
		double longtitude = loc.getLongitude();

		BaiduMap bMap = mapView.getMap();
		bMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		LatLng point = transCoordinate(new LatLng(latitude, longtitude));// 转换坐标
		/* 设置我的位置 */
		MyLocationData.Builder locationbuilder = new com.baidu.mapapi.map.MyLocationData.Builder();
		locationbuilder.latitude(point.latitude);
		locationbuilder.longitude(point.longitude);
		MyLocationData location = locationbuilder.build();
		bMap.setMyLocationData(location);
		/* 设置地图状态 */
		MapStatus.Builder statusBuilder = new MapStatus.Builder();
		statusBuilder.target(point);
		statusBuilder.zoom(25);
		MapStatus mapStatus = statusBuilder.build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mapStatus);
		bMap.setMapStatus(mapStatusUpdate);
		/* 设置地图覆盖标识 */
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marka);
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		bMap.addOverlay(option);
	}

	private LatLng transCoordinate(LatLng temppoint) {
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		converter.coord(temppoint);
		return converter.convert();
	}

	public void setListener(LocationRecListener listener) {
		mListener = listener;
	}

	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onProviderEnabledChanged(boolean enabled) {
		}

		@Override
		protected void onGPSLocationReceived(Context context, Location loc) {
			Log.i("fanjishuo_____onGPSLocationReceived",
					"loc.getLatitude()" + loc.getLatitude()
							+ "loc.getLongitude()" + loc.getLongitude());
			UItoolKit.showToastShort(getActivity(),
					"loc.getLatitude()" + loc.getLatitude()
							+ "loc.getLongitude()" + loc.getLongitude());
			if (progressLayout.isShown()) {
				progressLayout.setVisibility(View.GONE);
			}
			if (!vs.isShown()) {
				mapView = (MapView) vs.inflate();
			}
			UItoolKit.showToastShort(
					getActivity(),
					"Latitude" + loc.getLatitude() + "Longitude"
							+ loc.getLongitude());
			initMapView(loc);
		}

		@Override
		protected void onNetWorkLocationReceived(Context context,
				final Location loc) {
			Log.i("fanjishuo_____onNetWorkLocationReceived",
					"loc.getLatitude()" + loc.getLatitude()
							+ "loc.getLongitude()" + loc.getLongitude());
			if (progressLayout.isShown()) {
				progressLayout.setVisibility(View.GONE);
			}
			if (!vs.isShown()) {
				mapView = (MapView) vs.inflate();
			}
			initMapView(loc);
			mListener.onLocationReceiveListener(loc);
			mLocationManager.stopNetWorkLocationUpdates();
		}
	};
}
