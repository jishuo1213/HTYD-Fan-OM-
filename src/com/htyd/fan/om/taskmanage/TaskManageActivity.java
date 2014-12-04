package com.htyd.fan.om.taskmanage;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.htyd.fan.om.R;
import com.htyd.fan.om.taskmanage.fragment.CreateTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.ReceiveTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskListFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskWithProcessFragment;
import com.htyd.fan.om.util.base.SimpleFragmentActivity;

public class TaskManageActivity extends SimpleFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initActionBar();
	}

	@Override
	protected Fragment[] createFragment() {
		switch (getIntent().getIntExtra(TaskListFragment.TASKTYPE, -1)) {
		case 0:// 在处理
			Fragment processfragment = TaskWithProcessFragment.newInstance(getIntent()
					.getParcelableExtra(TaskListFragment.SELECTITEM));
			 return new Fragment[] { processfragment };
		case 1:// 待领取
			Fragment fragment = ReceiveTaskFragment.newInstance(getIntent()
					.getParcelableExtra(TaskListFragment.SELECTITEM));
			return new Fragment[] { fragment };
		case 2:// 已完成
		case -1:// 新建任务
			return new Fragment[] { new CreateTaskFragment() };
		}
		return null;
	}

	@Override
	protected int[] getAllFragmentId() {
		return new int[] { R.id.fragmentContainer };
	}

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_top_navigation_bar));
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(this) != null) {
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
