package com.htyd.fan.om.taskmanage.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.taskmanage.TaskManageActivity;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.CustomChooserView.OnItemChooserListener;

public class TaskListFragment extends Fragment implements OnItemChooserListener {

	public static final String TASKTYPE = "tasktype";
	public static final String SELECTITEM = "selectitem";

	static final String TASKSTATE = "taskstate";
	static final String INPROCESSINGTASK = "inprocessingtask";
	static final String BERECEIVE = "bereceive";
	static final String COMPLETED = "completed";

	HashMap<String, List<TaskDetailBean>> taskMap;
	ListView mListView;
	TaskAdapter allTaskAdapter, inProcessTaskAdapter, beReceiveAdapter,
			completedAdapter;
	private TaskCursorCallback mCallback;
	private LoaderManager mLoadManager;
	boolean isLoaderFinish;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		taskMap = new HashMap<String, List<TaskDetailBean>>();
		mCallback = new TaskCursorCallback();
		mLoadManager = getActivity().getLoaderManager();
		isLoaderFinish = false;
		Bundle args = new Bundle();
		args.putInt(TASKSTATE, -1);
		mLoadManager.initLoader(0, args, mCallback);
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
		mListView = (ListView) v.findViewById(R.id.list_my_task);
		mListView.setOnItemClickListener(new TaskItemClickListener());
	}

	@Override
	public void onItemChooser(int position) {
		switch (position) {
		case 0:
			mListView.setAdapter(inProcessTaskAdapter);
			break;
		case 1:
			mListView.setAdapter(beReceiveAdapter);
			break;
		case 2:
			mListView.setAdapter(completedAdapter);
			break;
		case 3:
			Intent i = new Intent(getActivity(), TaskManageActivity.class);
			i.putExtra(TASKTYPE, -1);
			startActivity(i);
			break;
		}
	}

	private class TaskItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TaskDetailBean mBean = (TaskDetailBean) parent.getAdapter()
					.getItem(position);
			Intent i = new Intent(getActivity(), TaskManageActivity.class);
			i.putExtra(TASKTYPE, mBean.taskType);
			i.putExtra(SELECTITEM, mBean);
			startActivity(i);
		}
	}

	private class TaskAdapter extends BaseAdapter {

		private int type;// 0 在处理 1 待领取 2已完成
		private int inProcessTaskNum, beReceiveNum, completedNum;

		public TaskAdapter(int type) {
			super();
			this.type = type;
			inProcessTaskNum = taskMap.get(INPROCESSINGTASK).size();
			beReceiveNum = taskMap.get(BERECEIVE).size();
			completedNum = taskMap.get(COMPLETED).size();
		}

		@Override
		public int getCount() {
			switch (type) {
			case 0:
				return inProcessTaskNum;
			case 1:
				return beReceiveNum;
			case 2:
				return completedNum;
			case -1:
				return inProcessTaskNum + beReceiveNum + completedNum;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			switch (type) {
			case 0:
				return taskMap.get(INPROCESSINGTASK).get(position);
			case 1:
				return taskMap.get(BERECEIVE).get(position);
			case 2:
				return taskMap.get(COMPLETED).get(position);
			case -1:
				if (position < inProcessTaskNum) {
					return taskMap.get(INPROCESSINGTASK).get(position);
				} else if (position >= inProcessTaskNum
						&& position < (inProcessTaskNum + beReceiveNum - 1)) {
					return taskMap.get(BERECEIVE).get(
							position - inProcessTaskNum);
				} else {
					return taskMap.get(COMPLETED).get(
							position - inProcessTaskNum - beReceiveNum);
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
			TaskDetailBean mBean = (TaskDetailBean) getItem(position);
			mHolder.taskDescrption.setText(mBean.taskDescription);
			mHolder.taskCreateTime.setText(Utils.formatTime(mBean.saveTime));
			mHolder.taskState.setText(mBean.taskState + "");
			return convertView;
		}
	}

	private class ViewHolder {
		public TextView taskDescrption, taskCreateTime, taskState;

		// public ImageView taskStateIcon;
		public ViewHolder(View v) {
			taskDescrption = (TextView) v.findViewById(R.id.tv_task_descrption);
			taskCreateTime = (TextView) v.findViewById(R.id.tv_task_createtime);
			taskState = (TextView) v.findViewById(R.id.tv_task_state);
			// taskStateIcon = (ImageView)
			// v.findViewById(R.id.img_task_state_icon);
		}
	}

	public static class TaskCursorLoader extends SQLiteCursorLoader {

		private int taskState;
		private OMUserDatabaseManager mManager;

		public TaskCursorLoader(Context context, int state) {
			super(context);
			this.taskState = state;
			mManager = OMUserDatabaseManager.getInstance(context);
		}

		@Override
		protected Cursor loadCursor() {
			mManager.openDb(0);
			return mManager.queryTaskCursorByState(taskState);
		}

		@Override
		protected Cursor loadFromNet() {
			/*
			 * 从网上获取数据，插入数据库，还未完成
			 */
			try {
				NetOperating.getResultFromNet(getContext(), null, Urls.TASKURL, "Operate=getAllRwxx");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return loadCursor();
		}
	}

	private class TaskCursorCallback implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new TaskCursorLoader(getActivity(), args.getInt(TASKSTATE));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			List<TaskDetailBean> mList = new ArrayList<TaskDetailBean>();
			if (data != null && data.moveToFirst()) {
				isLoaderFinish = true;
				TaskCursor taskCursor = (TaskCursor) data;
				mList.add(taskCursor.getTask());
				for (TaskDetailBean mBean : mList) {
					switch (mBean.taskState) {
					case 0:// 在处理任务
						taskMap.get(INPROCESSINGTASK).add(mBean);
						break;
					case 1:// 待领取任务
						taskMap.get(BERECEIVE).add(mBean);
						break;
					case 2:// 已完成任务
						taskMap.get(COMPLETED).add(mBean);
						break;
					}
				}
				if (mListView != null) {
					mListView.setAdapter(allTaskAdapter);
				}
				allTaskAdapter = new TaskAdapter(-1);
				inProcessTaskAdapter = new TaskAdapter(0);
				beReceiveAdapter = new TaskAdapter(1);
				completedAdapter = new TaskAdapter(2);
			} else {
				isLoaderFinish = false;
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}
}
