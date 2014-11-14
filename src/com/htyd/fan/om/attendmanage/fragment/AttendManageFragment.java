package com.htyd.fan.om.attendmanage.fragment;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.model.LocationBean;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AttendManageFragment extends Fragment{

	private ViewPanel mPanel;
	private LocationBean mBean;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.attend_manage_fragment,container, false);
		intiView(v);
		return v;
	}

	private void intiView(View v) {
		mPanel = new ViewPanel(v);
	}

	private void reverseGeoCode(LatLng loc){
		GeoCoder mCoder = GeoCoder.newInstance();
		mCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener(){

			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
			}

			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
				ReverseGeoCodeResult.AddressComponent component = arg0.getAddressDetail();
				
			}
		});
	}
	
	public void updateUI(Location loc) {
		
		reverseGeoCode();
	}
private class ViewPanel {
	private TextView locTextView,timeTextView;
	public  ViewPanel(View v){
		locTextView = (TextView) v.findViewById(R.id.tv_loction);
		timeTextView = (TextView) v.findViewById(R.id.tv_time);
	}
	public void setLocation(String loc){
		locTextView.setText(loc);
	}
	public void setTime(String time){
		timeTextView.setText(time);
	}
}
}
