package com.htyd.fan.om.util.fragment;

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
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.htyd.fan.om.R;

public class DateTimePickerDialog extends DialogFragment {

	public static final String EXTRATIME = "extratime";

	protected Calendar mDate;
	protected long currentTime;
	private TimeAdapter yearAdapter, monthAdapter, dayAdapter, hourAdapter,
			minuteAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.date_picker_layout, null);
		initView(v);
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(v);
		builder.setTitle("请选择开始时间");
		builder.setPositiveButton("确定", dialogClickListener);
		return builder.create();
	}

	private void initView(View v) {
		/*
		 * TextView today, tomorrow, afterTomorrow, am, pm; today = (TextView)
		 * v.findViewById(R.id.tv_today); tomorrow = (TextView)
		 * v.findViewById(R.id.tv_tomorrow); afterTomorrow = (TextView)
		 * v.findViewById(R.id.tv_after_tomorrow); am = (TextView)
		 * v.findViewById(R.id.tv_am); pm = (TextView)
		 * v.findViewById(R.id.tv_pm);
		 */
		/*
		 * today.setOnClickListener(selectDateListener);
		 * tomorrow.setOnClickListener(selectDateListener);
		 * afterTomorrow.setOnClickListener(selectDateListener);
		 * am.setOnClickListener(selectDateListener);
		 * pm.setOnClickListener(selectDateListener);
		 */
		Spinner year, month, day, hour, minute;
		year = (Spinner) v.findViewById(R.id.spinner_year);
		month = (Spinner) v.findViewById(R.id.spinner_month);
		day = (Spinner) v.findViewById(R.id.spinner_day);
		hour = (Spinner) v.findViewById(R.id.spinner_hour);
		minute = (Spinner) v.findViewById(R.id.spinner_minute);
		year.setAdapter(yearAdapter);
		month.setAdapter(monthAdapter);
		day.setAdapter(dayAdapter);
		hour.setAdapter(hourAdapter);
		minute.setAdapter(minuteAdapter);
		month.setSelection(mDate.get(Calendar.MONTH));
		day.setSelection(mDate.get(Calendar.DATE));
		hour.setSelection(mDate.get(Calendar.HOUR));
		minute.setSelection(mDate.get(Calendar.MINUTE));
		year.setOnItemSelectedListener(timeSeclectListener);
		month.setOnItemSelectedListener(timeSeclectListener);
		day.setOnItemSelectedListener(timeSeclectListener);
		hour.setOnItemSelectedListener(timeSeclectListener);
		minute.setOnItemSelectedListener(timeSeclectListener);
	}

	private void initData() {
		Calendar c = Calendar.getInstance();
		mDate = new GregorianCalendar(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH), c.get(Calendar.DATE),
				c.get(Calendar.HOUR), c.get(Calendar.MINUTE));
		currentTime =mDate.getTimeInMillis();
		yearAdapter = new TimeAdapter(2, c.get(Calendar.YEAR));
		monthAdapter = new TimeAdapter(12, 1);
		dayAdapter = new TimeAdapter(c.getActualMaximum(Calendar.DATE), 1);
		hourAdapter = new TimeAdapter(24, 0);
		minuteAdapter = new TimeAdapter(60, 0);
	}

	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				if (mDate.getTimeInMillis() < currentTime) {
					mDate.setTimeInMillis(currentTime);
				}
				sendResult();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				break;
			}
		}
	};

	private OnItemSelectedListener timeSeclectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch(parent.getId()){
			case R.id.spinner_year:
			case R.id.spinner_month:
			case R.id.spinner_day:
			case R.id.spinner_hour:
			case R.id.spinner_minute:
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};
	
	/*
	 * private OnClickListener selectDateListener = new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { switch (v.getId()) { case
	 * R.id.tv_today: break; case R.id.tv_tomorrow: break; case
	 * R.id.tv_after_tomorrow: break; case R.id.tv_am: break; case R.id.tv_pm:
	 * break; } } };
	 */

	protected void sendResult() {
		if (getTargetFragment() == null) {
			return;
		}
		Intent i = new Intent();
		i.putExtra(EXTRATIME, mDate.getTime());
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				Activity.RESULT_OK, i);
	}

	private class TimeAdapter extends BaseAdapter {

		private int count;
		private int start;

		public TimeAdapter(int count, int start) {
			this.count = count;
			this.start = start;
		}

		@Override
		public int getCount() {
			return this.count;
		}

		@Override
		public Object getItem(int position) {
			return start + position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView mTextView;
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.spinner_item_layout, null);
			}
			mTextView = (TextView) convertView;
			mTextView.setText((int)getItem(position)+"");
			return convertView;
		}
	}

}
