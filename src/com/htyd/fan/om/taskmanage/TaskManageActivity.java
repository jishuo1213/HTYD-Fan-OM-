package com.htyd.fan.om.taskmanage;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
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
	private static final int LOADERID = 0x09;
	
	private LoaderManager mManager;
	private TaskLoaderCallBacks mCallBacks;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		if (getIntent().getIntExtra(TaskListFragment.TASKTYPE, -1) == -1) {
			Fragment fragment = new CreateTaskFragment();
			showFragment(fragment);
		} else {
			initLoader();
		}
		initActionBar();
	}

	private void initLoader() {
		mManager = getLoaderManager();
		mCallBacks = new TaskLoaderCallBacks();
		Bundle args = new Bundle();
		args.putInt(TASKNETID, getIntent().getIntExtra(TaskListFragment.TASKID, -1));
		mManager.initLoader(LOADERID, args, mCallBacks);
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
	
	protected void showFragment(Fragment fragmenToshow) {
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		if(fragment == null){
			fragment = fragmenToshow;
			fm.beginTransaction().add(R.id.fragmentContainer, fragment)
			.commit();
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
			Fragment fragmenToshow;
			switch (getIntent().getIntExtra(TaskListFragment.TASKTYPE, -1)) {
			case 0:// 在处理
				fragmenToshow = TaskWithProcessFragment.newInstance(data);
			case -2:// 编辑任务
				fragmenToshow = EditTaskFragment.newInstance(data);
			case 2:// 已完成
				fragmenToshow = TaskWithProcessFragment.newInstance(data);
			default:
				fragmenToshow = null;
			}
			showFragment(fragmenToshow);
		}

		@Override
		public void onLoaderReset(Loader<TaskDetailBean> loader) {
		}
		
	}
}
