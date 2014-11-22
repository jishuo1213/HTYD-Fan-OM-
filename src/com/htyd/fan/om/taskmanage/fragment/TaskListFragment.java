package com.htyd.fan.om.taskmanage.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.db.OMUserDatabaseHelper.TaskCursor;
import com.htyd.fan.om.util.db.OMUserDatabaseManager;
import com.htyd.fan.om.util.loaders.SQLiteCursorLoader;
import com.htyd.fan.om.util.ui.CustomChooserView.OnItemChooserListener;
import com.htyd.fan.om.util.ui.UItoolKit;

public class TaskListFragment extends Fragment implements OnItemChooserListener {

	private static final String TASKSTATE = "taskstate";
	private static final String INPROCESSINGTASK = "inprocessingtask";
	private static final String BERECEIVE = "bereceive";
	private static final String COMPLETED = "completed";
	private HashMap<String,List<TaskDetailBean>> taskMap;
	private ListView mListView;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		taskMap = new HashMap<String, List<TaskDetailBean>>(); 
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tasklist_layout, container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
	}

	@Override
	public void onItemChooser(int position) {
		UItoolKit.showToastShort(getActivity(), "position"+position);
	}
	
	private class TaskAdapter extends BaseAdapter{
		
		private int type;//0 在处理 1 待领取 2已完成
		
		
		
		public TaskAdapter(int type) {
			super();
			this.type = type;
		}

		@Override
		public int getCount() {
			switch(type){
			case 0:
				return taskMap.get(INPROCESSINGTASK).size();
			case 1:
				return taskMap.get(BERECEIVE).size();
			case 2:
				return taskMap.get(COMPLETED).size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			switch(type){
			case 0:
				return taskMap.get(INPROCESSINGTASK).size();
			case 1:
				return taskMap.get(BERECEIVE).size();
			case 2:
				return taskMap.get(COMPLETED).size();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			return null;
		}
		
	}
	
	
	public static class TaskCursorLoader extends SQLiteCursorLoader{

		private int taskState;
		private OMUserDatabaseManager mManager;
		
		public TaskCursorLoader(Context context,int state) {
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
			return loadCursor();
		}
	}
	
	private class TaskCursorCallback  implements LoaderCallbacks<Cursor>{

		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new TaskCursorLoader(getActivity(), args.getInt(TASKSTATE));
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			List<TaskDetailBean> mList = new ArrayList<TaskDetailBean>();
			if(data != null && data.moveToFirst()){
				TaskCursor taskCursor = (TaskCursor) data;
				mList.add(taskCursor.getTask());
			}
			for(TaskDetailBean mBean:mList){
				switch(mBean.taskState){
				case 0://在处理任务
					taskMap.get(INPROCESSINGTASK).add(mBean);
					break;
				case 1://待领取任务
					taskMap.get(BERECEIVE).add(mBean);
					break;
				case 2://已完成任务
					taskMap.get(COMPLETED).add(mBean);
					break;
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
		
	}
}
