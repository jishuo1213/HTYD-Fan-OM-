package com.htyd.fan.om.taskmanage;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.taskmanage.fragment.CreateTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.EditTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskListFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskWithProcessFragment;

public class TaskManageActivity extends Activity {

	private static final String TASKNETID = "tasknetid";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_fragment);
		initActionBar();
	}

/*	@Override
	protected Fragment[] createFragment() {
		switch (getIntent().getIntExtra(TaskListFragment.TASKTYPE, -1)) {
		case 0:// 在处理
			Fragment processfragment = TaskWithProcessFragment.newInstance(getIntent()
					.getParcelableExtra(TaskListFragment.TASKID));
			 return new Fragment[] { processfragment };
		case -2:// 编辑任务
			Fragment fragment = EditTaskFragment.newInstance(getIntent()
					.getParcelableExtra(TaskListFragment.TASKID));
			return new Fragment[] { fragment };
		case 2:// 已完成
			Fragment taskendfragment = TaskWithProcessFragment.newInstance(getIntent()
					.getParcelableExtra(TaskListFragment.TASKID));
			 return new Fragment[] { taskendfragment };
		case -1:// 新建任务
			return new Fragment[] { new CreateTaskFragment() };
		}
		return null;
	}*/

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
	
	private class TaskLoaderCallBacks implements LoaderCallbacks<TaskDetailBean>{

		@Override
		public Loader<TaskDetailBean> onCreateLoader(int id, Bundle args) {
			return new SingleTaskLoader(getBaseContext(), args.getInt(TASKNETID));
		}

		@Override
		public void onLoadFinished(Loader<TaskDetailBean> loader,
				TaskDetailBean data) {
			switch (getIntent().getIntExtra(TaskListFragment.TASKTYPE, -1)) {
			case 0:// 在处理
				Fragment processfragment = TaskWithProcessFragment.newInstance(data);
			case -2:// 编辑任务
				Fragment fragment = EditTaskFragment.newInstance(data);
			case 2:// 已完成
				Fragment taskendfragment = TaskWithProcessFragment.newInstance(data);
			case -1:// 新建任务
				Fragment createTaskFragment = new CreateTaskFragment();
			}
		}

		@Override
		public void onLoaderReset(Loader<TaskDetailBean> loader) {
		}
		
	}
}
