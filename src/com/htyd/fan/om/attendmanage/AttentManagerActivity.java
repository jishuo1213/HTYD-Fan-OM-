package com.htyd.fan.om.attendmanage;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.baidu.mapapi.SDKInitializer;
import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.fragment.AttendManageFragment;
import com.htyd.fan.om.map.BaiduMapFragment;
import com.htyd.fan.om.map.BaiduMapFragment.LocationRecListener;
import com.htyd.fan.om.model.OMLocationBean;
import com.htyd.fan.om.util.base.SimpleFragmentActivity;

public class AttentManagerActivity extends SimpleFragmentActivity implements
		LocationRecListener {


	@Override
	protected void onCreate(Bundle arg0) {
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate(arg0);
		initActionBar();
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			Log.i("fanjishuo_____onOptionsItemSelected", "android.R.id.home");
			if(NavUtils.getParentActivityName(this) != null){
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onLocationReceiveListener(OMLocationBean loc) {
		FragmentManager fm = getFragmentManager();
		AttendManageFragment mf = (AttendManageFragment) fm
				.findFragmentById(R.id.attent_manage_fragment_container);
		mf.updateUI(loc);
	}
	
	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_top_navigation_bar));
		actionBar.setTitle("签到");
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
}
