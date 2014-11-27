package com.htyd.fan.om.util.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.htyd.fan.om.util.base.SingleFragmentActivity;

public class CameraActivity extends SingleFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(arg0);
	}

	@Override
	protected Fragment createFragment() {

		return new CameraFragment();
	}

}
