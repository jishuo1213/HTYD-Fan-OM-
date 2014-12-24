package com.htyd.fan.om.util.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.htyd.fan.om.R;
import com.htyd.fan.om.model.DateBean;
import com.htyd.fan.om.util.ui.TextViewWithBorder;

public class SelectDateDialog extends DialogFragment {

	public static final String SELECTTIME = "selecttime";
	
	private ArrayList<DateBean> monthList;
	protected Calendar selectDay;
	private int crrentdayPos;
	private TextView month;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Calendar c = Calendar.getInstance();
		selectDay = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
		monthList = new ArrayList<DateBean>();
		getMonth(c,monthList);
		crrentdayPos = getCurrentDayPosititon(c);
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.select_date_dialog, null);
		initView(v); 
		builder.setView(v);
		builder.setPositiveButton("确定", dialogClickListener);
		builder.setNegativeButton("取消", null);
		return builder.create();
	}

	private void initView(View v) {
		GridView weekGridView = (GridView) v.findViewById(R.id.grid_week_chinese);
		weekGridView.setAdapter(new MainPageGridAdapter());
		GridView monthGridView = (GridView) v.findViewById(R.id.grid_attend_calendar);
		SelectDateAdapter mAdapter = new SelectDateAdapter(monthList, getActivity());
		monthGridView.setAdapter(mAdapter);
		mAdapter.setSelect(crrentdayPos);
		monthGridView.setOnItemClickListener(dateClickListener);
		month = (TextView) v.findViewById(R.id.tv_month);
		setMonth();
	}
	
	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(getTargetFragment() == null){
				return;
			}
			Intent data = new Intent();
			data.putExtra(SELECTTIME, selectDay.getTimeInMillis());
			getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
		}
	};
	
	private OnItemClickListener dateClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			DateBean mBean = (DateBean) parent.getAdapter().getItem(position);
			if (mBean.attendState != 0) {
				if (mBean.attendState == -1) {
					//selectDay.set(Calendar.MONTH,selectDay.get(Calendar.MONTH) - 1);
					selectDay.add(Calendar.MONTH, -1);
				} else {
					//selectDay.set(Calendar.MONTH,selectDay.get(Calendar.MONTH) + 1);
					selectDay.add(Calendar.MONTH, 1);
				}
				selectDay.set(Calendar.DATE, mBean.day);
				monthList.clear();
				getMonth(selectDay, monthList);
				((SelectDateAdapter) parent.getAdapter())
						.setSelect(getCurrentDayPosititon(selectDay));
				((SelectDateAdapter) parent.getAdapter())
						.notifyDataSetChanged();
				setMonth();
				return;
			}
			selectDay.set(Calendar.DATE, mBean.day);
			if (parent.getTag() != null) {
				((View) parent.getTag()).setBackgroundColor(getActivity()
						.getResources().getColor(R.color.activity_bg_color));
			}
			parent.setTag(view);
			view.setBackgroundColor(getActivity().getResources().getColor(
					R.color.orange));
			((SelectDateAdapter)parent.getAdapter()).setSelect(position);
		}
	};
	
	private void getMonth(Calendar calendar,ArrayList<DateBean> monthList) {
		Calendar c = new GregorianCalendar(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
		int currentMonth = c.get(Calendar.MONTH);
		int weeks = c.get(Calendar.WEEK_OF_MONTH);
		int currentYear = c.get(Calendar.YEAR);
		int week = 0;
		c.add(Calendar.DATE, -(c.get(Calendar.DAY_OF_WEEK) - 1));
		c.add(Calendar.DATE, -(weeks - 1) * 7);
		if(c.get(Calendar.YEAR) < currentYear){
			while(c.get(Calendar.MONTH) % 11 <= currentMonth){
				c.add(Calendar.DATE, 7);
				week++;
			}
		} else {
			while (c.get(Calendar.MONTH) <= currentMonth && c.get(Calendar.YEAR) == currentYear) {
				c.add(Calendar.DATE, 7);
				week++;
			}
		}
		if(c.get(Calendar.DATE) == 1){
			c.add(Calendar.DATE, -week * 7);
			week++;
		}else{
			c.add(Calendar.DATE, -week * 7);
		}
		if(c.get(Calendar.DATE) == 1){
			c.add(Calendar.DATE, -7);
			week++;
		}
		int size = week * 7;
		for (int i = 0; i < size; i++) {
			DateBean mBean = new DateBean();
			mBean.day = c.get(Calendar.DATE);
			if (c.get(Calendar.MONTH) == (currentMonth + 11) % 12) {
				mBean.state = 0;
				mBean.attendState = -1;
			} else if (c.get(Calendar.MONTH) == (currentMonth + 13) % 12) {
				mBean.state = 0;
				mBean.attendState = 1;
			} else {
				mBean.state = 1;
				mBean.attendState = 0;
			}
			monthList.add(mBean);
			c.add(Calendar.DATE, 1);
		}
	}
	
	protected void setMonth() {
		month.setText(selectDay.get(Calendar.YEAR) + "年"
				+ (selectDay.get(Calendar.MONTH) + 1) + "月");
	}

	private int getCurrentDayPosititon(Calendar c){
		int currentDate = c.get(Calendar.DATE);
		c.add(Calendar.DATE, -currentDate);
		int firstDayPosition = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DATE, currentDate);
		return firstDayPosition + currentDate - 1;
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
