package com.htyd.fan.om.taskmanage.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskListBean;
import com.htyd.fan.om.taskmanage.TaskManageActivity;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.db.SQLSentence;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.CustomChooserView.OnItemChooserListener;
import com.htyd.fan.om.util.ui.UItoolKit;

public class TaskListFragment extends Fragment implements OnItemChooserListener {

	public static final String TASKTYPE = "tasktype";
	public static final String TASKID = "taskid";
	public static final int SAVETASK = 0x08;
	
	protected static final String TASKSTATE = "taskstate";
	protected static final String INPROCESSINGTASK = "inprocessingtask";
	protected static final String COMPLETED = "completed";
	
	
	HashMap<String, List<TaskListBean>> taskMap;
	ListView mListView;
	TaskAdapter allTaskAdapter, inProcessTaskAdapter,
			completedAdapter;
	private TaskCursorCallback mCallback;
	private LoaderManager mLoadManager;
	protected PullToRefreshListView mPullRefreshListView;
	boolean isLoaderFinish;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		taskMap = new HashMap<String, List<TaskListBean>>();
		taskMap.put(COMPLETED, new ArrayList<TaskListBean>());
		taskMap.put(INPROCESSINGTASK, new ArrayList<TaskListBean>());
		mCallback = new TaskCursorCallback();
		mLoadManager = getActivity().getLoaderManager();
		isLoaderFinish = false;
		mLoadManager.initLoader(1, null, mCallback);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tasklist_layout, container,
				false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.list_my_task);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener <ListView>(){
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				new DownloadTask().execute();
			}
		});
		mListView = mPullRefreshListView.getRefreshableView();
		if (mListView.getAdapter() == null) {
			mListView.setAdapter(allTaskAdapter);
		}
		mListView.setOnItemClickListener(new TaskItemClickListener());
		registerForContextMenu(mListView);
	}

	@Override
	public void onItemChooser(int position) {
		switch (position) {
		case 0:
			mListView.setAdapter(inProcessTaskAdapter);
			break;
		case 1:
			if(completedAdapter != null)
				mListView.setAdapter(completedAdapter);
			break;
		case 2:
			try {
				Intent i = new Intent(getActivity(), TaskManageActivity.class);
				i.putExtra(TASKTYPE, -1);
				startActivityForResult(i, SAVETASK);
			} catch (Exception e) {
				e.printStackTrace();
				Intent i = new Intent("om.fan.task");
				i.putExtra(TASKTYPE, -1);
				startActivity(i);
				break;
			}
			break;
		case 3:
			Intent intent = new Intent(getActivity(), TaskManageActivity.class);
			intent.putExtra(TASKTYPE, 12);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == SAVETASK){
				mLoadManager.restartLoader(1, null, mCallback);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = info.position;
		TaskListBean mBean = (TaskListBean) mListView.getAdapter()
				.getItem(position);
		switch (item.getItemId()) {
		case R.id.menu_edit_task:
			Intent i = new Intent(getActivity(), TaskManageActivity.class);
			i.putExtra(TASKID, mBean.taskNetId);
			i.putExtra(TASKTYPE, -2);
			startActivity(i);
			return true;
		case R.id.menu_delete_task:
			new deleteTask().execute(mBean);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.edit_task_menu, menu);
	}

	private class TaskItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TaskListBean mBean = (TaskListBean) parent.getAdapter()
					.getItem(position);
			Intent i = new Intent(getActivity(), TaskManageActivity.class);
			i.putExtra(TASKTYPE, mBean.taskState);
			i.putExtra(TASKID, mBean.taskNetId);
			startActivity(i);
		}
	}

	private class TaskAdapter extends BaseAdapter {

		private int type;// 0 在处理 1 待领取 2已完成
		private int inProcessTaskNum, completedNum;

		public TaskAdapter(int type) {
			super();
			this.type = type;
			inProcessTaskNum = taskMap.get(INPROCESSINGTASK).size();
			completedNum = taskMap.get(COMPLETED).size();
		}

		@Override
		public int getCount() {
			switch (type) {
			case 0:
				return inProcessTaskNum;
			case 2:
				return completedNum;
			case -1:
				return inProcessTaskNum + completedNum;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			switch (type) {
			case 0:
				return taskMap.get(INPROCESSINGTASK).get(position);
			case 2:
				return taskMap.get(COMPLETED).get(position);
			case -1:
				if (position < inProcessTaskNum) {
					return taskMap.get(INPROCESSINGTASK).get(position);
				}  else {
					return taskMap.get(COMPLETED).get(
							position - inProcessTaskNum);
				}
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.task_item_layout, null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			TaskListBean mBean = (TaskListBean) getItem(position);
			mHolder.taskDescrption.setText(mBean.taskTitle);
			mHolder.taskCreateTime.setText(Utils.formatTime(mBean.createTime));
			if (mBean.taskState == 0) {
				mHolder.taskState.setText("在处理");
				mHolder.taskStateIcon.setBackgroundResource(R.drawable.img_task_process);
			} else {
				mHolder.taskState.setText("已完成");
				mHolder.taskStateIcon.setBackgroundResource(R.drawable.img_task_complete);
			}
			return convertView;
		}
	}

	private class ViewHolder {
		public TextView taskDescrption, taskCreateTime, taskState;
		

		 public ImageView taskStateIcon;
		public ViewHolder(View v) {
			taskDescrption = (TextView) v.findViewById(R.id.tv_task_descrption);
			taskCreateTime = (TextView) v.findViewById(R.id.tv_task_createtime);
			taskState = (TextView) v.findViewById(R.id.tv_task_state);
			 taskStateIcon = (ImageView)v.findViewById(R.id.img_task_state_icon);
		}
	}

	public static class TaskCursorLoader extends SQLiteCursorLoader {

		private OMUserDatabaseManager mManager;

		public TaskCursorLoader(Context context) {
			super(context);
			mManager = OMUserDatabaseManager.getInstance(context);
		}

		@Override
		protected Cursor loadCursor() {
			mManager.openDb(0);
			return mManager.queryUserTask();
		}

		@Override
		protected Cursor loadFromNet() {
			
			JSONObject params = new JSONObject();
			try {
				params.put("RWBT", "");
				params.put("TXSJ", "");
				params.put("RWID", "");
				params.put("RWZT", "");
				params.put("TXSJ_BEGIN", "");
				params.put("TXSJ_END", "");
				params.put("TXR", Preferences.getUserinfo(getContext(), "YHMC"));
				params.put("LQR", Preferences.getUserinfo(getContext(), "YHID"));
				params.put("RWGL", "");
				params.put("WDRW", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			String result = "";
			try {
				result = NetOperating.getResultFromNet(getContext(), params,
						Urls.TASKURL, "Operate=getAllRwxx");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				return null;
			}
			boolean success = false;
			try {
				success = Utility.handleTaskResponse(mManager, result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (success) {
				return loadCursor();
			} else {
				return null;
			}
		}
	}

	private class TaskCursorCallback implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new TaskCursorLoader(getActivity());
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			List<TaskListBean> mList = new ArrayList<TaskListBean>();
			if (data != null && data.moveToFirst()) {
				isLoaderFinish = true;
				TaskCursor taskCursor = (TaskCursor) data;
				do {
					mList.add(taskCursor.getTaskListBean());
				} while (data.moveToNext());
				taskMap.get(INPROCESSINGTASK).clear();
				taskMap.get(COMPLETED).clear();
				for (TaskListBean mBean : mList) {
					switch (mBean.taskState) {
					case 0:// 在处理任务
						taskMap.get(INPROCESSINGTASK).add(mBean);
						break;
					case 2:// 已完成任务
						taskMap.get(COMPLETED).add(mBean);
						break;
					}
				}
				allTaskAdapter = new TaskAdapter(-1);
				if (mListView != null) {
					mListView.setAdapter(allTaskAdapter);
				}
				inProcessTaskAdapter = new TaskAdapter(0);
				completedAdapter = new TaskAdapter(2);
			} else {
				isLoaderFinish = false;
				if(mListView != null && mListView.getAdapter() != null){
					mListView.setAdapter(null);
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}
	
	private class DownloadTask extends AsyncTask<String, Void, Boolean>{
		
		private OMUserDatabaseManager mManger;
		
		public DownloadTask() {
			mManger = OMUserDatabaseManager.getInstance(getActivity());
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			JSONObject param = new JSONObject();
			try {
				param.put("RWBT", "");
				param.put("TXSJ", "");
				param.put("RWID", "");
				param.put("RWZT", "");
				param.put("RWZT", "");
				param.put("TXSJ_BEGIN", "");
				param.put("TXSJ_END", "");
				param.put("TXR", Preferences.getUserinfo(getActivity(), "YHMC"));
				param.put("LQR", Preferences.getUserinfo(getActivity(), "YHID"));
				param.put("RWGL", "");
				param.put("WDRW", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			String result = "";
			try {
				result = NetOperating.getResultFromNet(getActivity(), param,
						Urls.TASKURL, "Operate=getAllRwxx");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			try {
				mManger.clearFeedTable(SQLSentence.TABLE_TASK);
				return Utility.handleTaskResponse(mManger, result);
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				mLoadManager.restartLoader(1, null, mCallback);
				UItoolKit.showToastShort(getActivity(), "刷新成功");
			} else {
				UItoolKit.showToastShort(getActivity(), "加载数据失败");
			}
			mPullRefreshListView.onRefreshComplete();
			cancel(false);
		}
	}
	
	private class deleteTask extends AsyncTask<TaskListBean, Void, Boolean>{

		private TaskListBean mBean;
		
		@Override
		protected Boolean doInBackground(TaskListBean... params) {
			JSONObject param = new JSONObject();
			mBean = params[0];
			String result = "";
			try {
				param.put("RWID", mBean.taskNetId);
				result = NetOperating.getResultFromNet(getActivity(), param, Urls.TASKURL, "Operate=deleteRwByRwid");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			try {
				return new JSONObject(result).getBoolean("RESULT");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				deleteLocalTask(mBean.taskNetId);
				mLoadManager.restartLoader(1, null, mCallback);
				UItoolKit.showToastShort(getActivity(), "删除成功");
			} else {
				UItoolKit.showToastShort(getActivity(), "删除失败");
			}
		}
	}
	protected void deleteLocalTask(final int taskNetId){
		final OMUserDatabaseManager mManger = OMUserDatabaseManager.getInstance(getActivity());
		ThreadPool.runMethod(new Runnable() {
			@Override
			public void run() {
				mManger.deleteTask(taskNetId);
				mManger.deleteTaskProcess(taskNetId);
				mManger.deleteTaskAccessory(taskNetId);
			}
		});
	}
}
