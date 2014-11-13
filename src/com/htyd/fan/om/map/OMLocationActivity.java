package com.htyd.fan.om.map;

import com.baidu.mapapi.SDKInitializer;
import com.htyd.fan.om.R;
import com.htyd.fan.om.util.base.SimpleFragmentActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class OMLocationActivity extends SimpleFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		SDKInitializer.initialize(getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(arg0);
	}

	@Override
	protected Fragment[] createFragment() {
		return new Fragment[] { new  BaiduMapFragment() };
	}

	/*.newInstance(getIntent()
			.getDoubleExtra(BaiduMapFragment.LATITUDE, -1), getIntent()
			.getDoubleExtra(BaiduMapFragment.LONGTITUDE, -1))*/
	
	@Override
	protected int[] getAllFragmentId() {
		return new int[] { R.id.fragmentContainer };
	}

}
