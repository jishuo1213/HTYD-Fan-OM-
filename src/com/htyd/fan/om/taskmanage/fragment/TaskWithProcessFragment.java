package com.htyd.fan.om.taskmanage.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.taskmanage.fragment.EditTaskFragment.TaskViewPanel;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskProcessCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;

public class TaskWithProcessFragment extends Fragment {

	private static final String SELECTTASK = "selecttask";
	private static final String TASKID = "taskid";
	private static final int REQUEST_PROCESS = 0;

	private TaskViewPanel mPanel;
	protected TaskDetailBean mBean;
	protected ListView processListView;
	protected List<TaskProcessBean> listProcess;
	protected ProcessAdapter mAdapter;
	private TaskProcessCursorCallback mCallback;
	private LoaderManager mManager;

	public static Fragment newInstance(Parcelable mBean) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(SELECTTASK, mBean);
		Fragment fragment = new TaskWithProcessFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBean = getArguments().getParcelable(SELECTTASK);
		mManager = getActivity().getLoaderManager();
		mCallback = new TaskProcessCursorCallback();
		Bundle args = new Bundle();
		args.putInt(TASKID, mBean.taskNetId);
		mManager.initLoader(0, args, mCallback);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.task_with_process_layout, container,
				false);
		initView(v);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.create_task_process, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_create_task_process:
			FragmentManager fm = getActivity().getFragmentManager();
			CreateProcessDialog dialog = (CreateProcessDialog) CreateProcessDialog.newInstance(null, false,mBean.taskNetId);
			dialog.setTargetFragment(TaskWithProcessFragment.this,
					REQUEST_PROCESS);
			dialog.show(fm, null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		processListView = (ListView) v.findViewById(R.id.list_task_process);
		Button emptyView = (Button) v
				.findViewById(R.id.btn_create_task_process);
		emptyView.setText("新建任务处理");
		emptyView.setOnClickListener(createBtnClickListener);
		processListView.setEmptyView(emptyView);
		mPanel.setTaskShow(mBean);
		mPanel.setViewEnable();
		if (mBean.taskState == 0) {
			getActivity().getActionBar().setTitle("处理任务");
		} else if (mBean.taskState == 2) {
			getActivity().getActionBar().setTitle("查看已完成任务");
		}
		processListView.setOnItemClickListener(processItemListener);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_PROCESS) {
				listProcess.add((TaskProcessBean) data
						.getParcelableExtra(CreateProcessDialog.PROCESSBEAN));
				if (processListView.getAdapter() == null) {
					mAdapter = new ProcessAdapter();
					processListView.setAdapter(mAdapter);
				} else {
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private OnItemClickListener processItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			FragmentManager fm = getActivity().getFragmentManager();
			CreateProcessDialog dialog = (CreateProcessDialog) CreateProcessDialog.newInstance(
					(TaskProcessBean) parent.getAdapter().getItem(position),
					true,mBean.taskNetId);
			dialog.show(fm, null);
		}
	};

	private OnClickListener createBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			FragmentManager fm = getActivity().getFragmentManager();
			CreateProcessDialog dialog = (CreateProcessDialog) CreateProcessDialog.newInstance(null, false,mBean.taskNetId);
			dialog.setTargetFragment(TaskWithProcessFragment.this,
					REQUEST_PROCESS);
			dialog.show(fm, null);
		}
	};

	private class ProcessAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listProcess.size();
		}

		@Override
		public Object getItem(int position) {
			return listProcess.get(position);
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
						R.layout.task_process_item_layout, null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			TaskProcessBean mBean = (TaskProcessBean) getItem(position);
			mHolder.processContent.setText(mBean.processContent);
			mHolder.processCreateTime.setText(Utils
					.formatTime(mBean.createTime));
			mHolder.taskState.setText(mBean.taskState + "");
			mHolder.taskNum.setText((position + 1) + "");
			return convertView;
		}
	}

	private class ViewHolder {
		public TextView processContent, processCreateTime, taskState, taskNum;

		public ViewHolder(View v) {
			processContent = (TextView) v.findViewById(R.id.tv_process_content);
			processCreateTime = (TextView) v
					.findViewById(R.id.tv_process_create_time);
			taskState = (TextView) v.findViewById(R.id.tv_task_state);
			taskNum = (TextView) v.findViewById(R.id.tv_task_num);
		}
	}

	public static class TaskProcessCursorLoader extends SQLiteCursorLoader {

		private int taskId;
		private OMUserDatabaseManager mManager;

		public TaskProcessCursorLoader(Context context, int taskId) {
			super(context);
			this.taskId = taskId;
			mManager = OMUserDatabaseManager.getInstance(context);
		}

		@Override
		protected Cursor loadCursor() {
			mManager.openDb(0);
			return mManager.queryProcessByTaskId(taskId);
		}

		@Override
		protected Cursor loadFromNet() {
			mManager.openDb(1);
			JSONObject param = new JSONObject();
			String result = "";
			try {
				param.put("CLID", "");
				param.put("RWID", taskId);
				result = NetOperating.getResultFromNet(getContext(), param, Urls.TASKPROCESSURL, "Operate=getAllRwclxx");
				Utility.handleTaskProcessResponse(mManager, result);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return loadCursor();
		}
	}

	private class TaskProcessCursorCallback implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new TaskProcessCursorLoader(getActivity(),
					args.getInt(TASKID));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			listProcess = new ArrayList<TaskProcessBean>();
			TaskProcessCursor cursor = (TaskProcessCursor) data;
			if (cursor != null && cursor.moveToFirst()) {
				Log.i("fanjishuo_____onLoadFinished", cursor.getCount()+"");
				do {
					listProcess.add(cursor.getTaskProcess());
				} while (cursor.moveToNext());
				mAdapter = new ProcessAdapter();
				if (processListView != null) {
					processListView.setAdapter(mAdapter);
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}

	}
}
