package com.htyd.fan.om.taskmanage.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.TaskDetailBean;
import com.htyd.fan.om.util.base.Utils;

public class TaskAdapter extends BaseAdapter {

	private ArrayList<TaskDetailBean> taskList;
	private Context context;

	public TaskAdapter(ArrayList<TaskDetailBean> taskList,Context context) {
		super();
		this.taskList = taskList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return taskList.size();
	}

	@Override
	public Object getItem(int position) {
		return taskList.get(position);
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
			convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
					R.layout.task_item_layout, null);
			mHolder = new ViewHolder(convertView);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		TaskDetailBean mBean = (TaskDetailBean) getItem(position);
		mHolder.taskDescrption.setText(mBean.taskTitle);
		mHolder.taskCreateTime.setText(Utils.formatTime(mBean.saveTime));
		if (mBean.taskState == 0) {
			mHolder.taskState.setText("在处理");
		} else {
			mHolder.taskState.setText("已完成");
		}
		return convertView;
	}
	
	private class ViewHolder {
		public TextView taskDescrption, taskCreateTime, taskState;
		
		public ViewHolder(View v) {
			taskDescrption = (TextView) v.findViewById(R.id.tv_task_descrption);
			taskCreateTime = (TextView) v.findViewById(R.id.tv_task_createtime);
			taskState = (TextView) v.findViewById(R.id.tv_task_state);
		}
	}
}
