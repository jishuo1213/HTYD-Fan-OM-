package com.htyd.fan.om.taskmanage;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.taskmanage.fragment.CreateTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.EditTaskFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskListFragment;
import com.htyd.fan.om.taskmanage.fragment.TaskWithProcessFragment;
import com.htyd.fan.om.util.base.SingleFragmentActivity;

public class TaskManageActivity extends SingleFragmentActivity {

	private static final String TASKNETID = "tasknetid";
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
			return new SingleTaskLoader(getBaseContext(), args.getInt(TASKNETID));
		}

		@Override
		public void onLoadFinished(Loader<TaskDetailBean> loader,
				final TaskDetailBean data) {
			resposHandler.post(new Runnable() {
				@Override
				public void run() {
					Log.i("fanjishuo____onLoadFinished", (data == null)+"");
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
			return new CreateTaskFragment();
		} else {
			initLoader();
			return new Fragment();
		}
	}
}
