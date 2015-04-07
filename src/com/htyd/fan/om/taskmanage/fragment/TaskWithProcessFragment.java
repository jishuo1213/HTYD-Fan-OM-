package com.htyd.fan.om.taskmanage.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.taskmanage.TaskViewPanel;
import com.htyd.fan.om.taskmanage.adapter.ProcessAdapter;
import com.htyd.fan.om.taskmanage.netthread.DeleteProcessThread;
import com.htyd.fan.om.taskmanage.netthread.DeleteProcessThread.DeleteProcessListener;
import com.htyd.fan.om.util.base.ThreadPool;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskProcessCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.fragment.UploadFileDialog;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.UItoolKit;

public class TaskWithProcessFragment extends Fragment  implements DeleteProcessListener{

	private static final String SELECTTASK = "selecttask";
	private static final String TASKNETID = "taskid";
	private static final int REQUEST_PROCESS = 0x01;
	private static final int REQUEST_PROCESS_SYNC = 0x02;
	private static final int REQUEST_EDIT_PROCESS = 0x03;
	private static final String TASKLOCALID = "tasklocalid";

	private TaskViewPanel mPanel;
	protected TaskDetailBean mBean;
	protected ListView processListView;
	protected ArrayList<TaskProcessBean> listProcess;
	protected ProcessAdapter mAdapter;
	private TaskProcessCursorCallback mCallback;
	private LoaderManager mManager;
	private PullToRefreshListView mPullRefreshListView;
	protected Handler handler;

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
		handler = new Handler();  
		Bundle args = new Bundle();
		args.putInt(TASKNETID, mBean.taskNetId);
		args.putInt(TASKLOCALID, mBean.taskLocalId);
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.process_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = info.position;
		TaskProcessBean mBean =  (TaskProcessBean) processListView.getAdapter().getItem(position);
		switch (item.getItemId()) {
		case R.id.menu_delete_task_process:
			if(mBean.isSyncToServer == 0){
				OMUserDatabaseManager.getInstance(getActivity()).deleteSingleTaskProcess(mBean);
				listProcess.remove(mBean);
				mAdapter.notifyDataSetChanged();
				return true;
			}
			DeleteProcessThread thread = new DeleteProcessThread(handler, getActivity(), mBean);
			thread.setListener(this);
			ThreadPool.runMethod(thread);
			return true;
		case R.id.menu_edit_task_process:
			DialogFragment editDialog = EditProcessDialog.newInstance(mBean);
			editDialog.setTargetFragment(TaskWithProcessFragment.this, REQUEST_EDIT_PROCESS);
			editDialog.show(getFragmentManager(), null);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_create_task_process:
			if(mBean.taskState == 2){
				UItoolKit.showToastShort(getActivity(), "任务已经完成，不能继续创建处理项");
				return true;
			}
			FragmentManager fm = getActivity().getFragmentManager();
			CreateProcessDialog dialog = (CreateProcessDialog) CreateProcessDialog.newInstance(null, false,mBean.taskNetId,mBean.taskLocalId);
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
		mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.list_task_process);
		processListView = mPullRefreshListView.getRefreshableView();
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				new RefreshProcessTask().execute(mBean.taskNetId);
			}
		});
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
		mPanel.taskAccessory.setOnClickListener(createBtnClickListener);
		processListView.setOnItemClickListener(processItemListener);
		registerForContextMenu(processListView);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_PROCESS) {
				TaskProcessBean proBean = (TaskProcessBean) data.getParcelableExtra(CreateProcessDialog.PROCESSBEAN);
				listProcess.add(proBean);
				if (processListView.getAdapter() == null) {
					mAdapter = new ProcessAdapter(listProcess,getActivity());
					processListView.setAdapter(mAdapter);
				} else {
					mAdapter.notifyDataSetChanged();
				}
			}else if(requestCode == REQUEST_PROCESS_SYNC){
				mAdapter.notifyDataSetChanged();
			}else if(requestCode == REQUEST_EDIT_PROCESS){
				mAdapter.notifyDataSetChanged();
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
					true,mBean.taskNetId,mBean.taskLocalId);
			dialog.setTargetFragment(TaskWithProcessFragment.this, REQUEST_PROCESS_SYNC);
			dialog.show(fm, null);
		}
	};

	private OnClickListener createBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_create_task_process:
				if (mBean.taskState == 2) {
					UItoolKit.showToastShort(getActivity(), "任务已经完成，不能继续创建处理项");
					return;
				}
				FragmentManager fm = getActivity().getFragmentManager();
				CreateProcessDialog dialog = (CreateProcessDialog) CreateProcessDialog
						.newInstance(null, false, mBean.taskNetId,
								mBean.taskLocalId);
				dialog.setTargetFragment(TaskWithProcessFragment.this,
						REQUEST_PROCESS);
				dialog.show(fm, null);
				break;
			case R.id.tv_task_accessory:
				UploadFileDialog fileDialog = (UploadFileDialog) UploadFileDialog
						.newInstance(mBean.taskNetId, mBean.taskTitle, false,
								mBean.taskLocalId);
				fileDialog.show(getFragmentManager(), null);
				break;
			}
		}
	};

	public static class TaskProcessCursorLoader extends SQLiteCursorLoader {

		private int taskNetId,taskLocalId;
		private OMUserDatabaseManager mManager;

		public TaskProcessCursorLoader(Context context, int taskNetId,int taskLocalId) {
			super(context);
			this.taskNetId = taskNetId;
			this.taskLocalId = taskLocalId;
			mManager = OMUserDatabaseManager.getInstance(context);
		}

		@Override
		protected Cursor loadCursor() {
			mManager.openDb(0);
			if (taskNetId > 0) {
				return mManager.queryProcessByTaskNetId(taskNetId);
			} else {
				return mManager.queryProcessByTaskLocalId(taskLocalId);
			}
		}

		@Override
		protected Cursor loadFromNet() {
			JSONObject param = new JSONObject();
			String result = "";
			try {
				param.put("CLID", "");
				param.put("RWID", taskNetId);
				result = NetOperating.getResultFromNet(getContext(), param, Urls.TASKPROCESSURL, "Operate=getAllRwclxx");
				Utility.handleTaskProcessResponse(mManager, result,taskLocalId);
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
					args.getInt(TASKNETID),args.getInt(TASKLOCALID));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (listProcess == null)
				listProcess = new ArrayList<TaskProcessBean>();
			else {
				listProcess.clear();
			}
			TaskProcessCursor cursor = (TaskProcessCursor) data;
			if (cursor != null && cursor.moveToFirst()) {
				do {
					listProcess.add(cursor.getTaskProcess());
				} while (cursor.moveToNext());
				mAdapter = new ProcessAdapter(listProcess,getActivity());
				if (processListView != null) {
					processListView.setAdapter(mAdapter);
				}
			} else {
				if(processListView.getAdapter() != null){
					HeaderViewListAdapter ha = (HeaderViewListAdapter) processListView.getAdapter();
					ProcessAdapter adapter = (ProcessAdapter)ha.getWrappedAdapter();
					adapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}
	
	private class RefreshProcessTask extends AsyncTask<Integer, Void, Boolean>{
		
		private OMUserDatabaseManager mDbManger;
		
		public RefreshProcessTask() {
			mDbManger = OMUserDatabaseManager.getInstance(getActivity());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Bundle args = new Bundle();
				args.putInt(TASKNETID, mBean.taskNetId);
				mManager.restartLoader(0, args, mCallback);
				UItoolKit.showToastShort(getActivity(), "刷新成功");
			} else {
				UItoolKit.showToastShort(getActivity(), "刷新失败");
			}
			mPullRefreshListView.onRefreshComplete();
			cancel(false);
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			String result = "";
			JSONObject param = new JSONObject();
			try {
				param.put("CLID", "");
				param.put("RWID", params[0]);
				result = NetOperating.getResultFromNet(getActivity(), param, Urls.TASKPROCESSURL, "Operate=getAllRwclxx");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			mDbManger.refreshTaskProcess(mBean.taskLocalId);
			try {
				return Utility.handleTaskProcessResponse(mDbManger, result,mBean.taskLocalId);
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	@Override
	public void onDeleteSuccess(TaskProcessBean mBean) {
		listProcess.remove(mBean);
		mAdapter.notifyDataSetChanged();
		UItoolKit.showToastShort(getActivity(), "删除任务处理成功");
	}
}
