package com.htyd.fan.om.map;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
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
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.ui.UItoolKit;

public class BaiduMapFragment extends Fragment {

	public static final String LATITUDE = "latitude";
	public static final String LONGTITUDE = "longtitude";

	private MapView mapView;

	/*
	 * public static Fragment newInstance(double latitude, double longtitude) {
	 * Bundle arg = new Bundle(); arg.putDouble(LATITUDE, latitude);
	 * arg.putDouble(LONGTITUDE, longtitude); Fragment fragment = new
	 * BaiduMapFragment(); fragment.setArguments(arg); return fragment; }
	 */
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.map_fragment_layout, container,
				false);
		initMapView(v);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}

	private void initMapView(View v) {
		mapView = (MapView) v.findViewById(R.id.map_baidu);
		LatLng tempLatLng = Preferences.getLastLocation(getActivity());

		double latitude = tempLatLng.latitude;
		double longtitude = tempLatLng.longitude;

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
		bMap.setOnMapStatusChangeListener(mMapStatusCheangeListener);
	}

	private LatLng transCoordinate(LatLng temppoint) {
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		converter.coord(temppoint);
		return converter.convert();
	}

	private OnMapStatusChangeListener mMapStatusCheangeListener = new OnMapStatusChangeListener() {
		
		@Override
		public void onMapStatusChangeStart(MapStatus arg0) {
		}
		
		@Override
		public void onMapStatusChangeFinish(MapStatus arg0) {
		}
		
		@Override
		public void onMapStatusChange(MapStatus arg0) {
		}
	};
	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {

		@Override
		protected void onProviderEnabledChanged(boolean enabled) {
		}

		@Override
		protected void onLocationReceived(Context context, Location loc) {
			Preferences.setLastLocation(getActivity(), loc);
			UItoolKit.showToastShort(
					getActivity(),
					"Latitude" + loc.getLatitude() + "Longitude"
							+ loc.getLongitude());
			
		}
	};
}
