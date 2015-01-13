package com.htyd.fan.om.taskmanage.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.htyd.fan.om.R;
import com.htyd.fan.om.model.AffiliatedFileBean;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.taskmanage.TaskViewPanel;
import com.htyd.fan.om.taskmanage.adapter.ProcessAdapter;
import com.htyd.fan.om.util.fragment.UploadFileDialog;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.ui.UItoolKit;

public class ViewQueryTaskFragment extends Fragment {

	private static final String VIEWTASK = "viewtask";
	
	protected TaskViewPanel mPanel;
	private TaskDetailBean mBean;
	protected ArrayList<TaskProcessBean> processList;
	protected ListView processListView;
	protected ArrayList<AffiliatedFileBean> accessoryList;
	private boolean isLoadFinish;
	
	public static Fragment newInstance(TaskDetailBean mBean){
		ViewQueryTaskFragment fragment = new ViewQueryTaskFragment();
		Bundle args = new Bundle();
		args.putParcelable(VIEWTASK, mBean);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBean = (TaskDetailBean) getArguments().get(VIEWTASK);
		processList = new ArrayList<TaskProcessBean>();
		isLoadFinish = false;
		new GetProcessTask().execute(mBean.taskNetId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.task_with_process_layout, container, false);
		initView(v);
		return v;
	}
	
	@Override
	public void onDestroy() {
		if (processList != null)
			processList.clear();
		if (accessoryList != null && accessoryList .size() > 0) {
			Iterator<AffiliatedFileBean> it = accessoryList.iterator();
			while (it.hasNext()) {
				deleteFile(it.next().filePath);
			}
			accessoryList.clear();
		}
		super.onDestroy();
	}
	
	private void deleteFile(String filePath) {
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
	}

	private void initView(View v) {
		PullToRefreshListView pullView =  (PullToRefreshListView) v.findViewById(R.id.list_task_process);
		processListView = pullView.getRefreshableView();
		if(processList.size() > 0){
			processListView.setAdapter(new ProcessAdapter(processList, getActivity()));
		}
		mPanel = new TaskViewPanel(v);
		mPanel.setTaskShow(mBean);
		mPanel.setViewEnable();
		mPanel.taskAccessory.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!isLoadFinish){
					UItoolKit.showToastShort(getActivity(), "数据还未加载完成");
					return;
				}
				DialogFragment dialog = UploadFileDialog.newInstance(accessoryList, mBean.taskNetId, mBean.taskTitle,true);
				dialog.show(getFragmentManager(), null);
			}
		});
	}
	
	
	private class GetProcessTask extends AsyncTask<Integer, Void, String>{
		
		private int taskId;
		
		@Override
		protected void onPostExecute(String result) {
			if(result.length() == 0){
				return;
			}
			try {
				Utility.handleTaskProcessResponse(result,processList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(processList.size() > 0){
				if(processListView != null)
					processListView.setAdapter(new ProcessAdapter(processList, getActivity()));
			}
			new GetAccessoryTask().execute(taskId);
			cancel(false);
		}

		@Override
		protected String doInBackground(Integer... params) {
			String result = "";
			taskId = params[0];
			JSONObject param = new JSONObject();
			try {
				param.put("CLID", "");
				param.put("RWID", params[0]);
				result = NetOperating.getResultFromNet(getActivity(), param, Urls.TASKPROCESSURL, "Operate=getAllRwclxx");
			} catch (JSONException e) {
				e.printStackTrace();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return result;
			}
			return result;
		}
	}
	
	private class GetAccessoryTask extends AsyncTask<Integer, Void, String>{

		private int taskId;
		
		@Override
		protected void onPostExecute(String result) {
			isLoadFinish = true;
			if(result.length() == 0){
				return;
			}
			if(accessoryList == null){
				accessoryList = new ArrayList<AffiliatedFileBean>();
			}
			try {
				Utility.handleAccessory(accessoryList, result, taskId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			cancel(false);
		}

		@Override
		protected String doInBackground(Integer... params) {
			JSONObject param = new JSONObject();
			String result = "";
			taskId = params[0];
			try {
				param.put("JLID", params[0]);
				result = NetOperating.getResultFromNet(getActivity(), param,
						Urls.FILE, "Operate=getWjdz");
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
	}
}
