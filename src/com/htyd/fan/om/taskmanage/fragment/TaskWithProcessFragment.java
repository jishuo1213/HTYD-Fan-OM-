package com.htyd.fan.om.taskmanage.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.taskmanage.fragment.ReceiveTaskFragment.TaskViewPanel;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskProcessCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;

public class TaskWithProcessFragment extends Fragment {

	private static final String SELECTTASK = "selecttask";
	private static final String TASKID = "taskid";
	
	private TaskViewPanel mPanel;
	protected TaskDetailBean mBean;
	protected ListView processList;
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
		Bundle args = new Bundle();
		args.putInt(TASKID, mBean.taskId);
		mManager.initLoader(0, args, mCallback);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.task_with_process_layout, container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		mPanel = new TaskViewPanel(v);
		processList = (ListView) v.findViewById(R.id.list_task_process);
		Button emptyView = (Button) v.findViewById(R.id.btn_create_task_process);
		emptyView.setText("新建任务处理");
		emptyView.setOnClickListener(createBtnClickListener);
		processList.setEmptyView(emptyView);
		mPanel.setTaskShow(mBean);
		mPanel.setViewEnable();
		if(mBean.taskState == 0){
			getActivity().getActionBar().setTitle("处理任务");
		}else if(mBean.taskState == 2){
			getActivity().getActionBar().setTitle("查看已完成任务");
		}
	}

	private OnClickListener createBtnClickListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};
	
	private class ProcessAdapter extends BaseAdapter{

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
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.task_process_item_layout,null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			}else{
				mHolder = (ViewHolder) convertView.getTag();
			}
			TaskProcessBean mBean = (TaskProcessBean) getItem(position);
			mHolder.processContent.setText(mBean.processContent);
			mHolder.processCreateTime.setText(Utils.formatTime(mBean.createTime));
			mHolder.taskState.setText(mBean.taskState);
			return convertView;
		}
	}
	
	private class  ViewHolder {
		public TextView processContent,processCreateTime,taskState;

		public ViewHolder(View v) {
			processContent = (TextView) v.findViewById(R.id.tv_process_content);
			processCreateTime = (TextView) v.findViewById(R.id.tv_process_create_time);
			taskState = (TextView) v.findViewById(R.id.tv_task_state);
		}
	}
	
	public static class TaskProcessCursorLoader extends SQLiteCursorLoader{

		private int taskId;
		private OMUserDatabaseManager mManager;
		
		public TaskProcessCursorLoader(Context context,int taskId) {
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
			return loadCursor();
		}
	}
	
	private class TaskProcessCursorCallback implements LoaderCallbacks<Cursor>{

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new TaskProcessCursorLoader(getActivity(),args.getInt(TASKID));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			listProcess = new ArrayList<TaskProcessBean>();
			TaskProcessCursor cursor = (TaskProcessCursor) data;
			if(cursor != null && cursor.moveToFirst()){
				do{
					listProcess.add(cursor.getTaskProcess());
				}while(cursor.moveToNext());
				mAdapter = new ProcessAdapter();
				if(processList != null){
					processList.setAdapter(mAdapter);
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
		
	}
}
