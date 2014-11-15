package com.htyd.fan.om.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.AttentManagerActivity;
import com.htyd.fan.om.map.OMLocationManager;

public class TabOneFragment extends Fragment {

	private OMLocationManager mLocationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationManager = OMLocationManager.get(getActivity());
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.test_layout, container, false);
		Button getLoc = (Button) v.findViewById(R.id.btn_get_location);
		getLoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLocationManager.startLocationUpdates();
				Intent i = new Intent(getActivity(), AttentManagerActivity.class);
				startActivity(i);
			}
		});
		return v;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
