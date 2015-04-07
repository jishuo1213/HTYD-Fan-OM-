package com.htyd.fan.om.taskmanage;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.taskmanage.fragment.CreateTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.CreateTaskFragment.SaveTaskListener;
import com.htyd.fan.om.taskmanage.fragment.EditTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.QueryTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskListFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskWithProcessFragment;
import com.htyd.fan.om.util.base.SingleFragmentActivity;

public class TaskManageActivity extends SingleFragmentActivity implements SaveTaskListener {

	public static final String TASKDETAIL = "taskdetail";
	public static final String ISLOCAL = "islocal";
	
	private static final String TASKLOCALID = "tasknetid";
	private static final int LOADERID = 0x09;
	
	private LoaderManager mManager;
	private TaskLoaderCallBacks mCallBacks;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		initActionBar();
	}

	private void initLoader() {
		mManager = getLoaderManager();
		mCallBacks = new TaskLoaderCallBacks(new Handler());
		Bundle args = new Bundle();
		args.putInt(TASKLOCALID, getIntent().getIntExtra(TaskListFragment.TASKLOCALID, -1));
		mManager.initLoader(LOADERID, args, mCallBacks);
	}

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_top_navigation_bar));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	protected void showFragment(TaskDetailBean data) {
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(fragment);
		switch (getIntent().getIntExtra(TaskListFragment.TASKTYPE, -1)) {
		case 0:// 在处理
			fragment = TaskWithProcessFragment.newInstance(data);
			break;
		case -2:// 编辑任务
			fragment = EditTaskFragment.newInstance(data);
			break;
		case 2:// 已完成
			fragment = TaskWithProcessFragment.newInstance(data);
			break;
		default:
			fragment = null;
			break;
		}
		ft.replace(R.id.fragmentContainer, fragment).commit();
	}
	
	private class TaskLoaderCallBacks implements LoaderCallbacks<TaskDetailBean>{

		private Handler resposHandler;
		
		public TaskLoaderCallBacks(Handler resposHandler) {
			this.resposHandler = resposHandler;
		}

		@Override
		public Loader<TaskDetailBean> onCreateLoader(int id, Bundle args) {
			return new SingleTaskLoader(getBaseContext(), args.getInt(TASKLOCALID));
		}

		@Override
		public void onLoadFinished(Loader<TaskDetailBean> loader,
				final TaskDetailBean data) {
			resposHandler.post(new Runnable() {
				@Override
				public void run() {
					showFragment(data);
				}
			});
		}

		@Override
		public void onLoaderReset(Loader<TaskDetailBean> loader) {
		}
		
	}

	@Override
	protected Fragment createFragment() {
		if (getIntent().getIntExtra(TaskListFragment.TASKTYPE, -1) == -1) {
			return CreateTaskFragment.newInstance(this);
		}else if(getIntent().getIntExtra(TaskListFragment.TASKTYPE, -1) == 12){
			return new QueryTaskFragment();
		} else {
			initLoader();
			return new Fragment();
		}
	}

	@Override
	public void onSaveSuccess(TaskDetailBean mBean, boolean isLocal) {
		Intent i = new Intent();
		i.putExtra(TASKDETAIL, mBean);
		i.putExtra(ISLOCAL, isLocal);
		setResult(Activity.RESULT_OK,i);
	}
}
