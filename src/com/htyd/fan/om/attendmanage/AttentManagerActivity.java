package com.htyd.fan.om.attendmanage;

import android.app.Fragment;
import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.fragment.AttendManageFragment;
import com.htyd.fan.om.map.BaiduMapFragment;
import com.htyd.fan.om.map.BaiduMapFragment.LocationRecListener;
import com.htyd.fan.om.util.base.SimpleFragmentActivity;

public class AttentManagerActivity extends SimpleFragmentActivity implements
		LocationRecListener {


	@Override
	protected void onCreate(Bundle arg0) {
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate(arg0);
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.attent_manage_activity;
	}

	@Override
	protected Fragment[] createFragment() {
		return new Fragment[] { new AttendManageFragment(),
				new BaiduMapFragment() };
	}

	@Override
	protected int[] getAllFragmentId() {
		return new int[] { R.id.attent_manage_fragment_container,
				R.id.map_fragment_container };
	}

	@Override
	public void onLocationReceiveListener(Location loc) {
		FragmentManager fm = getFragmentManager();
		AttendManageFragment mf = (AttendManageFragment) fm
				.findFragmentById(R.id.attent_manage_fragment_container);
		mf.updateUI(loc);
	}
}
