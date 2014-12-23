package com.htyd.fan.om.util.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.DateBean;
import com.htyd.fan.om.util.ui.TextViewWithBorder;

public class SelectDateDialog extends DialogFragment {

	private GridView monthGridView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.select_date_dialog, null);
		initView(v); 
		builder.setView(v);
		return builder.create();
	}

	private void initView(View v) {
		GridView weekGridView = (GridView) v.findViewById(R.id.grid_week_chinese);
		weekGridView.setAdapter(new MainPageGridAdapter());
		monthGridView = (GridView) v.findViewById(R.id.grid_attend_calendar);
	}
	
	private List<DateBean> getMonth(Calendar c) {
		List<DateBean> monthList;
		int currentMonth = c.get(Calendar.MONTH);
		int weeks = c.get(Calendar.WEEK_OF_MONTH);
		int currentDate = c.get(Calendar.DATE);
		int currentYear = c.get(Calendar.YEAR);
		c.add(Calendar.DATE, -currentDate);
		c.add(Calendar.DATE, currentDate);
		int week = 0;
		c.add(Calendar.DATE, -(c.get(Calendar.DAY_OF_WEEK) - 1));
		c.add(Calendar.DATE, -(weeks - 1) * 7);
		while (c.get(Calendar.MONTH) <= currentMonth
				&& c.get(Calendar.YEAR) == currentYear) {
			c.add(Calendar.DATE, 7);
			week++;
		}
		c.add(Calendar.DATE, -week * 7);
		int size = week * 7;
		monthList = new ArrayList<DateBean>(size);
		for (int i = 0; i < size; i++) {
			DateBean mBean = new DateBean();
			mBean.day = c.get(Calendar.DATE);
			if (c.get(Calendar.MONTH) != currentMonth) {
				mBean.state = 0;
			} else {
				mBean.state = 1;
			}
			monthList.add(mBean);
			c.add(Calendar.DATE, 1);
		}
		return monthList;
	}
	
	private class MainPageGridAdapter extends BaseAdapter {
		private String[] itemArray;
		private Resources r;

		public MainPageGridAdapter() {
			r = getActivity().getResources();
			itemArray = r.getStringArray(R.array.week_chinese);
		}

		@Override
		public int getCount() {
			return itemArray.length;
		}

		@Override
		public Object getItem(int position) {
			return itemArray[position];
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
						R.layout.week_item_layout, null);
				mHolder = new ViewHolder(convertView);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			mHolder.setText((String) getItem(position));
			return convertView;
		}
	}
	
	protected class ViewHolder {

		private TextViewWithBorder itemTextView;

		public ViewHolder(View v) {
			itemTextView = (TextViewWithBorder) v
					.findViewById(R.id.tv_item_weekday);
		}

		public void setText(String text) {
			itemTextView.setText(text);
		}
	}

}
