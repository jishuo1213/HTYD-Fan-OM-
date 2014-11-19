package com.htyd.fan.om.attendmanage.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.DateBean;

public class AttendCalendarGridAdapter extends BaseAdapter {

	private List<DateBean> monthList;
	private Context context;
	private DateBean mBean;

	public AttendCalendarGridAdapter(List<DateBean> attendList, Context context) {
		this.monthList = attendList;
		this.context = context;
	}

	@Override
	public int getCount() {

		return monthList.size();
	}

	@Override
	public Object getItem(int position) {

		return monthList.get(position);
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
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.week_item_layout, null);
			mHolder = new ViewHolder(convertView);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mBean = (DateBean) getItem(position);
		if (mBean != null) {
			mHolder.mTextView.setText(mBean.day + "");
			if (mBean.state == 0) {
				Log.i("fanjishuo____getView", mBean.state+""+mBean.day);
/*				mHolder.mTextView.setBackgroundColor(context.getResources()
						.getColor(R.color.red));*/
				mHolder.mTextView.setTextColor(context.getResources().getColor(R.color.red));
			}else{
				mHolder.mTextView.setTextColor(context.getResources().getColor(R.color.green));
			}
		} else {
			mHolder.mTextView.setText("");
		}
		return convertView;
	}

	private class ViewHolder {

		private TextView mTextView;

		public ViewHolder(View v) {
			mTextView = (TextView) v.findViewById(R.id.tv_item_weekday);
		}
	}

}
