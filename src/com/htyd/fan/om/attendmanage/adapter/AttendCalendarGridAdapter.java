package com.htyd.fan.om.attendmanage.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.AttendBean;

public class AttendCalendarGridAdapter extends BaseAdapter {

	
	private List<AttendBean> attendList;
	private Context context;
	
	public AttendCalendarGridAdapter(List<AttendBean> attendList,Context context) {
		this.attendList = attendList;
		this.context = context;
	}

	@Override
	public int getCount() {

		return attendList.size();
	}

	@Override
	public Object getItem(int position) {

		return attendList.get(position);
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
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.week_item_layout, null);
			mHolder = new ViewHolder(convertView);
			convertView.setTag(mHolder);
		}else{
			mHolder = (ViewHolder) convertView.getTag();
		}
		AttendBean mBean = (AttendBean) getItem(position);
		return null;
	}

	private class ViewHolder {
		
		private TextView mTextView;
		
		
		public ViewHolder(View v) {
			mTextView = (TextView) v.findViewById(R.id.tv_item_weekday);
		}
		
	}
	
}
