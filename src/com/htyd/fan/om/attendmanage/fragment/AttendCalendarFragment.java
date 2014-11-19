package com.htyd.fan.om.attendmanage.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.attendmanage.AttentManagerActivity;
import com.htyd.fan.om.attendmanage.adapter.AttendCalendarGridAdapter;
import com.htyd.fan.om.model.DateBean;
import com.htyd.fan.om.util.ui.TextViewWithBorder;

public class AttendCalendarFragment extends Fragment {

	private List<DateBean> monthList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		monthList = getMonth(Calendar.getInstance());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_main_page, container, false);
		intiView(v);
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void intiView(View v) {
		GridView mGridView = (GridView) v.findViewById(R.id.grid_week_chinese);
		GridView monthGridView = (GridView) v
				.findViewById(R.id.grid_attend_calendar);
		mGridView.setAdapter(new MainPageGridAdapter());
		monthGridView.setAdapter(new AttendCalendarGridAdapter(monthList,
				getActivity()));
		mGridView.setOnItemClickListener(new GridItemClickListener());
		TextView month = (TextView) v.findViewById(R.id.tv_month);
		month.setText(Calendar.getInstance().get(Calendar.YEAR) + "年"
				+ Calendar.getInstance().get(Calendar.MONTH) + "月");
	}

	private class GridItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				Intent i = new Intent(getActivity(),
						AttentManagerActivity.class);
				startActivity(i);
			}
		}
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

	private class ViewHolder {

		private TextViewWithBorder itemTextView;

		public ViewHolder(View v) {
			itemTextView = (TextViewWithBorder) v
					.findViewById(R.id.tv_item_weekday);
		}

		public void setText(String text) {
			itemTextView.setText(text);
		}
	}

	private List<DateBean> getMonth(Calendar c) {
		List<DateBean> monthList;
		int currentMonth = c.get(Calendar.MONTH);
		int weeks = c.get(Calendar.WEEK_OF_MONTH);
		int week = 0;
		c.add(Calendar.DATE, -(c.get(Calendar.DAY_OF_WEEK) - 1));
		c.add(Calendar.DATE, -(weeks - 1) * 7);
		while (c.get(Calendar.MONTH) <= currentMonth) {
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

}
