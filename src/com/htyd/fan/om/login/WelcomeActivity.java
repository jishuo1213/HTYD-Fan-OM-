package com.htyd.fan.om.login;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Window;

import com.htyd.fan.om.util.base.SingleFragmentActivity;

public class WelcomeActivity extends SingleFragmentActivity {

	
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
	}

	@Override
	protected Fragment createFragment() {
		return new WelcomePageFragment();
	}

}
