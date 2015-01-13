package com.htyd.fan.om.taskmanage.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.taskmanage.adapter.TaskAdapter;
import com.htyd.fan.om.util.base.Preferences;
import com.htyd.fan.om.util.base.Utils;
import com.htyd.fan.om.util.fragment.SelectDateDialog;
import com.htyd.fan.om.util.https.NetOperating;
import com.htyd.fan.om.util.https.Urls;
import com.htyd.fan.om.util.https.Utility;
import com.htyd.fan.om.util.ui.UItoolKit;

public class QueryTaskFragment extends Fragment {

	private static final int REQUESTSTARTTIME = 0x05;
	private static final int REQUESTENDTIME = 0x06;
	
	private TextView taskCreateStartTime,taskCreateEndTime;
	protected ListView listView;
	protected ArrayList<TaskDetailBean> taskList = new ArrayList<TaskDetailBean>();
	private long startTime;
	private long endTime;
	private TextView queryThreeDays,queryOneWeek,queryOneMonth;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			loadData(taskList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.query_task_layout, container, false);
		initView(v);
		return v;
	}

	private void initView(View v) {
		taskCreateStartTime = (TextView) v.findViewById(R.id.tv_task_create_start_time);
		taskCreateEndTime = (TextView) v.findViewById(R.id.tv_task_create_end_time);
		taskCreateStartTime.setOnClickListener(queryTaskClickListener);
		taskCreateEndTime.setOnClickListener(queryTaskClickListener);
		queryThreeDays = (TextView) v.findViewById(R.id.tv_task_three_days);
		queryOneWeek = (TextView) v.findViewById(R.id.tv_task_one_week);
		queryOneMonth = (TextView) v.findViewById(R.id.tv_task_one_month);
		queryThreeDays.setOnClickListener(queryTaskClickListener);
		queryOneWeek.setOnClickListener(queryTaskClickListener);
		queryOneMonth.setOnClickListener(queryTaskClickListener);
		listView = (ListView) v.findViewById(R.id.list_query_task);
		if(taskList.size() > 0){
			listView.setAdapter(new TaskAdapter(taskList, getActivity()));
		}
		listView.setOnItemClickListener(queryTaskItemClickListener);
		getActivity().getActionBar().setTitle("查询任务");
	}
	
	private OnItemClickListener queryTaskItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TaskDetailBean mBean = (TaskDetailBean) parent.getAdapter().getItem(position);
			Fragment fragment = ViewQueryTaskFragment.newInstance(mBean);
			getFragmentManager().beginTransaction()
					.replace(R.id.fragmentContainer, fragment)
					.addToBackStack(null).commit();
		}
	};
	
	private OnClickListener queryTaskClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.tv_task_create_start_time:
				SelectDateDialog sdialog = new SelectDateDialog();
				sdialog.setTargetFragment(QueryTaskFragment.this, REQUESTSTARTTIME);
				sdialog.show(getFragmentManager(), null);
				break;
			case R.id.tv_task_create_end_time:
				SelectDateDialog edialog = new SelectDateDialog();
				edialog.setTargetFragment(QueryTaskFragment.this, REQUESTENDTIME);
				edialog.show(getFragmentManager(), null);
				break;
			case R.id.tv_task_three_days:
				queryClick(3);
				break;
			case R.id.tv_task_one_week:
				queryClick(7);
				break;
			case R.id.tv_task_one_month:
				queryClick(-1);
				break;
			}
		}
	};
	
	protected void queryClick(int day) {
		Calendar c = Calendar.getInstance();
		if(day == -1){
			day = c.get(Calendar.DATE);
		}
		endTime = c.getTimeInMillis();
		
		c.add(Calendar.DATE, -day);
		startTime = c.getTimeInMillis();
		taskCreateStartTime.setText(Utils.formatTime(startTime, "yyyy-MM-dd"));
		taskCreateEndTime.setText(Utils.formatTime(endTime, "yyyy-MM-dd"));
		new QueryTaskFromNet().execute(
				Utils.formatTime(startTime, "yyyy-MM-dd"),
				Utils.formatTime(endTime, "yyyy-MM-dd"));
	}
	
	public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == REQUESTSTARTTIME){
				startTime = data.getLongExtra(SelectDateDialog.SELECTTIME, 0);
				taskCreateStartTime.setText(Utils.formatTime(startTime, "yyyy-MM-dd"));
			}else if(requestCode == REQUESTENDTIME){
				endTime = data.getLongExtra(SelectDateDialog.SELECTTIME, 0);
				if(endTime < startTime){
					endTime = startTime;
				}
				taskCreateEndTime.setText(Utils.formatTime(data.getLongExtra(SelectDateDialog.SELECTTIME, 0), "yyyy-MM-dd"));
			}
		}
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.query_task, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_query_task:
			if(startTime == 0 && endTime == 0){
				UItoolKit.showToastShort(getActivity(), "先选择时间段");
				return true;
			}
			if(startTime == 0){
				startTime  = endTime;
			}
			if(endTime == 0){
				endTime = startTime;
			}
			new QueryTaskFromNet().execute(
					Utils.formatTime(startTime, "yyyy-MM-dd"),
					Utils.formatTime(endTime, "yyyy-MM-dd"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class QueryTaskFromNet extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			JSONObject param = new JSONObject();
			try {
				param.put("RWBT", "");
				param.put("TXSJ", "");
				param.put("RWID", "");
				param.put("RWZT", "");
				param.put("RWZT", "");
				param.put("TXSJ_BEGIN",params[0]);
				param.put("TXSJ_END", params[1]);
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
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return result;
			}
			saveResult(result);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result.length() == 0){
				UItoolKit.showToastShort(getActivity(), "未查询到相关数据");
				return;
			}
			try {
				taskList.clear();
				Utility.handleTaskResponse(result, taskList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(listView.getAdapter() == null){
				listView.setAdapter(new TaskAdapter(taskList,getActivity()));
			}
			((TaskAdapter)listView.getAdapter()).notifyDataSetChanged();
			cancel(false);
		}
	}

	public void saveResult(String result) {
		BufferedWriter writer = null;
		try {
			JSONObject json = new JSONObject(result);
			result = json.getString("Rows");
			OutputStream out = getActivity().openFileOutput(
					Preferences.getUserId(getActivity()) + ".xml",
					Context.MODE_PRIVATE);
			writer = new BufferedWriter(new OutputStreamWriter(out));
			writer.write(result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadData(ArrayList<TaskDetailBean> taskList) throws FileNotFoundException{
		BufferedReader reader = null;
		InputStream in = getActivity().openFileInput(Preferences.getUserId(getActivity()) + ".xml");
		reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			if (sb.toString().length() > 0) {
				JSONArray array = (JSONArray) new JSONTokener(sb.toString())
						.nextValue();
				for (int i = 0; i < array.length(); i++) {
					TaskDetailBean mBean = new TaskDetailBean();
					mBean.setFromJson(array.getJSONObject(i));
					taskList.add(mBean);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
