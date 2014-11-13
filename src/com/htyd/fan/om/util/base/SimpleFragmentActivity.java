package com.htyd.fan.om.util.base;

import com.htyd.fan.om.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public abstract class SimpleFragmentActivity extends Activity {
	protected abstract Fragment[] createFragment();

	protected abstract int[] getAllFragmentId();

	protected int getLayoutResId() {
		return R.layout.activity_fragment;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(getLayoutResId());
		FragmentManager fm = getFragmentManager();
		int[] fragmentIDArray = getAllFragmentId();
		Fragment[] fragmentArray = createFragment();
		for (int i = 0; i < fragmentIDArray.length; i++) {

			Fragment fragment = fm.findFragmentById(fragmentIDArray[i]);
			if (fragment == null) {
				fragment = fragmentArray[i];
				fm.beginTransaction().add(fragmentIDArray[i], fragment)
						.commit();
			}
		}
	}

}
