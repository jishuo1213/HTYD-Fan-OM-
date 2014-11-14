package com.htyd.fan.om.attendmanage.fragment;

import com.baidu.mapapi.search.geocode.GeoCoder;
import com.htyd.fan.om.R;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AttendManageFragment extends Fragment{

	private ViewPanel mPanel;
	
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

	private void reverseGeoCode(Location loc){
		GeoCoder mCoder = GeoCoder.newInstance();
		
	}
	
	public void updateUI(Location loc) {
		
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
