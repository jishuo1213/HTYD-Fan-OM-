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
import com.htyd.fan.om.model.TaskProcessBean;
import com.htyd.fan.om.util.base.Utils;

public class ProcessAdapter extends BaseAdapter {

	private ArrayList<TaskProcessBean> listProcess;
	private Context context;
	
	public ProcessAdapter(ArrayList<TaskProcessBean> listProcess,Context context) {
		super();
		this.listProcess = listProcess;
		this.context = context;
	}

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
			convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
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
/*		if (mBean.taskState == 0) {
			mHolder.taskState.setText("在处理");
		} else {
			mHolder.taskState.setText("已完成");
		}*/
		if (mBean.isSyncToServer == 0) {
			mHolder.processSyncState.setText("未同步");
		} else {
			mHolder.processSyncState.setText("");
		}
		mHolder.taskNum.setText((position + 1) + "");
		return convertView;
	}
	
	private class ViewHolder {
		public TextView processContent, processCreateTime, taskNum,processSyncState;

		public ViewHolder(View v) {
			processContent = (TextView) v.findViewById(R.id.tv_process_content);
			processCreateTime = (TextView) v
					.findViewById(R.id.tv_process_create_time);
//			taskState = (TextView) v.findViewById(R.id.tv_task_state);
			processSyncState = (TextView) v.findViewById(R.id.tv_process_sync_state);
			taskNum = (TextView) v.findViewById(R.id.tv_task_num);
		}
	}
}
