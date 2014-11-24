package com.htyd.fan.om.taskmanage;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;

import com.htyd.fan.om.R;
import com.htyd.fan.om.util.base.SimpleFragmentActivity;

public class TaskManageActivity extends SimpleFragmentActivity {

	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initActionBar();
	}

	@Override
	protected Fragment[] createFragment() {

		return null;
	}

	@Override
	protected int[] getAllFragmentId() {

		return null;
	}
	
	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_top_navigation_bar));
		actionBar.setDisplayShowHomeEnabled(true);
	}
}
